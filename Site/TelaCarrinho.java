import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TelaCarrinho extends JFrame implements ActionListener, ListSelectionListener {

    private List<ItemCarrinho> itensCarrinho;
    private JPanel cartItemsPanel;
    private JLabel totalLabel;
    private double currentTotal;
    private NumberFormat currencyFormat;
    private JTextArea enderecoTextArea;
    private ButtonGroup pagamentoGroup;
    private JRadioButton pixRadioButton, cartaoRadioButton;
    private JButton confirmarCompraButton;
    private String metodoPagamentoSelecionado;

    // Dados do banco (configure corretamente)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/seu_banco";
    private static final String DB_USER = "usuario";
    private static final String DB_PASSWORD = "senha";

    public TelaCarrinho() {
        super("Carrinho de Compras");

        itensCarrinho = new ArrayList<>();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Total do Carrinho: " + currencyFormat.format(0));
        bottomPanel.add(totalLabel, BorderLayout.NORTH);

        JPanel enderecoPanel = new JPanel(new BorderLayout());
        enderecoPanel.setBorder(BorderFactory.createTitledBorder("Endereço de Entrega"));
        enderecoTextArea = new JTextArea(3, 40);
        enderecoTextArea.setLineWrap(true);
        enderecoTextArea.setWrapStyleWord(true);
        enderecoPanel.add(new JScrollPane(enderecoTextArea), BorderLayout.CENTER);
        bottomPanel.add(enderecoPanel, BorderLayout.CENTER);

        JPanel pagamentoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagamentoPanel.setBorder(BorderFactory.createTitledBorder("Método de Pagamento"));
        pixRadioButton = new JRadioButton("Pix");
        cartaoRadioButton = new JRadioButton("Cartão de Crédito");
        pagamentoGroup = new ButtonGroup();
        pagamentoGroup.add(pixRadioButton);
        pagamentoGroup.add(cartaoRadioButton);
        pagamentoPanel.add(pixRadioButton);
        pagamentoPanel.add(cartaoRadioButton);
        bottomPanel.add(pagamentoPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        confirmarCompraButton = new JButton("Confirmar Compra");
        confirmarCompraButton.addActionListener(this);
        add(confirmarCompraButton, BorderLayout.NORTH);

        pixRadioButton.addActionListener(this);
        cartaoRadioButton.addActionListener(this);
    }

    // Método para carregar produtos do banco
    public List<Produto> carregarProdutosDoBanco() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, valor, quantidade_p, quantidade_m, quantidade_g, descricao, imagemPath FROM produtos";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getDouble("valor"),
                        rs.getInt("quantidade_p"),
                        rs.getInt("quantidade_m"),
                        rs.getInt("quantidade_g"),
                        rs.getString("descricao"),
                        rs.getString("imagemPath")
                );
                produtos.add(p);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return produtos;
    }

    // Adiciona item no carrinho (como antes)
    public void adicionarItem(Produto produto, int quantidade, String tamanho) {
        for (ItemCarrinho item : itensCarrinho) {
            if (item.getProduto().getId() == produto.getId() && item.getTamanho().equals(tamanho)) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                atualizarExibicaoCarrinho();
                return;
            }
        }
        ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade, tamanho);
        itensCarrinho.add(novoItem);
        atualizarExibicaoCarrinho();
    }

    private void addCartItemToPanel(ItemCarrinho item) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JLabel descricaoLabel = new JLabel(item.toString());
        itemPanel.add(descricaoLabel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removerButton = new JButton("Remover");
        removerButton.addActionListener(e -> {
            removerItem(itensCarrinho.indexOf(item));
        });
        buttonsPanel.add(removerButton);

        JTextField quantidadeField = new JTextField(String.valueOf(item.getQuantidade()), 3);
        quantidadeField.addActionListener(e -> {
            try {
                int novaQtd = Integer.parseInt(quantidadeField.getText());
                if (novaQtd <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    quantidadeField.setText(String.valueOf(item.getQuantidade()));
                    return;
                }
                item.setQuantidade(novaQtd);
                updateItemTotalPrice(itemPanel, item);
                atualizarTotal();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                quantidadeField.setText(String.valueOf(item.getQuantidade()));
            }
        });
        buttonsPanel.add(new JLabel("Qtd: "));
        buttonsPanel.add(quantidadeField);

        JLabel totalItemLabel = new JLabel(" | Total: " + currencyFormat.format(item.getSubtotal()));
        buttonsPanel.add(totalItemLabel);

        itemPanel.add(buttonsPanel, BorderLayout.EAST);

        cartItemsPanel.add(itemPanel);
    }

    private void updateItemTotalPrice(JPanel itemPanel, ItemCarrinho item) {
        Component[] components = itemPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel buttonsPanel = (JPanel) comp;
                for (Component innerComp : buttonsPanel.getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel label = (JLabel) innerComp;
                        if (label.getText().startsWith(" | Total:")) {
                            label.setText(" | Total: " + currencyFormat.format(item.getSubtotal()));
                            break;
                        }
                    }
                }
            }
        }
    }

    public void removerItem(int index) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.remove(index);
            atualizarExibicaoCarrinho();
        }
    }

    private void atualizarTotal() {
        currentTotal = 0;
        for (ItemCarrinho item : itensCarrinho) {
            currentTotal += item.getSubtotal();
        }
        totalLabel.setText("Total do Carrinho: " + currencyFormat.format(currentTotal));
    }

    public double getTotal() {
        return currentTotal;
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
                        switch (tamanho) {
                            case "P":
                                estoque = rs.getInt("quantidade_p");
                                break;
                            case "M":
                                estoque = rs.getInt("quantidade_m");
                                break;
                            case "G":
                                estoque = rs.getInt("quantidade_g");
                                break;
                            case "Único":
                                estoque = rs.getInt("quantidade_p");
                                break;
                            default:
                                estoque = 0;
                        }

                        if (quantidadeComprada > estoque) {
                            JOptionPane.showMessageDialog(this,
                                    "Quantidade indisponível para: " + produto.getDescricao() + " (Tam: " + tamanho + ").\n" +
                                            "Estoque: " + estoque + ", Desejado: " + quantidadeComprada + ".",
                                    "Erro de Estoque", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
                switch (tamanho) {
                    case "P":
                        updateSql = "UPDATE produtos SET quantidade_p = quantidade_p - ? WHERE id = ?";
                        break;
                    case "M":
                        updateSql = "UPDATE produtos SET quantidade_m = quantidade_m - ? WHERE id = ?";
                        break;
                    case "G":
                        updateSql = "UPDATE produtos SET quantidade_g = quantidade_g - ? WHERE id = ?";
                        break;
                    case "Único":
                        updateSql = "UPDATE produtos SET quantidade_p = quantidade_p - ? WHERE id = ?";
                        break;
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
            JOptionPane.showMessageDialog(this, "Compra finalizada e estoque atualizado.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void mostrarConfirmacaoCompra() {
        if (enderecoTextArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o endereço de entrega.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (metodoPagamentoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione o método de pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (itensCarrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!verificarEstoque()) {
            return;
        }

        StringBuilder confirmacao = new StringBuilder("Confirmação da Compra:\n\nItens:\n");
        for (ItemCarrinho item : itensCarrinho) {
            confirmacao.append("- ").append(item).append("\n");
        }
        confirmacao.append("\nTotal: ").append(currencyFormat.format(getTotal()));
        confirmacao.append("\nEndereço: ").append(enderecoTextArea.getText());
        confirmacao.append("\nPagamento: ").append(metodoPagamentoSelecionado);

        int escolha = JOptionPane.showConfirmDialog(this, confirmacao.toString(), "Confirmar Compra", JOptionPane.OK_CANCEL_OPTION);
        if (escolha == JOptionPane.OK_OPTION) {
            atualizarEstoqueEBanco();
            itensCarrinho.clear();
            atualizarExibicaoCarrinho();
            enderecoTextArea.setText("");
            pagamentoGroup.clearSelection();
            metodoPagamentoSelecionado = null;

            if (getParent() instanceof TelaCatalogo) {
                ((TelaCatalogo) getParent()).carregarProdutos();
            }
        }
    }

    public void mostrar() {
        atualizarExibicaoCarrinho();
        setVisible(true);
    }

    private void atualizarExibicaoCarrinho() {
        cartItemsPanel.removeAll();
        for (ItemCarrinho item : itensCarrinho) {
            addCartItemToPanel(item);
        }
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
        atualizarTotal();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == confirmarCompraButton) {
            mostrarConfirmacaoCompra();
        } else if (source == pixRadioButton) {
            metodoPagamentoSelecionado = "Pix";
        } else if (source == cartaoRadioButton) {
            metodoPagamentoSelecionado = "Cartão de Crédito";
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Não usado
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaCarrinho tela = new TelaCarrinho();

            // Exemplo: carrega produtos do banco e adiciona ao carrinho para testes
            List<Produto> produtosDoBanco = tela.carregarProdutosDoBanco();
            if (!produtosDoBanco.isEmpty()) {
                // Adiciona o primeiro produto tamanho P com quantidade 2
                tela.adicionarItem(produtosDoBanco.get(0), 2, "P");

                // Se tiver mais produtos, adiciona outro item
                if (produtosDoBanco.size() > 1) {
                    tela.adicionarItem(produtosDoBanco.get(1), 1, "M");
                }
            }

            tela.mostrar();
        });
    }
}

class Produto {
    private int id;
    private double valor;
    private int quantidade_p;
    private int quantidade_m;
    private int quantidade_g;
    private String descricao;
    private String imagemPath;

    public Produto(int id, double valor, int quantidade_p, int quantidade_m, int quantidade_g, String descricao, String imagemPath) {
        this.id = id;
        this.valor = valor;
        this.quantidade_p = quantidade_p;
        this.quantidade_m = quantidade_m;
        this.quantidade_g = quantidade_g;
        this.descricao = descricao;
        this.imagemPath = imagemPath;
    }

    public int getId() { return id; }
    public double getValor() { return valor; }
    public int getQuantidadeP() { return quantidade_p; }
    public int getQuantidadeM() { return quantidade_m; }
    public int getQuantidadeG() { return quantidade_g; }
    public String getDescricao() { return descricao; }
    public String getImagemPath() { return imagemPath; }
}

class ItemCarrinho {
    private Produto produto;
    private int quantidade;
    private String tamanho;

    public ItemCarrinho(Produto produto, int quantidade, String tamanho) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.tamanho = tamanho;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getTamanho() { return tamanho; }

    public double getSubtotal() {
        return produto.getValor() * quantidade;
    }

    @Override
    public String toString() {
        return produto.getDescricao() + " (Tam: " + tamanho + ") - Qtd: " + quantidade + " - " + NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(getSubtotal());
    }
}

