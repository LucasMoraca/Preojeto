package Site;

import javax.swing.*;
import java.awt.*;

public class TelaCadastro extends JFrame {
    private JTextField campoNomeUsuario;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoCadastrar;

    public TelaCadastro() {
        setTitle("Cadastrar-se");
        setSize(350, 250); // Aumentando um pouco a altura
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar na tela ao ser criada

        JPanel painelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        painelCampos.add(new JLabel("Nome:", SwingConstants.RIGHT));
        campoNomeUsuario = new JTextField();
        painelCampos.add(campoNomeUsuario);

        painelCampos.add(new JLabel("Email:", SwingConstants.RIGHT));
        campoEmail = new JTextField();
        painelCampos.add(campoEmail);

        painelCampos.add(new JLabel("Senha:", SwingConstants.RIGHT));
        campoSenha = new JPasswordField();
        painelCampos.add(campoSenha);

        botaoCadastrar = new JButton("Cadastrar");

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.add(botaoCadastrar);

        setLayout(new BorderLayout(10, 10));
        add(painelCampos, BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
        add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        botaoCadastrar.addActionListener(e -> {
            String nomeUsuario = campoNomeUsuario.getText();
            String email = campoEmail.getText();
            String senha = new String(campoSenha.getPassword());

            // Aqui você chamaria a lógica de cadastro
            System.out.println("Tentativa de cadastro com Nome: " + nomeUsuario +
                               ", Email: " + email + ", Senha: " + senha);
            JOptionPane.showMessageDialog(this, "Implementar lógica de cadastro aqui.");
            // Após o cadastro bem-sucedido, você pode fechar esta tela
            // dispose();
        });

        setVisible(false);
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastro().setVisible(true));
    }
}