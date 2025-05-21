// TelaCarrinho.java
// TelaCarrinho.java
package Site;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

public class TelaCarrinho extends JFrame implements ActionListener, ListSelectionListener {

    private DefaultListModel<ItemCarrinho> carrinhoListModel;
    private JList<ItemCarrinho> carrinhoJList;
    private JButton removerItemButton;
    private JButton alterarQuantidadeButton; // Novo botão
    private JLabel totalLabel;
    private JTextArea enderecoTextArea;
    private JButton confirmarCompraButton;
    private List<ItemCarrinho> itensCarrinho; // Lista para manipulação dos itens

    public TelaCarrinho() {
        setTitle("Carrinho de Compras");
        setSize(600, 450); // Aumentei um pouco a altura
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        itensCarrinho = new ArrayList<>();
        carrinhoListModel = new DefaultListModel<>();
        carrinhoJList = new JList<>(carrinhoListModel);
        carrinhoJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carrinhoJList.addListSelectionListener(this);
        JScrollPane carrinhoScrollPane = new JScrollPane(carrinhoJList);
        add(carrinhoScrollPane, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removerItemButton = new JButton("Remover Item");
        removerItemButton.addActionListener(this);
        removerItemButton.setEnabled(false); // Desabilitado inicialmente
        botoesPanel.add(removerItemButton);

        alterarQuantidadeButton = new JButton("Alterar Qtd."); // Novo botão
        alterarQuantidadeButton.addActionListener(this);
        alterarQuantidadeButton.setEnabled(false); // Desabilitado inicialmente
        botoesPanel.add(alterarQuantidadeButton);

        add(botoesPanel, BorderLayout.SOUTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        totalLabel = new JLabel("Total: R$ 0.00");
        infoPanel.add(totalLabel);

        infoPanel.add(new JLabel("Endereço de Entrega:"));
        enderecoTextArea = new JTextArea(3, 30);
        JScrollPane enderecoScrollPane = new JScrollPane(enderecoTextArea);
        infoPanel.add(enderecoScrollPane);

        confirmarCompraButton = new JButton("Confirmar Compra");
        confirmarCompraButton.addActionListener(this);
        infoPanel.add(confirmarCompraButton);

        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.add(infoPanel, BorderLayout.NORTH);
        add(checkoutPanel, BorderLayout.EAST);

        atualizarTotal();
        setVisible(false);
    }

    public void adicionarItem(Produto produto, int quantidade, String tamanho) {
        ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade, tamanho);
        itensCarrinho.add(novoItem);
        carrinhoListModel.addElement(novoItem);
        atualizarTotal();
    }

    public void removerItem(int index) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.remove(index);
            carrinhoListModel.remove(index);
            atualizarTotal();
            removerItemButton.setEnabled(false);
            alterarQuantidadeButton.setEnabled(false);
        }
    }

    private void mostrarDialogoAlterarQuantidade() {
        int selectedIndex = carrinhoJList.getSelectedIndex();
        if (selectedIndex != -1) {
            ItemCarrinho item = itensCarrinho.get(selectedIndex);
            String novaQtdStr = JOptionPane.showInputDialog(this, "Nova quantidade para " + item.getProduto().getDescricao() + ":", item.getQuantidade());
            if (novaQtdStr != null) {
                try {
                    int novaQuantidade = Integer.parseInt(novaQtdStr);
                    if (novaQuantidade > 0) {
                        atualizarQuantidade(selectedIndex, novaQuantidade);
                    } else {
                        JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Por favor, insira um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void atualizarQuantidade(int index, int novaQuantidade) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.get(index).setQuantidade(novaQuantidade);
            carrinhoListModel.setElementAt(itensCarrinho.get(index), index);
            atualizarTotal();
        }
    }

    private void atualizarTotal() {
        double total = 0;
        for (ItemCarrinho item : itensCarrinho) {
            total += item.getSubtotal();
        }
        totalLabel.setText("Total: R$ " + String.format("%.2f", total));
    }

    public void mostrar() {
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == removerItemButton) {
            int selectedIndex = carrinhoJList.getSelectedIndex();
            if (selectedIndex != -1) {
                removerItem(selectedIndex);
            }
        } else if (e.getSource() == alterarQuantidadeButton) {
            mostrarDialogoAlterarQuantidade();
        } else if (e.getSource() == confirmarCompraButton) {
            String endereco = enderecoTextArea.getText();
            if (endereco.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, adicione o endereço de entrega.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (itensCarrinho.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seu carrinho está vazio. Adicione produtos para comprar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Compra confirmada para o endereço:\n" + endereco + "\nTotal: R$ " + String.format("%.2f", getTotal()), "Compra Confirmada", JOptionPane.INFORMATION_MESSAGE);
            // Aqui você implementaria a lógica para processar a compra
            itensCarrinho.clear();
            carrinhoListModel.clear();
            atualizarTotal();
            enderecoTextArea.setText("");
        }
    }

    public double getTotal() {
        double total = 0;
        for (ItemCarrinho item : itensCarrinho) {
            total += item.getSubtotal();
        }
        return total;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            boolean itemSelecionado = carrinhoJList.getSelectedIndex() != -1;
            removerItemButton.setEnabled(itemSelecionado);
            alterarQuantidadeButton.setEnabled(itemSelecionado);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaCarrinho tela = new TelaCarrinho();
            // Para teste, vamos adicionar alguns itens simulados
            Produto p1 = new Produto(1, 25.00, 10, 5, 2, "Camiseta Azul", "caminho/azul.jpg");
            Produto p2 = new Produto(2, 50.00, 2, 8, 3, "Calça Jeans", "caminho/jeans.jpg");
            tela.adicionarItem(p1, 2, "M");
            tela.adicionarItem(p2, 1, "G");
            tela.mostrar();
        });
    }
}