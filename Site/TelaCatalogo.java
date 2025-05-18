package Site;

import javax.swing.*;
import java.awt.*;

public class TelaCatalogo extends JFrame {

    private JPanel painelLateralFiltros;
    private JTextField campoPesquisa;
    private JPanel painelListagemItens;
    private JButton botaoPerfil;
    private JButton botaoCarrinho;
    private JButton botaoDoar;
    private JButton botaoNotificacoes;
    private TelaLogin telaLogin; // Referência para a tela de login

    public TelaCatalogo() {
        setTitle("Catálogo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Inicializar telas que podem ser abertas a partir daqui - REMOVA ESTA LINHA DO CONSTRUTOR
        // telaLogin = new TelaLogin();

        // Painel superior para ícones e barra de pesquisa
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        campoPesquisa = new JTextField(20);
        JButton botaoBuscar = new JButton("Buscar");
        botaoPerfil = new JButton("Perfil");
        botaoCarrinho = new JButton("Carrinho");
        botaoDoar = new JButton("Doar");
        botaoNotificacoes = new JButton("Notificações");

        painelSuperior.add(campoPesquisa);
        painelSuperior.add(botaoBuscar);
        painelSuperior.add(botaoPerfil);
        painelSuperior.add(botaoCarrinho);
        painelSuperior.add(botaoDoar);
        painelSuperior.add(botaoNotificacoes);

        add(painelSuperior, BorderLayout.NORTH);

        botaoBuscar.addActionListener(e -> {
            String textoPesquisa = campoPesquisa.getText();
            System.out.println("Pesquisando por: " + textoPesquisa);
            JOptionPane.showMessageDialog(this, "Buscar por: " + textoPesquisa + " (a implementar)");
        });

        painelLateralFiltros = new JPanel();
        painelLateralFiltros.setPreferredSize(new Dimension(200, getHeight()));
        painelLateralFiltros.setLayout(new BoxLayout(painelLateralFiltros, BoxLayout.Y_AXIS));
        painelLateralFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        painelLateralFiltros.add(new JLabel("Categorias:"));
        painelLateralFiltros.add(new JCheckBox("Roupas"));
        painelLateralFiltros.add(new JCheckBox("Sapatos"));
        painelLateralFiltros.add(new JCheckBox("Acessórios"));
        painelLateralFiltros.add(Box.createVerticalStrut(10));

        painelLateralFiltros.add(new JLabel("Tamanhos:"));
        painelLateralFiltros.add(new JCheckBox("P"));
        painelLateralFiltros.add(new JCheckBox("M"));
        painelLateralFiltros.add(new JCheckBox("G"));
        painelLateralFiltros.add(Box.createVerticalStrut(10));

        painelLateralFiltros.add(new JLabel("Valor Médio:"));
        painelLateralFiltros.add(new JSlider(0, 100, 50));
        painelLateralFiltros.add(new JLabel("R$ 0 - R$ 100"));

        add(painelLateralFiltros, BorderLayout.WEST);

        painelListagemItens = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        painelListagemItens.setBorder(BorderFactory.createTitledBorder("Itens do Catálogo"));
        for (int i = 1; i <= 20; i++) {
            painelListagemItens.add(new JLabel("Item " + i));
        }
        JScrollPane scrollPane = new JScrollPane(painelListagemItens);
        add(scrollPane, BorderLayout.CENTER);

        botaoPerfil.addActionListener(e -> {
            boolean usuarioLogado = false; // Substitua pela sua lógica real
            if (usuarioLogado) {
                JOptionPane.showMessageDialog(this, "Abrir tela de perfil (a implementar)");
            } else {
                // Certifique-se de que telaLogin foi definida antes de usar
                if (telaLogin != null) {
                    telaLogin.setLocationRelativeTo(this);
                    telaLogin.mostrar();
                }
            }
        });

        botaoCarrinho.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abrir tela do carrinho (a implementar)");
        });

        botaoDoar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abrir tela de doação (a implementar)");
        });

        botaoNotificacoes.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abrir tela de notificações (a implementar)");
        });

        setVisible(false);
    }

    // Método para receber a instância de TelaLogin
    public void setTelaLogin(TelaLogin telaLogin) {
        this.telaLogin = telaLogin;
    }

    public void mostrar() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCatalogo().mostrar());
    }
}