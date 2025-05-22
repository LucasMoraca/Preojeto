// TelaCarrinho.java
package Site;

import javax.swing.*;
<<<<<<< HEAD
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
=======
>>>>>>> parent of f0e28be (Declaração)
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
<<<<<<< HEAD
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

public class TelaCarrinho extends JFrame implements ActionListener, ListSelectionListener {

    private JPanel cartItemsPanel;
    private JLabel totalLabel;
    private DecimalFormat currencyFormat = new DecimalFormat("R$ #,##0.00");
    private double currentTotal = 0.0;
    private JTextArea enderecoTextArea;
    private JButton confirmarCompraButton;
=======
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.border.TitledBorder;

public class TelaCarrinho extends JFrame implements ActionListener, ListSelectionListener {

    private DefaultListModel<ItemCarrinho> carrinhoListModel;
    private JList<ItemCarrinho> carrinhoJList;
    private JButton removerItemButton;
    private JButton alterarQuantidadeButton;
    private JLabel totalLabel;
    private JTextArea enderecoTextArea;
    private JButton confirmarCompraButton;
    private List<ItemCarrinho> itensCarrinho;
>>>>>>> parent of f0e28be (Declaração)
    private JRadioButton pixRadioButton;
    private JRadioButton cartaoRadioButton;
    private ButtonGroup pagamentoGroup;
    private String metodoPagamentoSelecionado = null;
<<<<<<< HEAD
    private List<ItemCarrinho> itensCarrinho = new ArrayList<>();
=======
>>>>>>> parent of f0e28be (Declaração)

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaCarrinho() {
        setTitle("Carrinho de Compras");
<<<<<<< HEAD
        setSize(950, 700); // Aumentei um pouco a largura
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(createTopBar(), BorderLayout.NORTH);
        mainPanel.add(createCenterContainer(), BorderLayout.CENTER);
        mainPanel.add(createFooter(), BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(false);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel title = new JLabel("MEU CARRINHO");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(title);
        return topBar;
    }

    private JPanel createCenterContainer() {
        JPanel center = new JPanel(new BorderLayout());
        center.add(createOrderDetailsPanel(), BorderLayout.EAST); // Moveu para a direita
        center.add(createCartItemsScrollPane(), BorderLayout.CENTER); // Painel de itens no centro
        return center;
    }

    private JScrollPane createCartItemsScrollPane() {
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(new Color(240, 240, 240));
        cartItemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adicionei margem lateral

        JScrollPane scroll = new JScrollPane(cartItemsPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private JPanel createOrderDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Entrega e Pagamento"));
        panel.setPreferredSize(new Dimension(300, 200)); // Largura fixa para a lateral
        panel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Endereço de Entrega:"));
        enderecoTextArea = new JTextArea(3, 25);
        JScrollPane enderecoScrollPane = new JScrollPane(enderecoTextArea);
        panel.add(enderecoScrollPane);
        panel.add(Box.createVerticalStrut(10)); // Espaçamento

        JPanel pagamentoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagamentoPanel.setBorder(BorderFactory.createTitledBorder("Pagamento"));
=======
        setSize(700, 500);
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
        removerItemButton.setEnabled(false);
        botoesPanel.add(removerItemButton);

        alterarQuantidadeButton = new JButton("Alterar Qtd.");
        alterarQuantidadeButton.addActionListener(this);
        alterarQuantidadeButton.setEnabled(false);
        botoesPanel.add(alterarQuantidadeButton);

        add(botoesPanel, BorderLayout.SOUTH);

        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setBorder(new TitledBorder("Checkout"));

        totalLabel = new JLabel("Total: R$ 0.00");
        checkoutPanel.add(totalLabel);

        checkoutPanel.add(new JLabel("Endereço de Entrega:"));
        enderecoTextArea = new JTextArea(3, 30);
        JScrollPane enderecoScrollPane = new JScrollPane(enderecoTextArea);
        checkoutPanel.add(enderecoScrollPane);

        JPanel pagamentoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagamentoPanel.setBorder(new TitledBorder("Pagamento"));
>>>>>>> parent of f0e28be (Declaração)
        pixRadioButton = new JRadioButton("Pix");
        cartaoRadioButton = new JRadioButton("Cartão de Crédito");
        pagamentoGroup = new ButtonGroup();
        pagamentoGroup.add(pixRadioButton);
        pagamentoGroup.add(cartaoRadioButton);
        pixRadioButton.addActionListener(this);
        cartaoRadioButton.addActionListener(this);
        pagamentoPanel.add(pixRadioButton);
        pagamentoPanel.add(cartaoRadioButton);
<<<<<<< HEAD
        panel.add(pagamentoPanel);
        panel.add(Box.createVerticalGlue()); // Empurra para cima

        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));

        confirmarCompraButton = new JButton("Finalizar Pedido");
        confirmarCompraButton.addActionListener(this);
        footer.add(confirmarCompraButton, BorderLayout.EAST);

        totalLabel = new JLabel("Total do Carrinho: " + currencyFormat.format(currentTotal), SwingConstants.LEFT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        footer.add(totalLabel, BorderLayout.WEST);

        return footer;
=======
        checkoutPanel.add(pagamentoPanel);

        confirmarCompraButton = new JButton("Confirmar Compra");
        confirmarCompraButton.addActionListener(this);
        checkoutPanel.add(confirmarCompraButton);

        add(checkoutPanel, BorderLayout.EAST);

        atualizarTotal();
        setVisible(false);
>>>>>>> parent of f0e28be (Declaração)
    }

    public void adicionarItem(Produto produto, int quantidade, String tamanho) {
        ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade, tamanho);
        itensCarrinho.add(novoItem);
<<<<<<< HEAD
        addCartItemToPanel(novoItem);
        atualizarTotal();
    }

