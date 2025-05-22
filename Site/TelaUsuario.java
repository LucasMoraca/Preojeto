// TelaUsuario.java
package Site;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TelaUsuario extends JFrame {

    private JLabel nomeLabel;
    private JLabel emailLabel;
    private JLabel senhaLabel;

    // Simulação do ID do usuário logado
    private static Integer usuarioLogadoId = null;

    // Configurações do banco de dados
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaUsuario() {
        setTitle("Perfil do Usuário");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nomeLabel = new JLabel();
        emailLabel = new JLabel();
        senhaLabel = new JLabel();

        nomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        senhaLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(nomeLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(senhaLabel);

        add(panel, BorderLayout.CENTER);

        atualizarDadosUsuario();
    }

    /** Define o ID do usuário logado */
    public static void setUsuarioLogadoId(Integer id) {
        usuarioLogadoId = id;
    }

    /** Atualiza os dados exibidos do usuário */
    private void atualizarDadosUsuario() {
        if (usuarioLogadoId != null) {
            String sql = "SELECT nome_usuario, email FROM usuarios WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, usuarioLogadoId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String nome = rs.getString("nome_usuario");
                    String email = rs.getString("email");

                    nomeLabel.setText("Nome: " + nome);
                    emailLabel.setText("Email: " + email);
                    senhaLabel.setText("Senha: ********"); // Nunca mostrar senha real

                } else {
                    exibirMensagemDeErro("Usuário não encontrado.");
                }

            } catch (SQLException e) {
                exibirMensagemDeErro("Erro ao carregar dados do usuário: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            exibirMensagemSemLogin();
        }

        revalidate();
        repaint();
    }

    /** Exibe mensagem de erro no painel */
    private void exibirMensagemDeErro(String mensagem) {
        nomeLabel.setText(mensagem);
        emailLabel.setText("");
        senhaLabel.setText("");
    }

    /** Exibe mensagem quando não há usuário logado */
    private void exibirMensagemSemLogin() {
        nomeLabel.setText("Nenhum usuário logado.");
        emailLabel.setText("");
        senhaLabel.setText("");

        JPanel mensagemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel mensagemLabel = new JLabel("Retorne à tela inicial para se cadastrar ou fazer login.");
        mensagemPanel.add(mensagemLabel);
        add(mensagemPanel, BorderLayout.SOUTH);
    }

    /** Mostra a tela */
    public void mostrar() {
        setVisible(true);
        atualizarDadosUsuario();
    }

    /** Exibe perfil após login */
    public void exibirPerfilLogado() {
        mostrar();
    }

    /** Conexão com banco de dados */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /** Teste isolado da tela */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaUsuario tela = new TelaUsuario();
            tela.mostrar();

            // Para testar um usuário logado, descomente:
            // TelaUsuario.setUsuarioLogadoId(1);
            // tela.atualizarDadosUsuario();
        });
    }
}
