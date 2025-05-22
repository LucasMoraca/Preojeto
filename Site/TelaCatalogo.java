// TelaCatalogo.java
package Site;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelaCatalogo extends JFrame {

    private JPanel painelListagemItens;
    private JButton botaoCarrinho;
    private TelaCarrinho telaCarrinho;

    private static boolean usuarioEstaLogado = false;

    // Configurações do banco
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaCatalogo() {
        setTitle("Catálogo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        telaCarrinho = new TelaCarrinho();

        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoCarrinho = new JButton("Carrinho");
        painelSuperior.add(botaoCarrinho);
        add(painelSuperior, BorderLayout.NORTH);

        painelListagemItens = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        painelListagemItens.setBorder(BorderFactory.createTitledBorder("Itens do Catálogo"));
        JScrollPane scrollPane = new JScrollPane(painelListagemItens);
        add(scrollPane, BorderLayout.CENTER);

        botaoCarrinho.addActionListener(e -> {
            telaCarrinho.setLocationRelativeTo(this);
            telaCarrinho.mostrar();
        });

        carregarProdutos();
        setVisible(true);
    }

    public static void setUsuarioLogado(boolean logado) {
        usuarioEstaLogado = logado;
    }

    public static boolean isUsuarioLogado() {
        return usuarioEstaLogado;
    }

    private void carregarProdutos() {
        painelListagemItens.removeAll();
        List<Produto> produtos = ProdutoDAO.listarProdutos();

        for (Produto produto : produtos) {
            JPanel produtoPanel = criarPainelProduto(produto);
            painelListagemItens.add(produtoPanel);
        }

        painelListagemItens.revalidate();
        painelListagemItens.repaint();
    }

    private JPanel criarPainelProduto(Produto produto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(200, 250));

        if (produto.getImagem1Path() != null && !produto.getImagem1Path().isEmpty()) {
            ImageIcon imageIcon = new ImageIcon(produto.getImagem1Path());
            Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            panel.add(imageLabel);
            panel.add(Box.createVerticalStrut(5));
        } else {
            JLabel noImageLabel = new JLabel("Sem Imagem");
            noImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noImageLabel);
            panel.add(Box.createVerticalStrut(5));
        }

        JLabel descricaoLabel = new JLabel("Descrição: " + (produto.getDescricao().length() > 50 ?
                produto.getDescricao().substring(0, 50) + "..." : produto.getDescricao()));
        descricaoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descricaoLabel);

        JLabel valorLabel = new JLabel("Valor: R$" + String.format("%.2f", produto.getValor()));
        valorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(valorLabel);
        panel.add(Box.createVerticalStrut(10));

        JButton adicionarCarrinhoButton = new JButton("Adicionar ao Carrinho");
        adicionarCarrinhoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adicionarCarrinhoButton.addActionListener(e -> mostrarOpcoesAdicionarCarrinho(produto));
        panel.add(adicionarCarrinhoButton);

        return panel;
    }

    private void mostrarOpcoesAdicionarCarrinho(Produto produto) {
        if (!isUsuarioLogado()) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para adicionar itens ao carrinho.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JComboBox<Integer> quantidadeComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JComboBox<String> tamanhoComboBox = new JComboBox<>();
        if (produto.getQuantidadeP() > 0) tamanhoComboBox.addItem("P");
        if (produto.getQuantidadeM() > 0) tamanhoComboBox.addItem("M");
        if (produto.getQuantidadeG() > 0) tamanhoComboBox.addItem("G");
        if (tamanhoComboBox.getItemCount() == 0) {
            tamanhoComboBox.addItem("Único");
            tamanhoComboBox.setEnabled(false);
        }

        panel.add(new JLabel("Quantidade:"));
        panel.add(quantidadeComboBox);
        panel.add(new JLabel("Tamanho:"));
        panel.add(tamanhoComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Escolher Quantidade e Tamanho", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int quantidade = (Integer) quantidadeComboBox.getSelectedItem();
            String tamanho = (String) tamanhoComboBox.getSelectedItem();
            telaCarrinho.adicionarItem(produto, quantidade, tamanho);
            JOptionPane.showMessageDialog(this,
                    produto.getDescricao() + " (x" + quantidade + ", Tam: " + tamanho + ") adicionado ao carrinho.",
                    "Adicionado ao Carrinho",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // *** CLASSE PRODUTO ***
    static class Produto {
        private int id;
        private double valor;
        private int quantidadeP;
        private int quantidadeM;
        private int quantidadeG;
        private String descricao;
        private String imagem1Path;

        public Produto(int id, double valor, int quantidadeP, int quantidadeM, int quantidadeG, String descricao, String imagem1Path) {
            this.id = id;
            this.valor = valor;
            this.quantidadeP = quantidadeP;
            this.quantidadeM = quantidadeM;
            this.quantidadeG = quantidadeG;
            this.descricao = descricao;
            this.imagem1Path = imagem1Path;
        }

        public int getId() { return id; }
        public double getValor() { return valor; }
        public int getQuantidadeP() { return quantidadeP; }
        public int getQuantidadeM() { return quantidadeM; }
        public int getQuantidadeG() { return quantidadeG; }
        public String getDescricao() { return descricao; }
        public String getImagem1Path() { return imagem1Path; }
    }

    // *** DAO para PRODUTOS ***
    static class ProdutoDAO {
        public static List<Produto> listarProdutos() {
            List<Produto> produtos = new ArrayList<>();
            String sql = "SELECT id, valor, quantidade_p, quantidade_m, quantidade_g, descricao, imagem1_path FROM produtos";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Produto produto = new Produto(
                            rs.getInt("id"),
                            rs.getDouble("valor"),
                            rs.getInt("quantidade_p"),
                            rs.getInt("quantidade_m"),
                            rs.getInt("quantidade_g"),
                            rs.getString("descricao"),
                            rs.getString("imagem1_path")
                    );
                    produtos.add(produto);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            return produtos;
        }
    }

    // *** TelaCarrinho simplificada ***
    static class TelaCarrinho extends JFrame {
        private DefaultListModel<String> itensModel;
        private JList<String> listaItens;

        public TelaCarrinho() {
            setTitle("Carrinho");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            itensModel = new DefaultListModel<>();
            listaItens = new JList<>(itensModel);

            add(new JScrollPane(listaItens), BorderLayout.CENTER);
        }

        public void adicionarItem(Produto produto, int quantidade, String tamanho) {
            String item = produto.getDescricao() + " - Qtde: " + quantidade + " - Tam: " + tamanho + " - R$" + String.format("%.2f", produto.getValor() * quantidade);
            itensModel.addElement(item);
        }

        public void mostrar() {
            setVisible(true);
        }
    }

    // Main
    public static void main(String[] args) {
        // Para evitar erro com driver JDBC, registrar explicitamente
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(TelaCatalogo::new);
    }
}
