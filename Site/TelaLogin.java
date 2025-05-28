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

/**
 * A classe {@code TelaLogin} representa a tela de login do sistema Bazar Online.
 * Permite que usuários existentes façam login com seu email e senha,
 * e oferece uma opção para novos usuários se cadastrarem.
 * A autenticação é feita consultando um banco de dados de usuários.
 */
public class TelaLogin extends JFrame {

    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton cadastrarButton;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Construtor da classe {@code TelaLogin}.
     * Inicializa a interface gráfica da tela de login, incluindo campos para email e senha,
     * e botões para login e cadastro.
     */
    public TelaLogin() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Rótulo para o campo de email
        add(new JLabel("Email:"));
        // Campo de texto para inserir o email
        emailField = new JTextField();
        add(emailField);

        // Rótulo para o campo de senha
        add(new JLabel("Senha:"));
        // Campo de senha para inserir a senha (os caracteres são mascarados)
        senhaField = new JPasswordField();
        add(senhaField);

        add(new JLabel("")); // Espaço em branco para alinhamento

        // Botão de login
        loginButton = new JButton("Login");
        // Adiciona um ActionListener para lidar com o clique no botão de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtém o email e a senha inseridos pelo usuário
                String email = emailField.getText();
                String senha = new String(senhaField.getPassword());
                // Tenta autenticar o usuário no banco de dados
                String tipoUsuario = autenticarUsuario(email, senha);
                // Se a autenticação for bem-sucedida
                if (tipoUsuario != null) {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Login realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    TelaLogin.this.dispose(); // Fecha a tela de login
                    // Redireciona o usuário com base no seu tipo
                    if (tipoUsuario.equals("bazar")) {
                        TelaBazar telaBazar = new TelaBazar();
                        telaBazar.setVisible(true);
                    } else if (tipoUsuario.equals("cliente")) {
                        TelaCatalogo telaCatalogo = new TelaCatalogo();
                        telaCatalogo.setVisible(true);
                    }
                } else {
                    // Se a autenticação falhar, exibe uma mensagem de erro
                    JOptionPane.showMessageDialog(TelaLogin.this, "Email ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loginButton);

        add(new JLabel("")); // Espaço em branco para alinhamento

        // Botão de cadastro
        cadastrarButton = new JButton("Cadastrar");
        // Adiciona um ActionListener para lidar com o clique no botão de cadastro
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria e exibe a tela de cadastro
                TelaCadastro telaCadastro = new TelaCadastro();
                telaCadastro.setVisible(true);
            }
        });
        add(cadastrarButton);

        setVisible(true);
    }

    /**
     * Autentica um usuário no banco de dados com base no email e senha fornecidos.
     *
     * @param email O email do usuário.
     * @param senha A senha do usuário.
     * @return O tipo de usuário ("bazar" ou "cliente") se a autenticação for bem-sucedida, caso contrário, retorna {@code null}.
     */
    private String autenticarUsuario(String email, String senha) {
        String tipo = null;
        // Consulta SQL para buscar o usuário pelo email
        String sql = "SELECT id, nome, senha, tipo, email FROM usuarios WHERE email = ?"; // Incluímos a coluna email
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            // Se um usuário com o email fornecido for encontrado
            if (rs.next()) {
                // Obtém a senha armazenada no banco de dados
                String senhaBanco = rs.getString("senha");
                // Verifica se a senha fornecida corresponde à senha do banco de dados
                if (senha.equals(senhaBanco)) {
                    // Obtém o tipo do usuário
                    tipo = rs.getString("tipo");
                    // Inicia a sessão do usuário, armazenando informações como ID, nome e tipo
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
            // Exibe uma mensagem de erro se ocorrer algum problema na comunicação com o banco de dados
            JOptionPane.showMessageDialog(this, "Erro ao autenticar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return tipo; // Retorna o tipo do usuário em caso de sucesso, ou null em caso de falha
    }

    /**
     * Método principal para criar e exibir a {@code TelaLogin}.
     * Executa a criação da interface gráfica na thread de despacho de eventos (EDT).
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin());
    }
}