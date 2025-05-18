package Site;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoEntrar;

    public TelaLogin() {
        setTitle("Login");
        setSize(350, 200); // Aumentando um pouco o tamanho
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar na tela ao ser criada

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

        setLayout(new BorderLayout(10, 10)); // Layout principal BorderLayout
        add(painelCampos, BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
        add(Box.createVerticalStrut(10), BorderLayout.NORTH); // Espaçamento superior
        add(Box.createHorizontalStrut(10), BorderLayout.WEST);  // Espaçamento esquerdo
        add(Box.createHorizontalStrut(10), BorderLayout.EAST); // Espaçamento direito

        botaoEntrar.addActionListener(e -> {
            String email = campoEmail.getText();
            String senha = new String(campoSenha.getPassword());

            // Aqui você chamaria a lógica de autenticação
            System.out.println("Tentativa de login com Email: " + email + ", Senha: " + senha);
            JOptionPane.showMessageDialog(this, "Implementar lógica de login aqui.");
            // Se a autenticação fosse bem-sucedida, você poderia fechar esta tela
            // dispose();
        });

        setVisible(false);
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}