// TelaInicial.java
package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaInicial extends JFrame implements ActionListener {

    private JLabel nomeBazarLabel;
    private JTextArea historicoBazarTextArea;
    private JButton loginButton;
    private JButton cadastrarButton;
    private JButton navegarButton;
    private TelaLogin telaLogin;
    private TelaCadastro telaCadastro;
    private TelaCatalogo telaCatalogo;
    private TelaBazar telaBazar;

    public TelaInicial() {
        setTitle("Bazar Online - Tela Inicial");
        setSize(600, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarTelas();
        inicializarComponentes();
        configurarLayout();

        setVisible(true);
    }

    private void inicializarTelas() {
        telaCatalogo = new TelaCatalogo();
        telaBazar = new TelaBazar();
        telaLogin = new TelaLogin(telaCatalogo, telaBazar);
        telaCadastro = new TelaCadastro();
        telaCadastro.setTelaCatalogo(telaCatalogo);
    }

    private void inicializarComponentes() {
        nomeBazarLabel = new JLabel("Bazar Solidário", SwingConstants.CENTER);
        nomeBazarLabel.setFont(new Font("Arial", Font.BOLD, 26));

        historicoBazarTextArea = new JTextArea(
            "Transforme seu guarda-roupa e o mundo com o nosso Bazar Solidário!\n\n" +
            "Descubra a moda ecológica e participe da economia circular. Aqui, seus itens ganham nova vida, " +
            "evitando o desperdício e promovendo um consumo consciente.\n\n" +
            "Junte-se a nós e adote um estilo com propósito!\n\n" +
            "Bazar Solidário: Vista essa ideia!"
        );
        historicoBazarTextArea.setLineWrap(true);
        historicoBazarTextArea.setWrapStyleWord(true);
        historicoBazarTextArea.setEditable(false);
        historicoBazarTextArea.setFont(new Font("Serif", Font.PLAIN, 14));
        historicoBazarTextArea.setBackground(new Color(248, 248, 248));

        loginButton = new JButton("Login");
        cadastrarButton = new JButton("Cadastrar-se");
        navegarButton = new JButton("Navegar sem login");

        loginButton.addActionListener(this);
        cadastrarButton.addActionListener(this);
        navegarButton.addActionListener(this);
    }

    private void configurarLayout() {
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nomeBazarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(historicoBazarTextArea);
        scroll.setPreferredSize(new Dimension(540, 300));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelBotoes.add(loginButton);
        painelBotoes.add(cadastrarButton);
        painelBotoes.add(navegarButton);

        painelPrincipal.add(nomeBazarLabel);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 15)));
        painelPrincipal.add(scroll);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        painelPrincipal.add(painelBotoes);

        setContentPane(painelPrincipal);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaInicial::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origem = e.getSource();
        if (origem == loginButton) {
            telaLogin.setLocationRelativeTo(this);
            telaLogin.mostrar();
        } else if (origem == cadastrarButton) {
            telaCadastro.setLocationRelativeTo(this);
            telaCadastro.mostrar();
        } else if (origem == navegarButton) {
            telaCatalogo.setLocationRelativeTo(this);
            telaCatalogo.mostrar();
        }
    }
}
