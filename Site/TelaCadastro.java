// TelaCadastro.java (continuação)
package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.util.Arrays;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class TelaCadastro extends JFrame {
    private JTextField campoNomeUsuario;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmarSenha;
    private JTextField campoTelefone;
    private JLabel labelForcaSenhaTexto;
    private JPanel painelForcaSenhaCor;
    private JButton botaoCadastrarUsuario;
    private JButton botaoCadastrarBazar;
    private TelaLogin telaLogin; // Alterado para TelaLogin

    // Configurações para o banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto"; // Ajuste a URL se necessário
    private static final String DB_USER = "root"; // Seu usuário do MySQL
    private static final String DB_PASSWORD = ""; // Sua senha do MySQL
    private static final String ADMIN_PASSWORD = "admin123";

    public TelaCadastro() {
        setTitle("Cadastrar-se");
        setSize(350, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelCamposUsuario = new JPanel(new GridLayout(5, 2, 10, 10));
        painelCamposUsuario.add(new JLabel("Nome:", SwingConstants.RIGHT));
        campoNomeUsuario = new JTextField();
        painelCamposUsuario.add(campoNomeUsuario);

        painelCamposUsuario.add(new JLabel("Email:", SwingConstants.RIGHT));
        campoEmail = new JTextField();
        painelCamposUsuario.add(campoEmail);

        painelCamposUsuario.add(new JLabel("Senha:", SwingConstants.RIGHT));
        campoSenha = new JPasswordField();
        painelCamposUsuario.add(campoSenha);

        painelCamposUsuario.add(new JLabel("Confirmar Senha:", SwingConstants.RIGHT));
        campoConfirmarSenha = new JPasswordField();
        painelCamposUsuario.add(campoConfirmarSenha);

        painelCamposUsuario.add(new JLabel("Telefone:", SwingConstants.RIGHT));
        campoTelefone = new JTextField();
        painelCamposUsuario.add(campoTelefone);

        labelForcaSenhaTexto = new JLabel(" ");
        labelForcaSenhaTexto.setHorizontalAlignment(SwingConstants.CENTER);

        painelForcaSenhaCor = new JPanel();
        painelForcaSenhaCor.setPreferredSize(new Dimension(100, 10));

        JPanel painelForcaSenha = new JPanel(new BorderLayout());
        painelForcaSenha.add(labelForcaSenhaTexto, BorderLayout.NORTH);
        painelForcaSenha.add(painelForcaSenhaCor, BorderLayout.SOUTH);

        botaoCadastrarUsuario = new JButton("Cadastrar Usuário");
        botaoCadastrarBazar = new JButton("Cadastrar Bazar");

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.add(botaoCadastrarUsuario);
        painelBotoes.add(botaoCadastrarBazar);

        setLayout(new BorderLayout(10, 10));
        add(painelForcaSenha, BorderLayout.NORTH);
        add(painelCamposUsuario, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
        add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        // telaLogin inicializada aqui, será usada após o cadastro de usuário
        // Precisa receber as instâncias de TelaCatalogo e TelaBazar para criar TelaLogin corretamente
        // Isso será feito no método setTelaLogin
        telaLogin = null;

        campoSenha.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                atualizarForcaSenhaVisual();
            }
            public void insertUpdate(DocumentEvent e) {
                atualizarForcaSenhaVisual();
            }
            public void removeUpdate(DocumentEvent e) {
                atualizarForcaSenhaVisual();
            }
        });

        botaoCadastrarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeUsuario = campoNomeUsuario.getText();
                String email = campoEmail.getText();
                char[] senha = campoSenha.getPassword();
                char[] confirmarSenha = campoConfirmarSenha.getPassword();
                String telefone = campoTelefone.getText();

                if (nomeUsuario.isEmpty() || email.isEmpty() || senha.length == 0 || confirmarSenha.length == 0 || telefone.isEmpty()) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!Arrays.equals(senha, confirmarSenha)) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isEmailCadastrado("usuarios", email)) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Este email já está cadastrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (cadastrarUsuario(nomeUsuario, email, new String(senha), telefone)) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Cadastro de Usuário realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    SwingUtilities.invokeLater(() -> {
                        if (telaLogin != null) {
                            telaLogin.setLocationRelativeTo(TelaCadastro.this);
                            telaLogin.mostrar();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "Erro: Tela de Login não inicializada.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Erro ao cadastrar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        botaoCadastrarBazar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPasswordField passwordField = new JPasswordField();
                int option = JOptionPane.showConfirmDialog(TelaCadastro.this, passwordField, "Digite a senha de administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    char[] adminPassword = passwordField.getPassword();
                    if (ADMIN_PASSWORD.equals(new String(adminPassword))) {
                        String nomeResponsavel = campoNomeUsuario.getText();
                        String email = campoEmail.getText();
                        char[] senha = campoSenha.getPassword();
                        char[] confirmarSenha = campoConfirmarSenha.getPassword();
                        String telefone = campoTelefone.getText();

                        if (nomeResponsavel.isEmpty() || email.isEmpty() || senha.length == 0 || confirmarSenha.length == 0 || telefone.isEmpty()) {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "Todos os campos devem ser preenchidos para cadastrar o bazar.", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (!Arrays.equals(senha, confirmarSenha)) {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (isEmailCadastrado("bazares", email)) {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "Este email já está cadastrado como bazar.", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (cadastrarBazar(nomeResponsavel, email, new String(senha), telefone)) {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "Cadastro de Bazar realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            // Opcional: Redirecionar para outra tela ou limpar os campos
                        } else {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "Erro ao cadastrar bazar.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(TelaCadastro.this, "Senha de administrador incorreta.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        setVisible(false);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private boolean cadastrarUsuario(String nome, String email, String senha, String telefone) {
        String sql = "INSERT INTO usuarios (nome_usuario, email, senha, telefone) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, senha);
            pstmt.setString(4, telefone);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
            return false;
        }
    }

    private boolean cadastrarBazar(String nomeResponsavel, String email, String senha, String telefone) {
        String sql = "INSERT INTO bazares (nome_responsavel, email, senha, telefone) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomeResponsavel);
            pstmt.setString(2, email);
            pstmt.setString(3, senha);
            pstmt.setString(4, telefone);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar bazar: " + e.getMessage());
            return false;
        }
    }

    private boolean isEmailCadastrado(String tabela, String email) {
        String sql = "SELECT email FROM " + tabela + " WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }

    private void atualizarForcaSenhaVisual() {
        String senha = new String(campoSenha.getPassword());
        int forca = calcularForcaSenha(senha);
        String textoForca;
        Color corForca;

        if (senha.isEmpty()) {
            textoForca = " ";
            corForca = getBackground();
        } else if (forca < 30) {
            textoForca = "Fraca";
            corForca = Color.RED;
        } else if (forca < 60) {
            textoForca = "Média";
            corForca = Color.YELLOW;
        } else {
            textoForca = "Forte";
            corForca = Color.GREEN;
        }

        labelForcaSenhaTexto.setText("Força da Senha: " + textoForca);
        painelForcaSenhaCor.setBackground(corForca);
    }

    private int calcularForcaSenha(String senha) {
        int forca = 0;
        if (senha.length() >= 8) forca += 20;
        if (senha.matches(".*[a-z].*")) forca += 20;
        if (senha.matches(".*[A-Z].*")) forca += 20;
        if (senha.matches(".*\\d.*")) forca += 20;
        if (senha.matches(".*[^a-zA-Z0-9].*")) forca += 20;
        return forca;
    }

    // Método para receber a instância de TelaLogin
    public void setTelaLogin(TelaLogin telaLogin) {
        this.telaLogin = telaLogin;
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastro().mostrar());
    }
}