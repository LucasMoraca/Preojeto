// TelaCarrinho.java
package Site;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.border.TitledBorder;

public class TelaCarrinho extends JFrame implements ActionListener, ListSelectionListener {

    private DefaultListModel<ItemCarrinho> carrinhoListModel;
    private JList<ItemCarrinho> carrinhoJList;
    private JButton removerItemButton;
    private JButton alterarQuantidadeButton;
    private JLabel totalLabel;
    private JTextArea enderecoTextArea;
    private JButton confirmarCompraButton;
    private List<ItemCarrinho> itensCarrinho;
    private JRadioButton pixRadioButton;
    private JRadioButton cartaoRadioButton;
    private ButtonGroup pagamentoGroup;
    private String metodoPagamentoSelecionado = null;

    // Configurações do banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaCarrinho() {
        setTitle("Carrinho de Compras");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        itensCarrinho = new ArrayList<>();
        carrinhoListModel = new DefaultListModel<>();
        carrinhoJList = new JList<>(carrinhoListModel);
        carrinhoJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carrinhoJList.addListSelectionListener(this);
        JScrollPane carrinhoScrollPane = new JScrollPane(carrinhoJList);
        add(carrinhoScrollPane, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removerItemButton = new JButton("Remover Item");
        removerItemButton.addActionListener(this);
        removerItemButton.setEnabled(false);
        botoesPanel.add(removerItemButton);

        alterarQuantidadeButton = new JButton("Alterar Qtd.");
        alterarQuantidadeButton.addActionListener(this);
        alterarQuantidadeButton.setEnabled(false);
        botoesPanel.add(alterarQuantidadeButton);

        add(botoesPanel, BorderLayout.SOUTH);

        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setBorder(new TitledBorder("Checkout"));

        totalLabel = new JLabel("Total: R$ 0.00");
        checkoutPanel.add(totalLabel);

        checkoutPanel.add(new JLabel("Endereço de Entrega:"));
        enderecoTextArea = new JTextArea(3, 30);
        JScrollPane enderecoScrollPane = new JScrollPane(enderecoTextArea);
        checkoutPanel.add(enderecoScrollPane);

        JPanel pagamentoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagamentoPanel.setBorder(new TitledBorder("Pagamento"));
        pixRadioButton = new JRadioButton("Pix");
        cartaoRadioButton = new JRadioButton("Cartão de Crédito");
        pagamentoGroup = new ButtonGroup();
        pagamentoGroup.add(pixRadioButton);
        pagamentoGroup.add(cartaoRadioButton);
        pixRadioButton.addActionListener(this);
        cartaoRadioButton.addActionListener(this);
        pagamentoPanel.add(pixRadioButton);
        pagamentoPanel.add(cartaoRadioButton);
        checkoutPanel.add(pagamentoPanel);

        confirmarCompraButton = new JButton("Confirmar Compra");
        confirmarCompraButton.addActionListener(this);
        checkoutPanel.add(confirmarCompraButton);

        add(checkoutPanel, BorderLayout.EAST);

        atualizarTotal();
        setVisible(false);
    }

