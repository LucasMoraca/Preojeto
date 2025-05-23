// TelaCarrinho.java
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
    private int usuarioIdCliente = SessaoUsuario.getInstance().getUsuarioId() != null ? SessaoUsuario.getInstance().getUsuarioId() : 1; // Obtém ID da sessão
    // private String emailUsuarioLogado = obterEmailUsuarioLogado(); // Agora obtemos dinamicamente

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaCarrinho() {
        setTitle("Carrinho de Compras");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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
        confirmarCompraButton.addActionListener(e -> {
            String endereco = enderecoTextArea.getText();
            String formaPagamento = pixRadioButton.isSelected() ? "Pix" : "Cartão de Crédito";
            double totalCompra = calcularTotalCarrinhoSingleton();

            if (endereco.isEmpty()) {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Por favor, preencha o endereço de entrega.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (Carrinho.getInstance().getItens().isEmpty()) {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "O carrinho está vazio. Adicione itens antes de confirmar a compra.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int pedidoId = salvarPedido(usuarioIdCliente, endereco, formaPagamento, totalCompra, obterEmailUsuarioLogado());

            if (pedidoId > 0) {
                boolean itensSalvos = salvarItensPedido(pedidoId, Carrinho.getInstance().getItens());
                if (itensSalvos) {
                    JOptionPane.showMessageDialog(TelaCarrinho.this,
                            "Compra confirmada com sucesso!\nNúmero do Pedido: " + pedidoId +
                                    "\nTotal: R$" + String.format("%.2f", totalCompra) +
                                    "\nPagamento: " + formaPagamento,
                            "Confirmação", JOptionPane.INFORMATION_MESSAGE);
                    // Mensagem de boa compra e encerramento
                    JOptionPane.showMessageDialog(TelaCarrinho.this, "Obrigado pela sua compra!\nVolte sempre!", "Boa Compra", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0); // Encerra a aplicação
                    Carrinho.getInstance().limparCarrinho();
                    atualizarListaItens();
                    atualizarTotalLabel();
                } else {
                    JOptionPane.showMessageDialog(TelaCarrinho.this, "Erro ao salvar os itens do pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Erro ao registrar o pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
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

        removerItemButton.addActionListener(e -> {
            int selectedIndex = listaItens.getSelectedIndex();
            if (selectedIndex != -1) {
                ItemCarrinho removedItem = listModel.getElementAt(selectedIndex);
                Carrinho.getInstance().removerItem(removedItem.getProdutoId(), removedItem.getTamanho());
                atualizarListaItens();
                atualizarTotalLabel();
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
                            // Encontrar o item no Carrinho e atualizar a quantidade
                            for (ItemCarrinho itemCarrinho : Carrinho.getInstance().getItens()) {
                                if (itemCarrinho.getProdutoId() == itemParaAlterar.getProdutoId() && itemCarrinho.getTamanho().equals(itemParaAlterar.getTamanho())) {
                                    itemCarrinho.setQuantidade(novaQuantidade);
                                    break;
                                }
                            }
                            atualizarListaItens();
                            atualizarTotalLabel();
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

        // Inicializar a lista de itens do carrinho
        atualizarListaItens();
        atualizarTotalLabel();

        setVisible(true);
    }

    private void atualizarListaItens() {
        listModel.clear();
        for (ItemCarrinho item : Carrinho.getInstance().getItens()) {
            listModel.addElement(item);
        }
    }

    private double calcularTotalCarrinhoSingleton() {
        return Carrinho.getInstance().getTotal();
    }

    private void atualizarTotalLabel() {
        totalLabel.setText("Total: R$ " + String.format("%.2f", calcularTotalCarrinhoSingleton()));
    }

    private String obterEmailUsuarioLogado() {
        return SessaoUsuario.getInstance().getEmailUsuario();
    }

    private int salvarPedido(int usuarioId, String endereco, String formaPagamento, double total, String email) {
        int pedidoId = -1;
        String sql = "INSERT INTO pedidos (usuario_id, endereco_entrega, forma_pagamento, total, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setString(2, endereco);
            pstmt.setString(3, formaPagamento);
            pstmt.setDouble(4, total);
            pstmt.setString(5, email);
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

    private boolean salvarItensPedido(int pedidoId, List<ItemCarrinho> itens) {
        boolean sucesso = true;
        String sql = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal, tamanho) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (ItemCarrinho item : itens) {
                pstmt.setInt(1, pedidoId);
                pstmt.setInt(2, item.getProdutoId());
                pstmt.setInt(3, item.getQuantidade());
                pstmt.setDouble(4, item.getValorUnitario());
                pstmt.setDouble(5, item.getSubtotal());
                pstmt.setString(6, item.getTamanho());
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
                return;
        }
        String sql = "UPDATE produtos SET " + colunaQuantidade + " = " + colunaQuantidade + " + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantidadeAlteracao);
            pstmt.setInt(2, produtoId);
            pstmt.executeUpdate();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCarrinho());
    }
}