    private void addCartItemToPanel(ItemCarrinho item) {
        JPanel itemPanel = new JPanel(new GridBagLayout());
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5))); // Reduzi a margem interna

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2); // Reduzi as insets
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel descricaoLabel = new JLabel(item.getProduto().getDescricao());
        descricaoLabel.setFont(descricaoLabel.getFont().deriveFont(Font.PLAIN, 12)); // Diminui a fonte
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        itemPanel.add(descricaoLabel, gbc);

        JLabel tamanhoLabel = new JLabel("Tam: " + item.getTamanho());
        tamanhoLabel.setFont(tamanhoLabel.getFont().deriveFont(Font.PLAIN, 10)); // Diminui a fonte
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        itemPanel.add(tamanhoLabel, gbc);

        // --- Controles de Quantidade ---
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quantityPanel.setBackground(Color.WHITE);
        JButton minusButton = new JButton("-");
        JLabel quantityLabel = new JLabel(String.valueOf(item.getQuantidade()));
        JButton plusButton = new JButton("+");
        minusButton.setFont(minusButton.getFont().deriveFont(Font.PLAIN, 10));
        quantityLabel.setFont(quantityLabel.getFont().deriveFont(Font.PLAIN, 10));
        plusButton.setFont(plusButton.getFont().deriveFont(Font.PLAIN, 10));
        quantityPanel.add(new JLabel("Qtd: "));
        quantityPanel.add(minusButton);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(plusButton);

        final ItemCarrinho finalItem = item;
        final JLabel finalQuantityLabel = quantityLabel;
        final JPanel finalItemPanel = itemPanel;

        minusButton.addActionListener(e -> {
            int currentQuantity = Integer.parseInt(finalQuantityLabel.getText());
            if (currentQuantity > 1) {
                finalItem.setQuantidade(currentQuantity - 1);
                finalQuantityLabel.setText(String.valueOf(finalItem.getQuantidade()));
                updateItemTotalPrice(finalItemPanel, finalItem);
                atualizarTotal();
            } else if (currentQuantity == 1) {
                int resposta = JOptionPane.showConfirmDialog(this, "Remover " + finalItem.getProduto().getDescricao() + "?", "Remover Item", JOptionPane.YES_NO_OPTION);
                if (resposta == JOptionPane.YES_OPTION) {
                    removerItem(itensCarrinho.indexOf(finalItem));
=======
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
>>>>>>> parent of f0e28be (Declaração)
                }
            }
        });

        plusButton.addActionListener(e -> {
            int currentQuantity = Integer.parseInt(finalQuantityLabel.getText());
            finalItem.setQuantidade(currentQuantity + 1);
            finalQuantityLabel.setText(String.valueOf(finalItem.getQuantidade()));
            updateItemTotalPrice(finalItemPanel, finalItem);
            atualizarTotal();
        });

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.CENTER;
        itemPanel.add(quantityPanel, gbc);
        // --- Fim Controles de Quantidade ---

        // --- Preço do Item ---
        JPanel prices = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        prices.setBackground(Color.WHITE);
        JLabel itemPriceLabel = new JLabel(currencyFormat.format(item.getProduto().getValor()));
        JLabel itemTotalPriceLabel = new JLabel("Total: " + currencyFormat.format(item.getSubtotal()));
        itemPriceLabel.setFont(itemPriceLabel.getFont().deriveFont(Font.PLAIN, 10));
        itemTotalPriceLabel.setFont(itemTotalPriceLabel.getFont().deriveFont(Font.PLAIN, 10));
        JLabel precoUnitLabel = new JLabel("Preço Unitário: ");
        precoUnitLabel.setFont(precoUnitLabel.getFont().deriveFont(Font.PLAIN, 10));
        JLabel totalItemLabel = new JLabel(" | Total: ");
        totalItemLabel.setFont(totalItemLabel.getFont().deriveFont(Font.PLAIN, 10));

        prices.add(precoUnitLabel);
        prices.add(itemPriceLabel);
        prices.add(totalItemLabel);
        prices.add(itemTotalPriceLabel);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.EAST;
        itemPanel.add(prices, gbc);
        // --- Fim Preço do Item ---

        // --- Botão Remover ---
        JButton removerButton = new JButton("Remover");
        removerButton.setFont(removerButton.getFont().deriveFont(Font.PLAIN, 10));
        removerButton.addActionListener(e -> removerItem(itensCarrinho.indexOf(finalItem)));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.EAST;
        itemPanel.add(removerButton, gbc);
        gbc.gridy = 1;
        itemPanel.add(new JPanel(), gbc); // Espaçador

        cartItemsPanel.add(itemPanel);
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
        atualizarTotal();
    }

    private void updateItemTotalPrice(JPanel itemPanel, ItemCarrinho item) {
        Component[] components = itemPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getLayout() instanceof FlowLayout && ((FlowLayout) ((JPanel) comp).getLayout()).getAlignment() == FlowLayout.RIGHT) {
                JPanel pricePanel = (JPanel) comp;
                Component[] priceComponents = pricePanel.getComponents();
                JLabel totalPriceLabelToUpdate = null;
                for (Component priceComp : priceComponents) {
                    if (priceComp instanceof JLabel && ((JLabel) priceComp).getText().startsWith("Total:")) {
                        // Encontra o label correto (pode haver outros JLabels no painel de preços)
                        int index = java.util.Arrays.asList(priceComponents).indexOf(priceComp);
                        if (index > 0 && priceComponents[index - 1] instanceof JLabel && ((JLabel) priceComponents[index - 1]).getText().equals(" | Total: ")) {
                            totalPriceLabelToUpdate = (JLabel) priceComp;
                            break;
                        }
                    }
                }
                if (totalPriceLabelToUpdate != null) {
                    totalPriceLabelToUpdate.setText(currencyFormat.format(item.getSubtotal()));
                }
                break;
            }
        }
    }

