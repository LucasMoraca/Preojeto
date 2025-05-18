package Site;

import javax.swing.*;
import java.awt.*;

public class TelaCadastro extends JFrame {
    private JTextField campoNomeUsuario;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoCadastrar;
    private TelaCatalogo telaCatalogo; // Adicione esta linha

    public TelaCadastro() {
        setTitle("Cadastrar-se");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

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

        // Inicialize a TelaCatalogo aqui
        telaCatalogo = new TelaCatalogo();

        botaoCadastrar.addActionListener(e -> {
            String nomeUsuario = campoNomeUsuario.getText();
            String email = campoEmail.getText();
            String senha = new String(campoSenha.getPassword());

            // Aqui você chamaria a lógica de cadastro
            System.out.println("Tentativa de cadastro com Nome: " + nomeUsuario +
                               ", Email: " + email + ", Senha: " + senha);
            JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!");
            // Após o cadastro bem-sucedido, abrir TelaCatalogo
            SwingUtilities.invokeLater(() -> {
                telaCatalogo.setLocationRelativeTo(this);
                telaCatalogo.mostrar();
                dispose(); // Fechar a tela de cadastro
            });
        });

        setVisible(false); // Visibilidade inicial controlada por mostrar()
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastro().mostrar()); // Usando o método mostrar()
    }
}