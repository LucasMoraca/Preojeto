// TelaUsuario.java
package Site;

import javax.swing.*; // Importa classes para criar interfaces gráficas Swing
import java.awt.*; // Importa classes para layouts e componentes gráficos AWT
import java.sql.Connection; // Importa a interface para a conexão com o banco de dados
import java.sql.DriverManager; // Importa a classe para gerenciar drivers JDBC
import java.sql.PreparedStatement; // Importa a classe para instruções SQL pré-compiladas
import java.sql.ResultSet; // Importa a interface para o resultado de uma consulta SQL
import java.sql.SQLException; // Importa a classe para exceções relacionadas ao SQL

// A classe TelaUsuario herda de JFrame (janela principal)
public class TelaUsuario extends JFrame {

    // Declaração de rótulos para exibir informações do usuário
    private JLabel nomeLabel;
    private JLabel emailLabel;
    private JLabel senhaLabel; // Novo label para exibir uma representação da senha

    // Variável estática para armazenar o ID do usuário logado
    // Inicialmente é nulo, indicando que nenhum usuário está logado
    private static Integer usuarioLogadoId = null;

    // Configurações para o banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto"; // URL de conexão com o banco de dados MySQL
    private static final String DB_USER = "root"; // Nome de usuário do MySQL
    private static final String DB_PASSWORD = ""; // Senha do MySQL (vazia neste caso)

    // Construtor da TelaUsuario
    public TelaUsuario() {
        setTitle("Perfil do Usuário"); // Define o título da janela
        setSize(400, 300); // Define o tamanho da janela (largura 400, altura 300 pixels)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define o comportamento ao fechar a janela (apenas fecha esta tela)
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setLayout(new GridLayout(0, 1, 10, 10)); // Define o layout como uma grade com número de linhas automático, 1 coluna, e espaçamento entre os componentes

        // Cria um painel para organizar os rótulos verticalmente
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Layout vertical
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adiciona bordas ao redor do painel

        // Inicializa os rótulos
        nomeLabel = new JLabel();
        emailLabel = new JLabel();
        senhaLabel = new JLabel(); // Inicializa o label para a senha

        // Adiciona os rótulos ao painel
        panel.add(nomeLabel);
        panel.add(emailLabel);
        panel.add(senhaLabel); // Adiciona o label da senha ao painel

        // Adiciona o painel à janela
        add(panel);

        // Chama o método para carregar e exibir os dados do usuário
        atualizarDadosUsuario();
    }

    // Método estático para definir o ID do usuário logado
    // Este ID seria definido após um login bem-sucedido
    public static void setUsuarioLogadoId(Integer id) {
        usuarioLogadoId = id;
    }

    // Método para buscar e exibir os dados do usuário logado
    private void atualizarDadosUsuario() {
        // Verifica se há um ID de usuário logado
        if (usuarioLogadoId != null) {
            String sql = "SELECT nome_usuario, email FROM usuarios WHERE id = ?"; // Consulta SQL para obter nome e email do usuário pelo ID
            try (Connection conn = getConnection(); // Abre uma conexão com o banco de dados (será fechada automaticamente)
                 PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara a instrução SQL
                pstmt.setInt(1, usuarioLogadoId); // Define o valor do parâmetro na consulta SQL (o ID do usuário)
                ResultSet rs = pstmt.executeQuery(); // Executa a consulta SQL e obtém o resultado

                // Se um usuário com o ID fornecido for encontrado
                if (rs.next()) {
                    String nome = rs.getString("nome_usuario"); // Obtém o nome do usuário do resultado
                    String email = rs.getString("email"); // Obtém o email do usuário do resultado

                    // Atualiza os textos dos rótulos com os dados do usuário
                    nomeLabel.setText("Nome: " + nome);
                    emailLabel.setText("Email: " + email);
                    senhaLabel.setText("Senha: ********"); // Exibe uma representação da senha (não a senha real por segurança)
                    emailLabel.setVisible(true);
                    senhaLabel.setVisible(true);
                } else {
                    // Caso o usuário não seja encontrado no banco de dados (erro interno)
                    nomeLabel.setText("Erro ao carregar dados do usuário.");
                    emailLabel.setVisible(false);
                    senhaLabel.setVisible(false);
                }
            } catch (SQLException e) {
                // Em caso de erro de SQL, exibe uma mensagem e imprime o stack trace
                JOptionPane.showMessageDialog(this, "Erro ao carregar dados do usuário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                nomeLabel.setText("Erro ao carregar dados do usuário.");
                emailLabel.setVisible(false);
                senhaLabel.setVisible(false);
            }
        } else {
            // Se nenhum usuário estiver logado
            nomeLabel.setText("Nenhum usuário logado.");
            emailLabel.setVisible(false);
            senhaLabel.setVisible(false);

            // Cria um painel com uma mensagem informando que o usuário precisa fazer login
            JPanel mensagemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel mensagemLabel = new JLabel("Por favor, retorne à tela inicial para se cadastrar ou fazer login.");
            mensagemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mensagemPanel.add(mensagemLabel);
            add(mensagemPanel, BorderLayout.SOUTH); // Adiciona o painel de mensagem na parte inferior
        }

        // Força a atualização da interface gráfica
        revalidate();
        repaint();
    }

    // Método para tornar a tela de usuário visível e atualizar os dados
    public void mostrar() {
        setVisible(true);
        atualizarDadosUsuario(); // Garante que os dados sejam atualizados quando a tela é mostrada
    }

    // Novo método para exibir o perfil após o login (apenas chama o método mostrar)
    public void exibirPerfilLogado() {
        atualizarDadosUsuario();
        mostrar();
    }

    // Método para obter uma conexão com o banco de dados MySQL
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Método main para testar a TelaUsuario individualmente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaUsuario tela = new TelaUsuario();
            tela.mostrar();

            // Para testar com um ID de usuário logado simulado, descomente a linha abaixo:
            // TelaUsuario.setUsuarioLogadoId(1);
            // TelaUsuario telaLogado = new TelaUsuario();
            // telaLogado.mostrar();
        });
    }
}