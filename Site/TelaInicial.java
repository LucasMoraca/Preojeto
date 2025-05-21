// TelaInicial.java
package Site;

import javax.swing.*; // Importa classes para criar interfaces gráficas Swing
import java.awt.*; // Importa classes para layouts e componentes gráficos AWT
import java.awt.event.ActionEvent; // Importa a classe para eventos de ação (como cliques de botão)
import java.awt.event.ActionListener; // Importa a interface para lidar com eventos de ação

// A classe TelaInicial herda de JFrame (janela principal) e implementa ActionListener (para lidar com eventos de botões)
public class TelaInicial extends JFrame implements ActionListener {

    // Declaração de componentes da interface gráfica
    private JLabel nomeBazarLabel; // Rótulo para exibir o nome do bazar
    private JTextArea historicoBazarTextArea; // Área de texto para exibir o histórico/descrição do bazar
    private JButton loginButton; // Botão para ir para a tela de login
    private JButton cadastrarButton; // Botão para ir para a tela de cadastro
    private JButton navegarButton; // Botão para navegar pelo catálogo sem fazer login

    // Declaração de referências para outras telas
    private TelaLogin telaLogin;
    private TelaCadastro telaCadastro;
    private TelaCatalogo telaCatalogo;
    private TelaBazar telaBazar; // Adicionando referência para a classe TelaBazar

    // Construtor da classe TelaInicial
    public TelaInicial() {
        setTitle("Bazar Online - Tela Inicial"); // Define o título da janela
        setSize(550, 500); // Define o tamanho inicial da janela (largura 550, altura 500 pixels)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Define o comportamento padrão ao fechar a janela (encerrar a aplicação)
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Define o layout como FlowLayout, centralizado, com espaçamento horizontal e vertical de 10 pixels entre os componentes
        setLocationRelativeTo(null); // Centraliza a janela na tela

        // Criação e configuração do rótulo do nome do bazar
        nomeBazarLabel = new JLabel("Nome do Bazar");
        nomeBazarLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Define a fonte como Arial, negrito, tamanho 24

        // Criação e configuração da área de texto do histórico do bazar
        historicoBazarTextArea = new JTextArea("Transforme seu guarda-roupa e o mundo com o nosso Bazar Solidário!\n" + "\n" + "Em um momento de reflexão global, convidamos você a fazer parte de uma revolução na moda. Nosso Bazar Solidário é mais que uma troca de roupas: é um movimento em direção a um futuro mais verde e justo.\n" + "\n" + "Descubra a moda ecológica: valorizamos a beleza da reutilização, a força da doação e a urgência da sustentabilidade. Dê um novo lar àquela peça especial e encontre tesouros únicos, tudo enquanto contribui para um planeta mais saudável e uma comunidade mais forte.\n" + "\n" + "Participe da economia circular: aqui, seus itens ganham nova vida, evitando o desperdício e inspirando um consumo consciente.\n" + "\n" + "Junte-se a nós: seja parte da mudança, adote um estilo com propósito e mostre que a moda pode ser uma poderosa ferramenta de transformação social e ambiental.\n" + "\n" + "Bazar Solidário: Vista essa ideia!");
        historicoBazarTextArea.setLineWrap(true); // Quebra as linhas longas automaticamente
        historicoBazarTextArea.setWrapStyleWord(true); // Quebra as linhas nas palavras
        historicoBazarTextArea.setPreferredSize(new Dimension(500, 300)); // Define o tamanho preferido da área de texto
        historicoBazarTextArea.setEditable(false); // Impede que o usuário edite o texto

        // Criação dos botões
        loginButton = new JButton("Login");
        cadastrarButton = new JButton("Cadastrar-se");
        navegarButton = new JButton("Navegar sem login");

        // Inicializar as telas
        telaCatalogo = new TelaCatalogo(); // Cria uma instância da TelaCatalogo
        telaBazar = new TelaBazar(); // Inicializa uma instância da TelaBazar
        telaCadastro = new TelaCadastro(); // Cria uma instância da TelaCadastro
        telaLogin = new TelaLogin(telaCatalogo, telaBazar, telaCadastro); // Cria uma instância da TelaLogin, passando referências para outras telas

        // Passar as referências necessárias
        telaCadastro.setTelaLogin(telaLogin); // Permite que a TelaCadastro acesse a TelaLogin (por exemplo, para voltar após o cadastro)
        // telaCadastro.setTelaCatalogo(telaCatalogo); // Comentado: Não precisa mais voltar direto para o catálogo a partir do cadastro

        // Adicionar ActionListener aos botões para que a classe TelaInicial lide com os eventos de clique
        loginButton.addActionListener(this);
        cadastrarButton.addActionListener(this);
        navegarButton.addActionListener(this);

        // Adicionar os componentes (rótulo, área de texto, botões) ao JFrame da tela inicial
        add(nomeBazarLabel);
        add(historicoBazarTextArea);
        add(loginButton);
        add(cadastrarButton);
        add(navegarButton);

        // Tornar a janela inicial visível
        setVisible(true);
    }

    // Método main, ponto de entrada da aplicação
    public static void main(String[] args) {
        // Executa a criação e exibição da TelaInicial na Event Dispatch Thread (EDT)
        // Isso garante que a interface gráfica seja manipulada de forma segura e thread-safe
        SwingUtilities.invokeLater(() -> new TelaInicial());
    }

    // Método da interface ActionListener, chamado quando um evento de ação ocorre (um botão é clicado)
    @Override
    public void actionPerformed(ActionEvent e) {
        // Verifica qual botão foi clicado
        if (e.getSource() == loginButton) {
            telaLogin.setLocationRelativeTo(this); // Centraliza a tela de login em relação à tela inicial
            telaLogin.mostrar(); // Chama um método (provavelmente na TelaLogin) para tornar a tela de login visível
        } else if (e.getSource() == cadastrarButton) {
            telaCadastro.setLocationRelativeTo(this); // Centraliza a tela de cadastro em relação à tela inicial
            telaCadastro.mostrar(); // Chama um método (provavelmente na TelaCadastro) para tornar a tela de cadastro visível
        } else if (e.getSource() == navegarButton) {
            telaCatalogo.setLocationRelativeTo(this); // Centraliza a tela de catálogo em relação à tela inicial
            telaCatalogo.mostrar(); // Chama um método (provavelmente na TelaCatalogo) para tornar a tela de catálogo visível
        }
    }
}