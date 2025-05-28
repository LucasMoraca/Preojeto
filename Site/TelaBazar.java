package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * {@code TelaBazar} é uma janela que oferece funcionalidades exclusivas para usuários do tipo "bazar".
 * Ela permite adicionar, editar produtos e visualizar os pedidos realizados.
 */
public class TelaBazar extends JFrame {

    /**
     * Construtor da classe {@code TelaBazar}.
     * Inicializa a interface gráfica com botões para adicionar produto, editar produto e visualizar pedidos.
     */
    public TelaBazar() {
        setTitle("Página do Bazar");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250); // Aumentei a altura para acomodar o novo botão
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50)); // Layout para centralizar os botões

        JButton adicionarProdutoButton = new JButton("Adicionar Produto");
        JButton editarProdutoButton = new JButton("Editar Produto");
        JButton pedidosButton = new JButton("Pedidos"); // Novo botão

        /**
         * Listener para o botão "Adicionar Produto".
         * Ao ser clicado, abre a {@code TelaAdicionarProduto}.
         */
        adicionarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaAdicionarProduto telaAdicionarProduto = new TelaAdicionarProduto();
                telaAdicionarProduto.setVisible(true);
            }
        });

        /**
         * Listener para o botão "Editar Produto".
         * Ao ser clicado, abre a {@code TelaEditarProduto}.
         */
        editarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaEditarProduto telaEditarProduto = new TelaEditarProduto();
                telaEditarProduto.setVisible(true);
            }
        });

        /**
         * Listener para o botão "Pedidos".
         * Ao ser clicado, abre a {@code TelaPedidos}.
         */
        pedidosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaPedidos telaPedidos = new TelaPedidos();
                telaPedidos.setVisible(true);
            }
        });

        add(adicionarProdutoButton);
        add(editarProdutoButton);
        add(pedidosButton); // Adiciona o novo botão

        setVisible(true);
    }

    /**
     * Método principal para criar e exibir a {@code TelaBazar}.
     * Executa a criação da interface gráfica na thread de despacho de eventos (EDT).
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaBazar());
    }
}