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
    private TelaBazar telaBazar; // Adicionando referência para TelaBazar

    public TelaInicial() {
        setTitle("Bazar Online - Tela Inicial");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setLocationRelativeTo(null);

        nomeBazarLabel = new JLabel("Nome do Bazar");
        nomeBazarLabel.setFont(new Font("Arial", Font.BOLD, 24));

        historicoBazarTextArea = new JTextArea("Transforme seu guarda-roupa e o mundo com o nosso Bazar Solidário!\n" + "\n" + "Em um momento de reflexão global, convidamos você a fazer parte de uma revolução na moda. Nosso Bazar Solidário é mais que uma troca de roupas: é um movimento em direção a um futuro mais verde e justo.\n" + "\n" + "Descubra a moda ecológica: valorizamos a beleza da reutilização, a força da doação e a urgência da sustentabilidade. Dê um novo lar àquela peça especial e encontre tesouros únicos, tudo enquanto contribui para um planeta mais saudável e uma comunidade mais forte.\n" + "\n" + "Participe da economia circular: aqui, seus itens ganham nova vida, evitando o desperdício e inspirando um consumo consciente.\n" + "\n" + "Junte-se a nós: seja parte da mudança, adote um estilo com propósito e mostre que a moda pode ser uma poderosa ferramenta de transformação social e ambiental.\n" + "\n" + "Bazar Solidário: Vista essa ideia!");
        historicoBazarTextArea.setLineWrap(true);
        historicoBazarTextArea.setWrapStyleWord(true);
        historicoBazarTextArea.setPreferredSize(new Dimension(500, 300));
        historicoBazarTextArea.setEditable(false);

        loginButton = new JButton("Login");
        cadastrarButton = new JButton("Cadastrar-se");
        navegarButton = new JButton("Navegar sem login");

        // Inicializar as telas
        telaCatalogo = new TelaCatalogo();
        telaBazar = new TelaBazar(); // Inicializa TelaBazar
        telaCadastro = new TelaCadastro();
        telaLogin = new TelaLogin(telaCatalogo, telaBazar, telaCadastro); // Passa as referências corretas

        // Passar as referências necessárias
        telaCadastro.setTelaLogin(telaLogin);
        // telaCadastro.setTelaCatalogo(telaCatalogo); // Não precisa mais voltar direto para o catálogo

        // Adicionar ActionListener aos botões
        loginButton.addActionListener(this);
        cadastrarButton.addActionListener(this);
        navegarButton.addActionListener(this);

        // Adicionar componentes ao JFrame da tela inicial
        add(nomeBazarLabel);
        add(historicoBazarTextArea);
        add(loginButton);
        add(cadastrarButton);
        add(navegarButton);

        // Tornar a janela inicial visível
        setVisible(true);
    }

    public static void main(String[] args) {
        // Executa a tela inicial na thread de despacho de eventos (EDT)
        SwingUtilities.invokeLater(() -> new TelaInicial());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            telaLogin.setLocationRelativeTo(this);
            telaLogin.mostrar();
        } else if (e.getSource() == cadastrarButton) {
            telaCadastro.setLocationRelativeTo(this);
            telaCadastro.mostrar();
        } else if (e.getSource() == navegarButton) {
            telaCatalogo.setLocationRelativeTo(this);
            telaCatalogo.mostrar();
        }
    }
}