// TelaLogin.java
package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class TelaLogin extends JFrame {
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoEntrar;
    private TelaCatalogo telaCatalogo;
    private TelaBazar telaBazar;

    // Banco de dados
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaLogin(TelaCatalogo telaCatalogo, TelaBazar telaBazar) {
        this.telaCatalogo = telaCatalogo;
        this.telaBazar = telaBazar;

        setTitle("Login");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        JPanel painelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        painelCampos.add(new JLabel("Email:", SwingConstants.RIGHT));
        campoEmail = new JTextField();
        painelCampos.add(campoEmail);

        painelCampos.add(new JLabel("Senha:", SwingConstants.RIGHT));
        campoSenha = new JPasswordField();
        painelCampos.add(campoSenha);

        botaoEntrar = new JButton("Entrar");

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.add(botaoEntrar);

        setLayout(new BorderLayout(10, 10));
        add(painelCampos, BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        botaoEntrar.addActionListener((ActionEvent e) -> {
            String email = campoEmail.getText().trim();
            String senha = new String(campoSenha.getPassword()).trim();

            if (email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Autenticação como usuário
            Integer usuarioId = autenticar("usuarios", email, senha);
            if (usuarioId != null) {
                JOptionPane.showMessageDialog(this, "Login de usuário bem-sucedido!");

                TelaCatalogo.setUsuarioLogado(true);
                TelaUsuario.setUsuarioLogadoId(usuarioId);

                SwingUtilities.invokeLater(() -> {
                    telaCatalogo.setLocationRelativeTo(this);
                    telaCatalogo.mostrar();
                    dispose();
                });
                return;
            }

            // Autenticação como bazar
            Integer bazarId = autenticar("bazares", email, senha);
            if (bazarId != null) {
                JOptionPane.showMessageDialog(this, "Login de bazar bem-sucedido!");

                SwingUtilities.invokeLater(() -> {
                    telaBazar.setLocationRelativeTo(this);
                    telaBazar.mostrar();
                    dispose();
                });
                return;
            }

            // Nenhuma autenticação bem-sucedida
            JOptionPane.showMessageDialog(this, "Email ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
        });
    }

    /** Método genérico para autenticação em uma tabela */
    private Integer autenticar(String tabela, String email, String senha) {
        String sql = "SELECT id FROM " + tabela + " WHERE email = ? AND senha = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    /** Conexão com o banco de dados */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /** Torna a tela de login visível */
    public void mostrar() {
        setVisible(true);
    }
}