<<<<<<< HEAD
    public void removerItem(int index) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.remove(index);
            atualizarExibicaoCarrinho();
=======
    public void atualizarQuantidade(int index, int novaQuantidade) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.get(index).setQuantidade(novaQuantidade);
            carrinhoListModel.setElementAt(itensCarrinho.get(index), index);
            atualizarTotal();
>>>>>>> parent of f0e28be (Declaração)
        }
    }

    private void atualizarTotal() {
        currentTotal = 0;
        for (ItemCarrinho item : itensCarrinho) {
            currentTotal += item.getSubtotal();
        }
        totalLabel.setText("Total do Carrinho: " + currencyFormat.format(currentTotal));
    }

    public double getTotal() {
        return currentTotal;
    }

    private boolean verificarEstoque() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (ItemCarrinho item : itensCarrinho) {
                Produto produto = item.getProduto();
                int quantidadeComprada = item.getQuantidade();
                String tamanho = item.getTamanho();
                int produtoId = produto.getId();

                String sql = "SELECT quantidade_p, quantidade_m, quantidade_g FROM produtos WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, produtoId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int estoque = 0;
                        if (tamanho.equals("P")) {
                            estoque = rs.getInt("quantidade_p");
                        } else if (tamanho.equals("M")) {
                            estoque = rs.getInt("quantidade_m");
                        } else if (tamanho.equals("G")) {
                            estoque = rs.getInt("quantidade_g");
                        } else if (tamanho.equals("Único")) {
                            estoque = rs.getInt("quantidade_p");
                        }

                        if (quantidadeComprada > estoque) {
                            JOptionPane.showMessageDialog(this,
                                    "Quantidade indisponível para: " + produto.getDescricao() + " (Tam: " + tamanho + ").\n" +
                                            "Estoque: " + estoque + ", Desejado: " + quantidadeComprada + ".",
                                    "Erro de Estoque", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void atualizarEstoqueEBanco() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (ItemCarrinho item : itensCarrinho) {
                Produto produto = item.getProduto();
                int quantidadeComprada = item.getQuantidade();
                String tamanho = item.getTamanho();
                int produtoId = produto.getId();

                String updateSql = "";
                if (tamanho.equals("P")) {
                    updateSql = "UPDATE produtos SET quantidade_p = quantidade_p - ? WHERE id = ?";
                } else if (tamanho.equals("M")) {
                    updateSql = "UPDATE produtos SET quantidade_m = quantidade_m - ? WHERE id = ?";
                } else if (tamanho.equals("G")) {
                    updateSql = "UPDATE produtos SET quantidade_g = quantidade_g - ? WHERE id = ?";
                } else if (tamanho.equals("Único")) {
                    // TelaCarrinho.java (continuação)
                    updateSql = "UPDATE produtos SET quantidade_p = quantidade_p - ? WHERE id = ?";
                }

                if (!updateSql.isEmpty()) {
                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setInt(1, quantidadeComprada);
                        pstmt.setInt(2, produtoId);
                        pstmt.executeUpdate();
                    }
                }

                String checkQuantidadeSql = "SELECT quantidade_p, quantidade_m, quantidade_g FROM produtos WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(checkQuantidadeSql)) {
                    pstmt.setInt(1, produtoId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int qtdP = rs.getInt("quantidade_p");
                        int qtdM = rs.getInt("quantidade_m");
                        int qtdG = rs.getInt("quantidade_g");
                        if (qtdP <= 0 && qtdM <= 0 && qtdG <= 0) {
                            String deleteSql = "DELETE FROM produtos WHERE id = ?";
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                                deleteStmt.setInt(1, produtoId);
                                deleteStmt.executeUpdate();
                                System.out.println("Produto ID " + produtoId + " removido por falta de estoque.");
                            }
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Estoque atualizado.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void mostrarConfirmacaoCompra() {
        if (enderecoTextArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o endereço de entrega.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (metodoPagamentoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione o método de pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (itensCarrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

<<<<<<< HEAD
=======
        // Nova verificação de estoque antes de prosseguir
>>>>>>> parent of f0e28be (Declaração)
        if (!verificarEstoque()) {
            return;
        }

        StringBuilder confirmacao = new StringBuilder("Confirmação da Compra:\n\nItens:\n");
        for (ItemCarrinho item : itensCarrinho) {
            confirmacao.append("- ").append(item).append("\n");
        }
        confirmacao.append("\nTotal: ").append(currencyFormat.format(getTotal()));
        confirmacao.append("\nEndereço: ").append(enderecoTextArea.getText());
        confirmacao.append("\nPagamento: ").append(metodoPagamentoSelecionado);

        int escolha = JOptionPane.showConfirmDialog(this, confirmacao.toString(), "Confirmar Compra", JOptionPane.OK_CANCEL_OPTION);
        if (escolha == JOptionPane.OK_OPTION) {
            atualizarEstoqueEBanco();
            JOptionPane.showMessageDialog(this, "Compra finalizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            itensCarrinho.clear();
<<<<<<< HEAD
            atualizarExibicaoCarrinho();
=======
            carrinhoListModel.clear();
            atualizarTotal();
>>>>>>> parent of f0e28be (Declaração)
            enderecoTextArea.setText("");
            pagamentoGroup.clearSelection();
            metodoPagamentoSelecionado = null;
            if (getParent() instanceof TelaCatalogo) {
                ((TelaCatalogo) getParent()).carregarProdutos();
            }
        }
    }

    public void mostrar() {
        atualizarExibicaoCarrinho();
        setVisible(true);
    }

<<<<<<< HEAD
    private void atualizarExibicaoCarrinho() {
        cartItemsPanel.removeAll();
        for (ItemCarrinho item : itensCarrinho) {
            addCartItemToPanel(item);
        }
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
        atualizarTotal();
    }

=======
>>>>>>> parent of f0e28be (Declaração)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmarCompraButton) {
            mostrarConfirmacaoCompra();
        } else if (e.getSource() == pixRadioButton) {
            metodoPagamentoSelecionado = "Pix";
        } else if (e.getSource() == cartaoRadioButton) {
            metodoPagamentoSelecionado = "Cartão de Crédito";
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Não estamos usando a JList diretamente para seleção neste layout.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaCarrinho tela = new TelaCarrinho();
            Produto p1 = new Produto(1, 25.00, 10, 5, 2, "Camiseta Azul", "caminho/azul.jpg");
            Produto p2 = new Produto(2, 50.00, 2, 8, 3, "Calça Jeans", "caminho/jeans.jpg");
            tela.adicionarItem(p1, 6, "M"); // Quantidade maior que o estoque (5)
            tela.adicionarItem(p2, 1, "G");
            tela.mostrar();
        });
    }
<<<<<<< HEAD
}

// Classe auxiliar para representar um item no carrinho
class ItemCarrinho {
    private Produto produto;
    private int quantidade;
    private String tamanho;

    public ItemCarrinho(Produto produto, int quantidade, String tamanho) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.tamanho = tamanho;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getTamanho() {
        return tamanho;
    }

    public double getSubtotal() {
        return produto.getValor() * quantidade;
    }

    @Override
    public String toString() {
        return produto.getDescricao() + " (Tam: " + tamanho + ", Qtd: " + quantidade + ") - " + String.format("R$ %.2f", getSubtotal());
    }
}

// Classe auxiliar Produto (apenas para exemplo)
class Produto {
    private int id;
    private double valor;
    private int quantidade_p;
    private int quantidade_m;
    private int quantidade_g;
    private String descricao;
    private String imagemPath;

    public Produto(int id, double valor, int quantidade_p, int quantidade_m, int quantidade_g, String descricao, String imagemPath) {
        this.id = id;
        this.valor = valor;
        this.quantidade_p = quantidade_p;
        this.quantidade_m = quantidade_m;
        this.quantidade_g = quantidade_g;
        this.descricao = descricao;
        this.imagemPath = imagemPath;
    }

    public int getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }
=======
>>>>>>> parent of f0e28be (Declaração)
}