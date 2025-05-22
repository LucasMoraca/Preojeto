// TelaCatalogo.java
package Site;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelaCatalogo extends JFrame {

    private JPanel painelListagemItens;
    private JButton botaoCarrinho;
    private TelaLogin telaLogin; // Referência para a tela de login
    private TelaCarrinho telaCarrinho; // Instância da TelaCarrinho
    // private TelaUsuario telaUsuario; // Removido
    private JPanel painelSuperior; // Para o botão de carrinho

    // Variável estática para rastrear o estado de login
    private static boolean usuarioEstaLogado = false;

    // Configurações do banco de dados MySQL (poderiam ser externalizadas em um arquivo de configuração)
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaCatalogo() {
        setTitle("Catálogo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Inicializa a TelaCarrinho
        telaCarrinho = new TelaCarrinho();
        // telaUsuario = new TelaUsuario(); // Removido

        // Painel superior para ícones
        painelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // botaoPerfil = new JButton("Perfil"); // Removido
        botaoCarrinho = new JButton("Carrinho");
        // painelSuperior.add(botaoPerfil); // Removido
        painelSuperior.add(botaoCarrinho);
        add(painelSuperior, BorderLayout.NORTH);

        painelListagemItens = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        painelListagemItens.setBorder(BorderFactory.createTitledBorder("Itens do Catálogo"));
        JScrollPane scrollPane = new JScrollPane(painelListagemItens);
        add(scrollPane, BorderLayout.CENTER);

        // Adiciona ActionListener para mostrar a TelaCarrinho ao clicar no botão
        botaoCarrinho.addActionListener(e -> {
            telaCarrinho.setLocationRelativeTo(this);
            telaCarrinho.mostrar();
        });

        carregarProdutos();
        setVisible(false);
    }

    // Métodos estáticos para gerenciar o estado de login
    public static void setUsuarioLogado(boolean logado) {
        usuarioEstaLogado = logado;
    }

    public static boolean isUsuarioLogado() {
        return usuarioEstaLogado;
    }

    public void carregarProdutos() {
        painelListagemItens.removeAll();
        List<Produto> produtos = buscarProdutosNoBanco();
        for (Produto produto : produtos) {
            JPanel produtoPanel = criarPainelProduto(produto);
            painelListagemItens.add(produtoPanel);
        }
        painelListagemItens.revalidate();
        painelListagemItens.repaint();
    }

    private List<Produto> buscarProdutosNoBanco() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, valor, quantidade_p, quantidade_m, quantidade_g, descricao, imagem1_path FROM produtos";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
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
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return produtos;
    }

    private JPanel criarPainelProduto(Produto produto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(200, 250)); // Tamanho base para cada item

        // Exibir imagem (se houver caminho)
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

        // Exibir descrição do produto (truncada se for muito longa)
        String descricao = produto.getDescricao();
        JLabel descricaoLabel = new JLabel("Descrição: " + (descricao.length() > 50 ? descricao.substring(0, 50) + "..." : descricao));
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
        if (!TelaCatalogo.isUsuarioLogado()) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para adicionar itens ao carrinho.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return; // Impede a abertura das opções de adicionar ao carrinho
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JComboBox<Integer> quantidadeComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JComboBox<String> tamanhoComboBox = new JComboBox<>();
        if (produto.getQuantidadeP() > 0) tamanhoComboBox.addItem("P");
        if (produto.getQuantidadeM() > 0) tamanhoComboBox.addItem("M");
        if (produto.getQuantidadeG() > 0) tamanhoComboBox.addItem("G");
        if (tamanhoComboBox.getItemCount() == 0) {
            tamanhoComboBox.addItem("Único"); // Caso não haja tamanhos definidos
            tamanhoComboBox.setEnabled(false);
        }

        panel.add(new JLabel("Quantidade:"));
        panel.add(quantidadeComboBox);
        panel.add(new JLabel("Tamanho:"));
        panel.add(tamanhoComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Escolher Quantidade e Tamanho",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

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

    // Método para receber a instância de TelaLogin
    public void setTelaLogin(TelaLogin telaLogin) {
        this.telaLogin = telaLogin;
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCatalogo().mostrar());
    }
}

// Classe auxiliar para representar um Produto
class Produto {
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

    // Getters
    public int getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public int getQuantidadeP() {
        return quantidadeP;
    }

    public int getQuantidadeM() {
        return quantidadeM;
    }

    public int getQuantidadeG() {
        return quantidadeG;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getImagem1Path() {
        return imagem1Path;
    }
}