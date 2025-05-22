package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaBazar extends JFrame {

    public TelaBazar() {
        setTitle("Página do Bazar");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50)); // Layout para centralizar os botões

        JButton adicionarProdutoButton = new JButton("Adicionar Produto");
        JButton editarProdutoButton = new JButton("Editar Produto");

        adicionarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre a TelaAdicionarProduto
                TelaAdicionarProduto telaAdicionarProduto = new TelaAdicionarProduto();
                telaAdicionarProduto.setVisible(true);
            }
        });

        editarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre a TelaEditarProduto em tela cheia
                TelaEditarProduto telaEditarProduto = new TelaEditarProduto();
                telaEditarProduto.setVisible(true);
            }
        });

        add(adicionarProdutoButton);
        add(editarProdutoButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaBazar());
    }
}