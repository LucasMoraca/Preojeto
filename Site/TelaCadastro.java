package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TelaCadastro extends JFrame {

    private JTextField nomeField;
    private JTextField emailField;
    private JPasswordField senhaField;
    private JComboBox<String> tipoUsuarioComboBox;
    private JButton cadastrarButton;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String ADMIN_PASSWORD = "admin123";

    public TelaCadastro() {
        setTitle("Cadastro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Nome:"));
        nomeField = new JTextField();
        add(nomeField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        add(senhaField);

        add(new JLabel("Tipo de Usuário:"));
        String[] tipos = {"cliente", "bazar"}; // Cliente primeiro na lista
        tipoUsuarioComboBox = new JComboBox<>(tipos);
        add(tipoUsuarioComboBox);

        cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = nomeField.getText();
                String email = emailField.getText();
                String senha = new String(senhaField.getPassword());
                String tipoSelecionado = (String) tipoUsuarioComboBox.getSelectedItem();
                String tipoParaBanco = tipoSelecionado;

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Por favor, preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (tipoSelecionado.equals("bazar")) {
                    String adminSenhaInserida = JOptionPane.showInputDialog(TelaCadastro.this, "Senha de Administrador:", "Confirmação de Bazar", JOptionPane.PLAIN_MESSAGE);
                    if (adminSenhaInserida == null || !adminSenhaInserida.equals(ADMIN_PASSWORD)) {
                        JOptionPane.showMessageDialog(TelaCadastro.this, "Senha de administrador incorreta. Cadastro como bazar cancelado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return; // Não prossegue com o cadastro de bazar
                    }
                    tipoParaBanco = "bazar";
                } else {
                    tipoParaBanco = "cliente";
                }

                if (cadastrarUsuario(nome, email, senha, tipoParaBanco)) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Cadastro realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    // Fechar a tela de cadastro
                    TelaCadastro.this.dispose();
                    // Iniciar a tela de login
                    TelaLogin telaLogin = new TelaLogin();
                    telaLogin.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Erro ao cadastrar o usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(new JLabel("")); // Espaço em branco no grid
        add(cadastrarButton);

        setVisible(true);
    }

    private boolean cadastrarUsuario(String nome, String email, String senha, String tipo) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, senha);
            pstmt.setString(4, tipo);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastro());
    }
}