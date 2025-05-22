// TelaCatalogo.java
package Site;

import javax.swing.*; // Importa classes para criar interfaces gráficas Swing
import javax.swing.border.EmptyBorder; // Importa a classe para criar bordas vazias
import java.awt.*; // Importa classes para layouts e componentes gráficos AWT
import java.sql.Connection; // Importa a interface para a conexão com o banco de dados
import java.sql.DriverManager; // Importa a classe para gerenciar drivers JDBC
import java.sql.PreparedStatement; // Importa a classe para instruções SQL pré-compiladas
import java.sql.ResultSet; // Importa a interface para o resultado de uma consulta SQL
import java.sql.SQLException; // Importa a classe para exceções relacionadas ao SQL
import java.util.ArrayList; // Importa a classe ArrayList para listas dinâmicas
import java.util.List; // Importa a interface List para coleções ordenadas

// A classe TelaCatalogo herda de JFrame (janela principal)
public class TelaCatalogo extends JFrame {

    private JPanel painelListagemItens; // Painel para exibir a lista de itens do catálogo
    private JButton botaoCarrinho; // Botão para acessar a tela do carrinho
    private TelaLogin telaLogin; // Referência para a tela de login
    private TelaCarrinho telaCarrinho; // Instância da TelaCarrinho
    // private TelaUsuario telaUsuario; // Removido (funcionalidade movida para o botão de perfil, que foi removido)
    private JPanel painelSuperior; // Painel para conter componentes na parte superior (como o botão de carrinho)

    // Variável estática para rastrear o estado de login do usuário
    private static boolean usuarioEstaLogado = false;

    // Configurações do banco de dados MySQL (poderiam ser externalizadas em um arquivo de configuração)
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Construtor da TelaCatalogo
    public TelaCatalogo() {
        setTitle("Catálogo"); // Define o título da janela
        setSize(800, 600); // Define o tamanho inicial da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define o comportamento ao fechar a janela (apenas fecha esta tela)
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setLayout(new BorderLayout()); // Define o layout principal como BorderLayout

        // Inicializa a TelaCarrinho
        telaCarrinho = new TelaCarrinho();
        // telaUsuario = new TelaUsuario(); // Removido

        // Painel superior para ícones (atualmente apenas o botão de carrinho)
        painelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Layout FlowLayout alinhado à direita
        // botaoPerfil = new JButton("Perfil"); // Removido
        botaoCarrinho = new JButton("Carrinho");
        // painelSuperior.add(botaoPerfil); // Removido
        painelSuperior.add(botaoCarrinho); // Adiciona o botão de carrinho ao painel superior
        add(painelSuperior, BorderLayout.NORTH); // Adiciona o painel superior na parte norte da janela

        // Painel para listar os itens do catálogo com um FlowLayout alinhado à esquerda e espaçamento
        painelListagemItens = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        painelListagemItens.setBorder(BorderFactory.createTitledBorder("Itens do Catálogo")); // Adiciona uma borda com título
        JScrollPane scrollPane = new JScrollPane(painelListagemItens); // Adiciona barra de rolagem ao painel de listagem
        add(scrollPane, BorderLayout.CENTER); // Adiciona o painel de listagem no centro da janela

        // Adiciona ActionListener para mostrar a TelaCarrinho ao clicar no botão
        botaoCarrinho.addActionListener(e -> {
            telaCarrinho.setLocationRelativeTo(this); // Centraliza a tela do carrinho em relação ao catálogo
            telaCarrinho.mostrar(); // Torna a tela do carrinho visível
        });

        carregarProdutos(); // Chama o método para buscar e exibir os produtos do banco de dados
        setVisible(false); // A tela de catálogo começa invisível e é mostrada explicitamente
    }

    // Métodos estáticos para gerenciar o estado de login do usuário
    public static void setUsuarioLogado(boolean logado) {
        usuarioEstaLogado = logado;
    }

    public static boolean isUsuarioLogado() {
        return usuarioEstaLogado;
    }

    // Carrega os produtos e os exibe no painel de listagem
    public void carregarProdutos() {
        painelListagemItens.removeAll(); // Remove todos os componentes do painel de listagem
        List<Produto> produtos = buscarProdutosNoBanco(); // Busca a lista de produtos do banco de dados
        for (Produto produto : produtos) {
            JPanel produtoPanel = criarPainelProduto(produto); // Cria um painel para cada produto
            painelListagemItens.add(produtoPanel); // Adiciona o painel do produto ao painel de listagem
        }
        painelListagemItens.revalidate(); // Revalida o layout do painel
        painelListagemItens.repaint(); // Redesenha o painel
    }

