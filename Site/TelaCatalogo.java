package Site;

import javax.swing.*;
import java.awt.*;

public class TelaCatalogo extends JFrame {

    private JPanel painelLateralFiltros;
    private JPanel painelListagemItens;
    private JButton botaoPerfil;
    private JButton botaoCarrinho;
    private TelaLogin telaLogin; // Referência para a tela de login

    public TelaCatalogo() {
        setTitle("Catálogo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Painel superior para ícones
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        botaoPerfil = new JButton("Perfil");
        botaoCarrinho = new JButton("Carrinho");

        painelSuperior.add(botaoPerfil);
        painelSuperior.add(botaoCarrinho);

        add(painelSuperior, BorderLayout.NORTH);

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

        add(painelLateralFiltros, BorderLayout.WEST);

        painelListagemItens = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        painelListagemItens.setBorder(BorderFactory.createTitledBorder("Itens do Catálogo"));
        for (int i = 1; i <= 20; i++) {
            painelListagemItens.add(new JLabel("Item " + i));
        }
        JScrollPane scrollPane = new JScrollPane(painelListagemItens);
        add(scrollPane, BorderLayout.CENTER);

        botaoPerfil.addActionListener(e -> {
            // Simulação de verificação de login
            boolean usuarioLogado = true; // Mude para sua lógica real de verificação de login
            if (!usuarioLogado) {
                // Se não estiver logado, mostra a tela de login
                if (telaLogin != null) {
                    telaLogin.setLocationRelativeTo(this);
                    telaLogin.mostrar();
                } else {
                    JOptionPane.showMessageDialog(this, "Você precisa estar logado para acessar o perfil.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    // Opcional: Criar e mostrar TelaLogin aqui se ela não foi passada
                }
            } else {
                JOptionPane.showMessageDialog(this, "Abrir tela de perfil (a implementar)");
            }
        });

        botaoCarrinho.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abrir tela do carrinho (a implementar)");
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