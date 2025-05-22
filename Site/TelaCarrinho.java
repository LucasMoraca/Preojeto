package Site;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TelaCarrinho extends JFrame {

    private JList<ItemCarrinho> listaItens;
    private DefaultListModel<ItemCarrinho> listModel;
    private JButton removerItemButton;
    private JButton alterarQtdButton;
    private JLabel totalLabel;
    private JTextArea enderecoTextArea;
    private JRadioButton pixRadioButton;
    private JRadioButton creditoRadioButton;
    private JButton confirmarCompraButton;
    private List<ItemCarrinho> itensNoCarrinho;
    private int usuarioIdCliente = 1; // TODO: Obter o ID do cliente logado dinamicamente

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaCarrinho() {
        setTitle("Carrinho de Compras");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        itensNoCarrinho = new ArrayList<>();
        listModel = new DefaultListModel<>();
        listaItens = new JList<>(listModel);
        listaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel listaPanel = new JPanel(new BorderLayout());
        listaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        listaPanel.add(new JScrollPane(listaItens), BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removerItemButton = new JButton("Remover Item");
        alterarQtdButton = new JButton("Alterar Qtd.");
        botoesPanel.add(removerItemButton);
        botoesPanel.add(alterarQtdButton);
        listaPanel.add(botoesPanel, BorderLayout.SOUTH);

        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        totalLabel = new JLabel("Total: R$ 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        enderecoTextArea = new JTextArea(5, 20);
        enderecoTextArea.setBorder(BorderFactory.createTitledBorder("Endereço de Entrega:"));

        JPanel pagamentoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagamentoPanel.setBorder(BorderFactory.createTitledBorder("Pagamento"));
        pixRadioButton = new JRadioButton("Pix");
        creditoRadioButton = new JRadioButton("Cartão de Crédito");
        ButtonGroup pagamentoGroup = new ButtonGroup();
        pagamentoGroup.add(pixRadioButton);
        pagamentoGroup.add(creditoRadioButton);
        pixRadioButton.setSelected(true);
        pagamentoPanel.add(pixRadioButton);
        pagamentoPanel.add(creditoRadioButton);

        confirmarCompraButton = new JButton("Confirmar Compra");
        confirmarCompraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String endereco = enderecoTextArea.getText();
                String formaPagamento = pixRadioButton.isSelected() ? "Pix" : "Cartão de Crédito";
                double totalCompra = calcularTotal();

                if (endereco.isEmpty()) {
                    JOptionPane.showMessageDialog(TelaCarrinho.this, "Por favor, preencha o endereço de entrega.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (listModel.isEmpty()) {
                    JOptionPane.showMessageDialog(TelaCarrinho.this, "O carrinho está vazio. Adicione itens antes de confirmar a compra.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Salvar o pedido no banco de dados
                int pedidoId = salvarPedido(usuarioIdCliente, endereco, formaPagamento, totalCompra);

                if (pedidoId > 0) {
                    // Salvar os itens do pedido e atualizar o estoque
                    boolean itensSalvos = salvarItensPedido(pedidoId);
                    if (itensSalvos) {
                        JOptionPane.showMessageDialog(TelaCarrinho.this,
                                "Compra confirmada com sucesso!\nNúmero do Pedido: " + pedidoId +
                                        "\nTotal: R$" + String.format("%.2f", totalCompra) +
                                        "\nPagamento: " + formaPagamento,
                                "Confirmação", JOptionPane.INFORMATION_MESSAGE);
                        // Limpar o carrinho após a compra
                        listModel.clear();
                        itensNoCarrinho.clear();
                        atualizarTotal();
                    } else {
                        JOptionPane.showMessageDialog(TelaCarrinho.this, "Erro ao salvar os itens do pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
                        // Opcional: Reverter o pedido se os itens não forem salvos
                    }
                } else {
                    JOptionPane.showMessageDialog(TelaCarrinho.this, "Erro ao registrar o pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        checkoutPanel.add(totalLabel);
        checkoutPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        checkoutPanel.add(new JScrollPane(enderecoTextArea));
        checkoutPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        checkoutPanel.add(pagamentoPanel);
        checkoutPanel.add(Box.createVerticalGlue());
        checkoutPanel.add(confirmarCompraButton);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(checkoutPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listaPanel, rightPanel);
        splitPane.setDividerLocation(400);

        add(splitPane, BorderLayout.CENTER);

        // Listeners para remover e alterar quantidade (já implementados)
        removerItemButton.addActionListener(e -> {
            int selectedIndex = listaItens.getSelectedIndex();
            if (selectedIndex != -1) {
                ItemCarrinho removedItem = listModel.remove(selectedIndex);
                itensNoCarrinho.remove(removedItem);
                atualizarTotal();
            } else {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Selecione um item para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        alterarQtdButton.addActionListener(e -> {
            int selectedIndex = listaItens.getSelectedIndex();
            if (selectedIndex != -1) {
                ItemCarrinho itemParaAlterar = listModel.getElementAt(selectedIndex);
                String novoValorStr = JOptionPane.showInputDialog(TelaCarrinho.this, "Digite a nova quantidade para " + itemParaAlterar.getNome() + ":", itemParaAlterar.getQuantidade());
                if (novoValorStr != null) {
                    try {
                        int novaQuantidade = Integer.parseInt(novoValorStr);
                        if (novaQuantidade > 0) {
                            itemParaAlterar.setQuantidade(novaQuantidade);
                            listModel.setElementAt(itemParaAlterar, selectedIndex);
                            atualizarTotal();
                        } else {
                            JOptionPane.showMessageDialog(TelaCarrinho.this, "A quantidade deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(TelaCarrinho.this, "Por favor, digite um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Selecione um item para alterar a quantidade.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        setVisible(true);
    }

    private int salvarPedido(int usuarioId, String endereco, String formaPagamento, double total) {
        int pedidoId = -1;
        String sql = "INSERT INTO pedidos (usuario_id, endereco_entrega, forma_pagamento, total) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setString(2, endereco);
            pstmt.setString(3, formaPagamento);
            pstmt.setDouble(4, total);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    pedidoId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar pedido: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return pedidoId;
    }

    private boolean salvarItensPedido(int pedidoId) {
        boolean sucesso = true;
        String sql = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (ItemCarrinho item : itensNoCarrinho) {
                pstmt.setInt(1, pedidoId);
                pstmt.setInt(2, item.getProdutoId());
                pstmt.setInt(3, item.getQuantidade());
                pstmt.setDouble(4, item.getValorUnitario());
                pstmt.setDouble(5, item.getSubtotal());
                pstmt.addBatch();
                // Atualizar o estoque
                atualizarEstoque(conn, item.getProdutoId(), item.getTamanho(), -item.getQuantidade());
            }
            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    sucesso = false;
                    break;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar itens do pedido: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            sucesso = false;
        }
        return sucesso;
    }

    private void atualizarEstoque(Connection conn, int produtoId, String tamanho, int quantidadeAlteracao) throws SQLException {
        String colunaQuantidade = "";
        switch (tamanho.toUpperCase()) {
            case "P":
                colunaQuantidade = "quantidade_p";
                break;
            case "M":
                colunaQuantidade = "quantidade_m";
                break;
            case "G":
                colunaQuantidade = "quantidade_g";
                break;
            default:
                // Tamanho inválido, não atualiza o estoque para este item
                return;
        }
        String sql = "UPDATE produtos SET " + colunaQuantidade + " = " + colunaQuantidade + " + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantidadeAlteracao);
            pstmt.setInt(2, produtoId);
            pstmt.executeUpdate();
        }
    }

    public void adicionarItem(int produtoId, String nome, String tamanho, int quantidade, double valorUnitario) {
        ItemCarrinho novoItem = new ItemCarrinho(produtoId, nome, tamanho, quantidade, valorUnitario);
        itensNoCarrinho.add(novoItem);
        listModel.addElement(novoItem);
        atualizarTotal();
    }

    private double calcularTotal() {
        double total = 0;
        for (ItemCarrinho item : itensNoCarrinho) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void atualizarTotal() {
        totalLabel.setText("Total: R$ " + String.format("%.2f", calcularTotal()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCarrinho());
    }
}