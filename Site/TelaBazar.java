package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaBazar extends JFrame implements ActionListener {

    private JButton adicionarProdutoButton;
    private JButton editarProdutoButton;
    private TelaAdicionarProduto telaAdicionarProduto;
    private TelaEditarProduto telaEditarProduto;

    public TelaBazar() {
        setTitle("Página do Bazar");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        adicionarProdutoButton = new JButton("Adicionar Produto");
        editarProdutoButton = new JButton("Editar Produto");

        telaAdicionarProduto = new TelaAdicionarProduto();
        telaEditarProduto = new TelaEditarProduto();

        adicionarProdutoButton.addActionListener(this);
        editarProdutoButton.addActionListener(this);

        add(adicionarProdutoButton);
        add(editarProdutoButton);

        setVisible(false);
    }

    public void mostrar() {
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adicionarProdutoButton) {
            telaAdicionarProduto.setLocationRelativeTo(this);
            telaAdicionarProduto.mostrar();
        } else if (e.getSource() == editarProdutoButton) {
            telaEditarProduto.setLocationRelativeTo(this);
            telaEditarProduto.mostrar();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaBazar().mostrar());
    }
}