    // Busca os produtos no banco de dados e retorna uma lista de objetos Produto
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

    // Cria um painel para exibir os detalhes de um produto
    private JPanel criarPainelProduto(Produto produto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Layout vertical
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Adiciona borda vazia ao redor do painel
        panel.setPreferredSize(new Dimension(200, 250)); // Define um tamanho preferencial para cada item

        // Exibir imagem do produto (se o caminho da imagem existir)
        if (produto.getImagem1Path() != null && !produto.getImagem1Path().isEmpty()) {
            ImageIcon imageIcon = new ImageIcon(produto.getImagem1Path());
            Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            panel.add(imageLabel);
            panel.add(Box.createVerticalStrut(5)); // Adiciona um pequeno espaço vertical
        } else {
            JLabel noImageLabel = new JLabel("Sem Imagem");
            noImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o texto
            panel.add(noImageLabel);
            panel.add(Box.createVerticalStrut(5));
        }

        // Exibir descrição do produto (truncada se for muito longa)
        String descricao = produto.getDescricao();
        JLabel descricaoLabel = new JLabel("Descrição: " + (descricao.length() > 50 ? descricao.substring(0, 50) + "..." : descricao));
        descricaoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descricaoLabel);

        // Exibir valor do produto
        JLabel valorLabel = new JLabel("Valor: R$" + String.format("%.2f", produto.getValor()));
        valorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(valorLabel);
        panel.add(Box.createVerticalStrut(10));

        // Botão para adicionar o produto ao carrinho
        JButton adicionarCarrinhoButton = new JButton("Adicionar ao Carrinho");
        adicionarCarrinhoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adicionarCarrinhoButton.addActionListener(e -> mostrarOpcoesAdicionarCarrinho(produto)); // Adiciona listener para mostrar opções
        panel.add(adicionarCarrinhoButton);

        return panel;
    }

    // Mostra um diálogo para o usuário escolher a quantidade e o tamanho para adicionar ao carrinho
    private void mostrarOpcoesAdicionarCarrinho(Produto produto) {
        // Verifica se o usuário está logado antes de permitir adicionar ao carrinho
        if (!TelaCatalogo.isUsuarioLogado()) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para adicionar itens ao carrinho.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return; // Impede a abertura das opções de adicionar ao carrinho
        }

        JPanel panel = new JPanel(new GridLayout(0, 1)); // Layout em grade com número de linhas automático e 1 coluna
        JComboBox<Integer> quantidadeComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5}); // ComboBox para escolher a quantidade
        JComboBox<String> tamanhoComboBox = new JComboBox<>(); // ComboBox para escolher o tamanho

        // Adiciona os tamanhos disponíveis ao ComboBox de tamanho, com base na quantidade em estoque
        if (produto.getQuantidadeP() > 0) tamanhoComboBox.addItem("P");
        if (produto.getQuantidadeM() > 0) tamanhoComboBox.addItem("M");
        if (produto.getQuantidadeG() > 0) tamanhoComboBox.addItem("G");
        if (tamanhoComboBox.getItemCount() == 0) {
            tamanhoComboBox.addItem("Único"); // Caso não haja tamanhos definidos
            tamanhoComboBox.setEnabled(false); // Desabilita se for tamanho único
        }

        // Adiciona os ComboBoxes e seus rótulos ao painel
        panel.add(new JLabel("Quantidade:"));
        panel.add(quantidadeComboBox);
        panel.add(new JLabel("Tamanho:"));
        panel.add(tamanhoComboBox);

        // Exibe o diálogo de confirmação
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Escolher Quantidade e Tamanho",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // Se o usuário clicar em OK, adiciona o item ao carrinho
        if (result == JOptionPane.OK_OPTION) {
            int quantidade = (Integer) quantidadeComboBox.getSelectedItem();
            String tamanho = (String) tamanhoComboBox.getSelectedItem();
            telaCarrinho.adicionarItem(produto, quantidade, tamanho); // Adiciona o item à tela do carrinho
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

    // Método para tornar a tela de catálogo visível
    public void mostrar() {
        setVisible(true);
    }

    // Método main para executar a TelaCatalogo individualmente (para testes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCatalogo().mostrar());
    }
}

// Classe auxiliar para representar um Produto (movida para dentro do arquivo TelaCatalogo.java)
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