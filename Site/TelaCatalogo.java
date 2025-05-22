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

    private JPanel produtosPanel;
    private JScrollPane scrollPane;
    private List<ProdutoCatalogo> listaDeProdutos;
    private JButton carrinhoButton; // Botão para ir ao carrinho

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaCatalogo() {
        setTitle("Catálogo de Produtos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // Usando BorderLayout para posicionar o botão do carrinho

        // Painel para o botão do carrinho no topo
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        carrinhoButton = new JButton("Carrinho");
        carrinhoButton.addActionListener(e -> {
            // Abre a tela do carrinho
            TelaCarrinho telaCarrinho = new TelaCarrinho();
            telaCarrinho.setVisible(true);
        });
        topPanel.add(carrinhoButton);
        add(topPanel, BorderLayout.NORTH);

        produtosPanel = new JPanel();
        produtosPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        produtosPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        scrollPane = new JScrollPane(produtosPanel);
        add(scrollPane, BorderLayout.CENTER);

        listaDeProdutos = new ArrayList<>();
        carregarProdutosDoBanco();
        exibirProdutos();

        setVisible(true);
    }

    private void carregarProdutosDoBanco() {
        String sql = "SELECT id, nome, valor, imagens1_path FROM produtos";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                double valor = rs.getDouble("valor");
                String imagemPath = rs.getString("imagens1_path");
                listaDeProdutos.add(new ProdutoCatalogo(id, nome, valor, imagemPath));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void exibirProdutos() {
        produtosPanel.removeAll();
        for (ProdutoCatalogo produto : listaDeProdutos) {
            JPanel produtoPanel = new JPanel();
            produtoPanel.setLayout(new BoxLayout(produtoPanel, BoxLayout.Y_AXIS));
            produtoPanel.setPreferredSize(new Dimension(180, 270)); // Ajustei a altura
            produtoPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JLabel nomeLabel = new JLabel(produto.getNome());
            nomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

            ImageIcon imageIcon = null;
            if (produto.getImagemPath() != null && !produto.getImagemPath().isEmpty()) {
                try {
                    java.net.URL imgURL = getClass().getResource("/imagens/" + produto.getImagemPath());
                    if (imgURL == null) {
                        imgURL = new java.io.File(produto.getImagemPath()).toURI().toURL();
                    }
                    Image image = new ImageIcon(imgURL).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(image);
                } catch (Exception e) {
                    System.err.println("Erro ao carregar imagem: " + produto.getImagemPath() + " - " + e.getMessage());
                    imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/imagens/no_image.png")).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                }
            } else {
                imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/imagens/no_image.png")).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            }
            JLabel imagemLabel = new JLabel(imageIcon);
            imagemLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel valorLabel = new JLabel("R$ " + String.format("%.2f", produto.getValor()));
            valorLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JButton adicionarCarrinhoButton = new JButton("Adicionar ao Carrinho");
            adicionarCarrinhoButton.addActionListener(e -> {
                // Abre a tela TelaSelecaoTamanho
                TelaSelecaoTamanho telaSelecao = new TelaSelecaoTamanho(produto.getId(), produto.getNome(), produto.getValor());
                telaSelecao.setVisible(true);
            });

            produtoPanel.add(nomeLabel);
            produtoPanel.add(imagemLabel);
            produtoPanel.add(valorLabel);
            produtoPanel.add(adicionarCarrinhoButton);

            produtosPanel.add(produtoPanel);
        }
        produtosPanel.revalidate();
        produtosPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCatalogo());
    }
}

class ProdutoCatalogo {
    private int id;
    private String nome;
    private double valor;
    private String imagemPath;

    public ProdutoCatalogo(int id, String nome, double valor, String imagemPath) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.imagemPath = imagemPath;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getValor() {
        return valor;
    }

    public String getImagemPath() {
        return imagemPath;
    }
}