    public void adicionarItem(Produto produto, int quantidade, String tamanho) {
        ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade, tamanho);
        itensCarrinho.add(novoItem);
        carrinhoListModel.addElement(novoItem);
        atualizarTotal();
    }

    public void removerItem(int index) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.remove(index);
            carrinhoListModel.remove(index);
            atualizarTotal();
            removerItemButton.setEnabled(false);
            alterarQuantidadeButton.setEnabled(false);
        }
    }

    private void mostrarDialogoAlterarQuantidade() {
        int selectedIndex = carrinhoJList.getSelectedIndex();
        if (selectedIndex != -1) {
            ItemCarrinho item = itensCarrinho.get(selectedIndex);
            String novaQtdStr = JOptionPane.showInputDialog(this, "Nova quantidade para " + item.getProduto().getDescricao() + ":", item.getQuantidade());
            if (novaQtdStr != null) {
                try {
                    int novaQuantidade = Integer.parseInt(novaQtdStr);
                    if (novaQuantidade > 0) {
                        atualizarQuantidade(selectedIndex, novaQuantidade);
                    } else {
                        JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Por favor, insira um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void atualizarQuantidade(int index, int novaQuantidade) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.get(index).setQuantidade(novaQuantidade);
            carrinhoListModel.setElementAt(itensCarrinho.get(index), index);
            atualizarTotal();
        }
    }

    private void atualizarTotal() {
        double total = 0;
        for (ItemCarrinho item : itensCarrinho) {
            total += item.getSubtotal();
        }
        totalLabel.setText("Total: R$ " + String.format("%.2f", total));
    }

    public double getTotal() {
        double total = 0;
        for (ItemCarrinho item : itensCarrinho) {
            total += item.getSubtotal();
        }
        return total;
    }

    private boolean verificarEstoque() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (ItemCarrinho item : itensCarrinho) {
                Produto produto = item.getProduto();
                int quantidadeComprada = item.getQuantidade();
                String tamanho = item.getTamanho();
                int produtoId = produto.getId();

                String sql = "SELECT quantidade_p, quantidade_m, quantidade_g FROM produtos WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, produtoId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int estoque = 0;
                        if (tamanho.equals("P")) {
                            estoque = rs.getInt("quantidade_p");
                        } else if (tamanho.equals("M")) {
                            estoque = rs.getInt("quantidade_m");
                        } else if (tamanho.equals("G")) {
                            estoque = rs.getInt("quantidade_g");
                        } else if (tamanho.equals("Único")) {
                            estoque = rs.getInt("quantidade_p"); // Consideramos a quantidade P para itens únicos
                        }

                        if (quantidadeComprada > estoque) {
                            JOptionPane.showMessageDialog(this,
                                    "Quantidade indisponível para o item: " + produto.getDescricao() + " (Tam: " + tamanho + ").\n" +
                                            "Estoque disponível: " + estoque + ", Quantidade desejada: " + quantidadeComprada + ".",
                                    "Erro de Estoque", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar o estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void atualizarEstoqueEBanco() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (ItemCarrinho item : itensCarrinho) {
                Produto produto = item.getProduto();
                int quantidadeComprada = item.getQuantidade();
                String tamanho = item.getTamanho();
                int produtoId = produto.getId();

                String updateSql = "";
                if (tamanho.equals("P")) {
                    updateSql = "UPDATE produtos SET quantidade_p = quantidade_p - ? WHERE id = ?";
                } else if (tamanho.equals("M")) {
                    updateSql = "UPDATE produtos SET quantidade_m = quantidade_m - ? WHERE id = ?";
                } else if (tamanho.equals("G")) {
                    updateSql = "UPDATE produtos SET quantidade_g = quantidade_g - ? WHERE id = ?";
                } else if (tamanho.equals("Único")) {
                    updateSql = "UPDATE produtos SET quantidade_p = quantidade_p - ? WHERE id = ?";
                }

                if (!updateSql.isEmpty()) {
                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setInt(1, quantidadeComprada);
                        pstmt.setInt(2, produtoId);
                        pstmt.executeUpdate();
                    }
                }

                String checkQuantidadeSql = "SELECT quantidade_p, quantidade_m, quantidade_g FROM produtos WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(checkQuantidadeSql)) {
                    pstmt.setInt(1, produtoId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int qtdP = rs.getInt("quantidade_p");
                        int qtdM = rs.getInt("quantidade_m");
                        int qtdG = rs.getInt("quantidade_g");
                        if (qtdP <= 0 && qtdM <= 0 && qtdG <= 0) {
                            String deleteSql = "DELETE FROM produtos WHERE id = ?";
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                                deleteStmt.setInt(1, produtoId);
                                deleteStmt.executeUpdate();
                                System.out.println("Produto ID " + produtoId + " removido por falta de estoque.");
                            }
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Estoque atualizado no banco de dados.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o estoque no banco de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void mostrarConfirmacaoCompra() {
        if (enderecoTextArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, adicione o endereço de entrega.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (metodoPagamentoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um método de pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (itensCarrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seu carrinho está vazio. Adicione produtos para comprar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Nova verificação de estoque antes de prosseguir
        if (!verificarEstoque()) {
            return; // Impede a finalização se houver problema de estoque
        }

        StringBuilder confirmacao = new StringBuilder("Confirmação da Compra:\n\nItens:\n");
        for (ItemCarrinho item : itensCarrinho) {
            confirmacao.append("- ").append(item).append("\n");
        }
        confirmacao.append("\nTotal: R$ ").append(String.format("%.2f", getTotal()));
        confirmacao.append("\nEndereço de Entrega: ").append(enderecoTextArea.getText());
        confirmacao.append("\nPagamento: ").append(metodoPagamentoSelecionado);

        int escolha = JOptionPane.showConfirmDialog(this, confirmacao.toString(), "Confirmar Compra", JOptionPane.OK_CANCEL_OPTION);
        if (escolha == JOptionPane.OK_OPTION) {
            atualizarEstoqueEBanco();
            JOptionPane.showMessageDialog(this, "Compra finalizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            itensCarrinho.clear();
            carrinhoListModel.clear();
            atualizarTotal();
            enderecoTextArea.setText("");
            pagamentoGroup.clearSelection();
            metodoPagamentoSelecionado = null;
            if (getParent() instanceof TelaCatalogo) {
                ((TelaCatalogo) getParent()).carregarProdutos();
            }
        }
    }

    public void mostrar() {
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == removerItemButton) {
            int selectedIndex = carrinhoJList.getSelectedIndex();
            if (selectedIndex != -1) {
                removerItem(selectedIndex);
            }
        } else if (e.getSource() == alterarQuantidadeButton) {
            mostrarDialogoAlterarQuantidade();
        } else if (e.getSource() == confirmarCompraButton) {
            mostrarConfirmacaoCompra();
        } else if (e.getSource() == pixRadioButton) {
            metodoPagamentoSelecionado = "Pix";
        } else if (e.getSource() == cartaoRadioButton) {
            metodoPagamentoSelecionado = "Cartão de Crédito";
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            boolean itemSelecionado = carrinhoJList.getSelectedIndex() != -1;
            removerItemButton.setEnabled(itemSelecionado);
            alterarQuantidadeButton.setEnabled(itemSelecionado);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaCarrinho tela = new TelaCarrinho();
            Produto p1 = new Produto(1, 25.00, 10, 5, 2, "Camiseta Azul", "caminho/azul.jpg");
            Produto p2 = new Produto(2, 50.00, 2, 8, 3, "Calça Jeans", "caminho/jeans.jpg");
            tela.adicionarItem(p1, 6, "M"); // Quantidade maior que o estoque (5)
            tela.adicionarItem(p2, 1, "G");
            tela.mostrar();
        });
    }
}