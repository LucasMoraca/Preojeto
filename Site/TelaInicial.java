package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A classe {@code TelaInicial} representa a tela principal do aplicativo Bazar Online.
 * Esta tela exibe o título do bazar, uma descrição sobre sua proposta solidária
 * e botões para o usuário fazer login, cadastrar-se ou navegar pelo catálogo sem login.
 */
public class TelaInicial extends JFrame {

    /**
     * Construtor da classe {@code TelaInicial}. Inicializa a interface gráfica da tela principal.
     */
    public TelaInicial() {
        // Define o título da janela
        setTitle("Bazar Online - Tela Inicial");
        // Define a operação padrão ao fechar a janela (encerrar o aplicativo)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Define um tamanho inicial para a janela
        setSize(700, 500);
        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Cria um painel principal com um layout BorderLayout para organizar os componentes
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout());

        // Cria um rótulo para o título do bazar, centralizado
        JLabel tituloLabel = new JLabel("Nome do Bazar", SwingConstants.CENTER);
        // Define a fonte do título (Arial, negrito, tamanho 24)
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Cria uma área de texto para a descrição do bazar
        JTextArea descricaoTextArea = new JTextArea(
                "Transforme seu guarda-roupa e o mundo com o nosso Bazar Solidário!\n\n" +
                "Em um momento de reflexão global, convidamos você a fazer parte de uma revolução na\n" +
                "moda. Nosso Bazar Solidário é mais que uma troca de roupas: é um movimento em\n" +
                "direção a um futuro mais verde e justo.\n\n" +
                "Descubra a moda ecológica: valorizamos a beleza da reutilização, a força da doação e a\n" +
                "urgência da sustentabilidade. Dê um novo lar àquela peça especial e encontre tesouros\n" +
                "únicos, tudo enquanto contribui para um planeta mais saudável e uma comunidade mais\n" +
                "forte.\n\n" +
                "Participe da economia circular: aqui, seus itens ganham nova vida, evitando o desperdício\n" +
                "e inspirando um consumo consciente.\n\n" +
                "Junte-se a nós: seja parte da mudança, adote um estilo com propósito e mostre que a\n" +
                "moda pode ser uma poderosa ferramenta de transformação social e ambiental.\n\n" +
                "Bazar Solidário: Vista essa ideia!");
        // Impede que o usuário edite a área de texto
        descricaoTextArea.setEditable(false);
        // Ativa a quebra automática de linha
        descricaoTextArea.setLineWrap(true);
        // Quebra as linhas nas palavras
        descricaoTextArea.setWrapStyleWord(true);

        // Cria um painel para os botões com um layout FlowLayout centralizado
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        // Cria os botões para login, cadastro e navegação sem login
        JButton loginButton = new JButton("Login");
        JButton cadastrarButton = new JButton("Cadastrar-se");
        JButton navegarButton = new JButton("Navegar sem login");

        /**
         * Adiciona um {@code ActionListener} ao botão "Login".
         * Ao ser clicado, cria e exibe uma instância da classe {@code TelaLogin}.
         */
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria e exibe a TelaLogin
                TelaLogin telaDeLogin = new TelaLogin();
                telaDeLogin.setVisible(true);
            }
        });

        /**
         * Adiciona um {@code ActionListener} ao botão "Cadastrar-se".
         * Ao ser clicado, cria e exibe uma instância da classe {@code TelaCadastro}.
         */
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria e exibe a TelaCadastro (corrigido o nome da classe)
                TelaCadastro telaDeCadastro = new TelaCadastro();
                telaDeCadastro.setVisible(true);
            }
        });

        /**
         * Adiciona um {@code ActionListener} ao botão "Navegar sem login".
         * Ao ser clicado, cria e exibe uma instância da classe {@code TelaCatalogo}.
         */
        navegarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria e exibe a TelaCatalogo
                TelaCatalogo telaDeCatalogo = new TelaCatalogo();
                telaDeCatalogo.setVisible(true);
            }
        });

        // Adiciona os botões ao painel de botões
        botoesPanel.add(loginButton);
        botoesPanel.add(cadastrarButton);
        botoesPanel.add(navegarButton);

        // Adiciona os componentes ao painel principal nas suas respectivas posições no BorderLayout
        painelPrincipal.add(tituloLabel, BorderLayout.NORTH);
        painelPrincipal.add(new JScrollPane(descricaoTextArea), BorderLayout.CENTER); // Adiciona a área de texto com scroll
        painelPrincipal.add(botoesPanel, BorderLayout.SOUTH);

        // Adiciona o painel principal ao JFrame
        add(painelPrincipal);
        // Torna a janela visível
        setVisible(true);
    }

    /**
     * Método principal que inicia a aplicação {@code TelaInicial}.
     * Utiliza {@code SwingUtilities.invokeLater} para garantir que a criação e exibição
     * da interface gráfica ocorram na thread de despacho de eventos (Event Dispatch Thread - EDT).
     *
     * @param args Argumentos da linha de comando (não utilizados neste aplicativo).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicial());
    }
}