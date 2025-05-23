// TelaLogin.java
package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaLogin extends JFrame {

    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton cadastrarButton;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaLogin() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        add(senhaField);

        add(new JLabel("")); // Espaço em branco
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String senha = new String(senhaField.getPassword());
                String tipoUsuario = autenticarUsuario(email, senha);
                if (tipoUsuario != null) {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Login realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    TelaLogin.this.dispose();
                    if (tipoUsuario.equals("bazar")) {
                        TelaBazar telaBazar = new TelaBazar();
                        telaBazar.setVisible(true);
                    } else if (tipoUsuario.equals("cliente")) {
                        TelaCatalogo telaCatalogo = new TelaCatalogo();
                        telaCatalogo.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Email ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loginButton);

        add(new JLabel("")); // Espaço em branco
        cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaCadastro telaCadastro = new TelaCadastro();
                telaCadastro.setVisible(true);
            }
        });
        add(cadastrarButton);

        setVisible(true);
    }

    private String autenticarUsuario(String email, String senha) {
        String tipo = null;
        String sql = "SELECT id, nome, senha, tipo, email FROM usuarios WHERE email = ?"; // Incluímos a coluna email
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String senhaBanco = rs.getString("senha");
                if (senha.equals(senhaBanco)) {
                    tipo = rs.getString("tipo");
                    if (tipo.equals("cliente")) {
                        int userId = rs.getInt("id");
                        String nomeUsuario = rs.getString("nome");
                        String emailUsuario = rs.getString("email"); // Obtemos o email
                        SessaoUsuario.getInstance().iniciarSessao(userId, nomeUsuario, tipo, emailUsuario); // Passamos o email
                    } else {
                        SessaoUsuario.getInstance().iniciarSessao(rs.getInt("id"), rs.getString("nome"), tipo, rs.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao autenticar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return tipo;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin());
    }
}