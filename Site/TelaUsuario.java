// TelaUsuario.java
package Site;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaUsuario extends JFrame {

    private JLabel nomeLabel;
    private JLabel emailLabel;
    private JLabel senhaLabel; // Novo label para a senha

    // Vamos simular o ID do usuário logado. Em uma aplicação real, isso viria de um sistema de autenticação.
    private static Integer usuarioLogadoId = null; // Inicialmente nenhum usuário logado

    // Configurações para o banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto"; // URL de conexão com o banco de dados MySQL
    private static final String DB_USER = "root"; // Nome de usuário do MySQL
    private static final String DB_PASSWORD = ""; // Senha do MySQL

    public TelaUsuario() {
        setTitle("Perfil do Usuário");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0, 1, 10, 10)); // Layout em grade para os labels com espaçamento
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nomeLabel = new JLabel();
        emailLabel = new JLabel();
        senhaLabel = new JLabel(); // Inicializa o label da senha

        panel.add(nomeLabel);
        panel.add(emailLabel);
        panel.add(senhaLabel); // Adiciona o label da senha

        add(panel);

        atualizarDadosUsuario();
    }

    // Método para definir o ID do usuário logado
    public static void setUsuarioLogadoId(Integer id) {
        usuarioLogadoId = id;
    }

    private void atualizarDadosUsuario() {
        if (usuarioLogadoId != null) {
            String sql = "SELECT nome_usuario, email FROM usuarios WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuarioLogadoId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String email = rs.getString("email");

                    nomeLabel.setText("Nome: " + nome);
                    emailLabel.setText("Email: " + email);
                    senhaLabel.setText("Senha: ********"); // Não exibimos a senha real
                    emailLabel.setVisible(true);
                    senhaLabel.setVisible(true);
                } else {
                    // Usuário não encontrado (erro interno)
                    nomeLabel.setText("Erro ao carregar dados do usuário.");
                    emailLabel.setVisible(false);
                    senhaLabel.setVisible(false);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar dados do usuário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                nomeLabel.setText("Erro ao carregar dados do usuário.");
                emailLabel.setVisible(false);
                senhaLabel.setVisible(false);
            }
        } else {
            // Nenhum usuário logado
            nomeLabel.setText("Nenhum usuário logado.");
            emailLabel.setVisible(false);
            senhaLabel.setVisible(false);

            JPanel mensagemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel mensagemLabel = new JLabel("Por favor, retorne à tela inicial para se cadastrar ou fazer login.");
            mensagemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mensagemPanel.add(mensagemLabel);
            add(mensagemPanel, BorderLayout.SOUTH);
        }

        revalidate();
        repaint();
    }

    public void mostrar() {
        setVisible(true);
        atualizarDadosUsuario(); // Garante que os dados sejam atualizados ao mostrar a tela
    }

    // Novo método para exibir o perfil após o login
    public void exibirPerfilLogado() {
        atualizarDadosUsuario();
        mostrar();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaUsuario tela = new TelaUsuario();
            tela.mostrar();

            // Para testar com um usuário logado, descomente a linha abaixo:
            // TelaUsuario.setUsuarioLogadoId(1);
            // TelaUsuario telaLogado = new TelaUsuario();
            // telaLogado.mostrar();
        });
    }
}