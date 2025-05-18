package Site;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoEntrar;
    private TelaCatalogo telaCatalogo; // Mantenha a referência

    public TelaLogin() {
        setTitle("Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
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
        add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        // Inicialize a TelaCatalogo aqui - REMOVA ESTA LINHA DO CONSTRUTOR
        // telaCatalogo = new TelaCatalogo();

        botaoEntrar.addActionListener(e -> {
            String email = campoEmail.getText();
            String senha = new String(campoSenha.getPassword());

            if ("teste@email.com".equals(email) && "123".equals(senha)) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
                SwingUtilities.invokeLater(() -> {
                    // Certifique-se de que telaCatalogo foi definida antes de usar
                    if (telaCatalogo != null) {
                        telaCatalogo.setLocationRelativeTo(this);
                        telaCatalogo.mostrar();
                        dispose();
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, "Email ou senha incorretos.");
            }
        });

        setVisible(false);
    }

    // Método para receber a instância de TelaCatalogo
    public void setTelaCatalogo(TelaCatalogo telaCatalogo) {
        this.telaCatalogo = telaCatalogo;
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().mostrar());
    }
}