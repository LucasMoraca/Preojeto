package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.util.Arrays;

public class TelaCadastro extends JFrame {
    // Declaração dos componentes da interface gráfica
    private JTextField campoNomeUsuario;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmarSenha;
    private JTextField campoTelefone;
    private JLabel labelForcaSenhaTexto;
    private JPanel painelForcaSenhaCor;
    private JButton botaoCadastrarUsuario; // Renomeei o botão de cadastro de usuário
    private JButton botaoCadastrarBazar;
    private TelaCatalogo telaCatalogo;

    // Construtor da classe TelaCadastro
    public TelaCadastro() {
        setTitle("Cadastrar-se");
        setSize(350, 360); // Aumentei a altura para o novo botão
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel para os campos de cadastro de usuário
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

        telaCatalogo = new TelaCatalogo();

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

        // ActionListener para o cadastro de usuário (como antes)
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

                if (verificarEmailExistente(email)) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Este email já está cadastrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                System.out.println("Cadastro de Usuário realizado com:");
                System.out.println("Nome: " + nomeUsuario);
                System.out.println("Email: " + email);
                System.out.println("Senha: " + new String(senha));
                System.out.println("Telefone: " + telefone);
                JOptionPane.showMessageDialog(TelaCadastro.this, "Cadastro de Usuário realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                SwingUtilities.invokeLater(() -> {
                    telaCatalogo.setLocationRelativeTo(TelaCadastro.this);
                    telaCatalogo.mostrar();
                    dispose();
                });
            }
        });

        // ActionListener para o cadastro de bazar
        botaoCadastrarBazar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre uma caixa de diálogo para inserir a senha de administrador
                JPasswordField passwordField = new JPasswordField();
                int option = JOptionPane.showConfirmDialog(TelaCadastro.this, passwordField, "Digite a senha de administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    char[] adminPassword = passwordField.getPassword();
                    // Aqui você deve comparar com a senha de administrador real
                    if (isSenhaAdminCorreta(new String(adminPassword))) {
                        String nomeUsuario = campoNomeUsuario.getText();
                        String email = campoEmail.getText();
                        char[] senha = campoSenha.getPassword();
                        char[] confirmarSenha = campoConfirmarSenha.getPassword();
                        String telefone = campoTelefone.getText();

                        if (nomeUsuario.isEmpty() || email.isEmpty() || senha.length == 0 || confirmarSenha.length == 0 || telefone.isEmpty()) {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "Todos os campos devem ser preenchidos para cadastrar o bazar.", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (!Arrays.equals(senha, confirmarSenha)) {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (verificarEmailExistente(email)) {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "Este email já está cadastrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Lógica para cadastrar o bazar (você precisará implementar isso)
                        System.out.println("Solicitação de Cadastro de Bazar com:");
                        System.out.println("Nome: " + nomeUsuario);
                        System.out.println("Email: " + email);
                        System.out.println("Senha: " + new String(senha));
                        System.out.println("Telefone: " + telefone);
                        JOptionPane.showMessageDialog(TelaCadastro.this, "Solicitação de Cadastro de Bazar enviada para aprovação!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        // Aqui você pode adicionar a lógica para, por exemplo, salvar essa solicitação em um estado pendente.

                    } else {
                        JOptionPane.showMessageDialog(TelaCadastro.this, "Senha de administrador incorreta.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        setVisible(false);
    }

    // Método para verificar se a senha de administrador está correta
    private boolean isSenhaAdminCorreta(String senhaDigitada) {
        // *** IMPORTANTE: Em uma aplicação real, NUNCA codifique a senha diretamente no código.
        // *** Use métodos seguros como hashing com salt para armazenar e verificar senhas.
        String senhaAdminCorreta = "admin123"; // Senha de administrador de exemplo
        return senhaDigitada.equals(senhaAdminCorreta);
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

    private boolean verificarEmailExistente(String email) {
        String[] emailsExistentes = {"teste@email.com", "outro@email.com"};
        for (String e : emailsExistentes) {
            if (e.equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastro().mostrar());
    }
}