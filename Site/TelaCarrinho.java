// TelaCarrinho.java
package Site;

import javax.swing.*; // Importa classes para criar interfaces gráficas Swing
import javax.swing.event.ListSelectionEvent; // Importa a classe para eventos de seleção em listas
import javax.swing.event.ListSelectionListener; // Importa a interface para lidar com eventos de seleção em listas
import java.awt.*; // Importa classes para layouts e componentes gráficos AWT
import java.awt.event.ActionEvent; // Importa a classe para eventos de ação (como cliques de botão)
import java.awt.event.ActionListener; // Importa a interface para lidar com eventos de ação
import java.sql.Connection; // Importa a interface para a conexão com o banco de dados
import java.sql.DriverManager; // Importa a classe para gerenciar drivers JDBC
import java.sql.PreparedStatement; // Importa a classe para instruções SQL pré-compiladas
import java.sql.ResultSet; // Importa a interface para o resultado de uma consulta SQL
import java.sql.SQLException; // Importa a classe para exceções relacionadas ao SQL
import java.util.ArrayList; // Importa a classe ArrayList para listas dinâmicas
import java.util.List; // Importa a interface List para coleções ordenadas
import javax.swing.DefaultListModel; // Importa a classe para o modelo padrão de listas Swing
import javax.swing.border.TitledBorder; // Importa a classe para criar bordas com títulos

// A classe TelaCarrinho herda de JFrame (janela principal) e implementa ActionListener (para eventos de botões) e ListSelectionListener (para seleção de itens na lista)
public class TelaCarrinho extends JFrame implements ActionListener, ListSelectionListener {

    private DefaultListModel<ItemCarrinho> carrinhoListModel; // Modelo da lista para gerenciar os itens no carrinho
    private JList<ItemCarrinho> carrinhoJList; // Lista para exibir os itens do carrinho
    private JButton removerItemButton; // Botão para remover o item selecionado do carrinho
    private JButton alterarQuantidadeButton; // Botão para alterar a quantidade do item selecionado
    private JLabel totalLabel; // Label para exibir o valor total do carrinho
    private JTextArea enderecoTextArea; // Área de texto para o usuário inserir o endereço de entrega
    private JButton confirmarCompraButton; // Botão para finalizar a compra
    private List<ItemCarrinho> itensCarrinho; // Lista para armazenar os objetos ItemCarrinho
    private JRadioButton pixRadioButton; // RadioButton para o método de pagamento Pix
    private JRadioButton cartaoRadioButton; // RadioButton para o método de pagamento Cartão de Crédito
    private ButtonGroup pagamentoGroup; // Grupo para garantir que apenas um método de pagamento seja selecionado
    private String metodoPagamentoSelecionado = null; // Variável para armazenar o método de pagamento selecionado

    // Configurações do banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Construtor da TelaCarrinho
    public TelaCarrinho() {
        setTitle("Carrinho de Compras"); // Define o título da janela
        setSize(700, 500); // Define o tamanho inicial da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define o comportamento ao fechar a janela (apenas fecha esta tela)
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setLayout(new BorderLayout(10, 10)); // Define o layout principal como BorderLayout com espaçamento

        itensCarrinho = new ArrayList<>(); // Inicializa a lista de itens do carrinho
        carrinhoListModel = new DefaultListModel<>(); // Inicializa o modelo da lista
        carrinhoJList = new JList<>(carrinhoListModel); // Cria a lista com o modelo
        carrinhoJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite apenas uma seleção por vez
        carrinhoJList.addListSelectionListener(this); // Adiciona um listener para detectar a seleção de itens na lista
        JScrollPane carrinhoScrollPane = new JScrollPane(carrinhoJList); // Adiciona barra de rolagem à lista do carrinho
        add(carrinhoScrollPane, BorderLayout.CENTER); // Adiciona a lista no centro da janela

        // Painel para os botões de manipulação do carrinho (remover, alterar quantidade)
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removerItemButton = new JButton("Remover Item");
        removerItemButton.addActionListener(this); // Adiciona listener para o evento de clique
        removerItemButton.setEnabled(false); // Inicialmente desabilitado, habilitado ao selecionar um item
        botoesPanel.add(removerItemButton);

        alterarQuantidadeButton = new JButton("Alterar Qtd.");
        alterarQuantidadeButton.addActionListener(this); // Adiciona listener para o evento de clique
        alterarQuantidadeButton.setEnabled(false); // Inicialmente desabilitado, habilitado ao selecionar um item
        botoesPanel.add(alterarQuantidadeButton);

        add(botoesPanel, BorderLayout.SOUTH); // Adiciona o painel de botões na parte sul da janela

        // Painel para a seção de checkout (total, endereço, pagamento, confirmar)
        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS)); // Layout vertical
        checkoutPanel.setBorder(new TitledBorder("Checkout")); // Adiciona uma borda com título

        totalLabel = new JLabel("Total: R$ 0.00"); // Inicializa o label do total
        checkoutPanel.add(totalLabel);

        checkoutPanel.add(new JLabel("Endereço de Entrega:"));
        enderecoTextArea = new JTextArea(3, 30); // Área de texto para o endereço
        JScrollPane enderecoScrollPane = new JScrollPane(enderecoTextArea); // Adiciona barra de rolagem
        checkoutPanel.add(enderecoScrollPane);

        // Painel para os métodos de pagamento
        JPanel pagamentoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagamentoPanel.setBorder(new TitledBorder("Pagamento"));
        pixRadioButton = new JRadioButton("Pix");
        cartaoRadioButton = new JRadioButton("Cartão de Crédito");
        pagamentoGroup = new ButtonGroup(); // Cria um grupo para os radio buttons
        pagamentoGroup.add(pixRadioButton); // Adiciona os radio buttons ao grupo
        pagamentoGroup.add(cartaoRadioButton);
        pixRadioButton.addActionListener(this); // Adiciona listener para detectar a seleção
        cartaoRadioButton.addActionListener(this); // Adiciona listener para detectar a seleção
        pagamentoPanel.add(pixRadioButton);
        pagamentoPanel.add(cartaoRadioButton);
        checkoutPanel.add(pagamentoPanel);

        confirmarCompraButton = new JButton("Confirmar Compra");
        confirmarCompraButton.addActionListener(this); // Adiciona listener para o evento de clique
        checkoutPanel.add(confirmarCompraButton);

        add(checkoutPanel, BorderLayout.EAST); // Adiciona o painel de checkout na parte leste da janela

        atualizarTotal(); // Calcula e exibe o total inicial
        setVisible(false); // A tela do carrinho começa invisível e é mostrada explicitamente
    }

    // Adiciona um item ao carrinho
    public void adicionarItem(Produto produto, int quantidade, String tamanho) {
        ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade, tamanho);
        itensCarrinho.add(novoItem);
        carrinhoListModel.addElement(novoItem);
        atualizarTotal(); // Recalcula o total após adicionar um item
    }

    // Remove um item do carrinho pelo índice
    public void removerItem(int index) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.remove(index);
            carrinhoListModel.remove(index);
            atualizarTotal(); // Recalcula o total após remover um item
            removerItemButton.setEnabled(false); // Desabilita os botões após a remoção ou se a lista estiver vazia
            alterarQuantidadeButton.setEnabled(false);
        }
    }

    // Exibe um diálogo para o usuário inserir a nova quantidade do item selecionado
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

    // Atualiza a quantidade de um item no carrinho
    public void atualizarQuantidade(int index, int novaQuantidade) {
        if (index >= 0 && index < itensCarrinho.size()) {
            itensCarrinho.get(index).setQuantidade(novaQuantidade);
            carrinhoListModel.setElementAt(itensCarrinho.get(index), index);
            atualizarTotal(); // Recalcula o total após alterar a quantidade
        }
    }

    // Calcula e atualiza o total do carrinho
    private void atualizarTotal() {
        double total = 0;
        for (ItemCarrinho item : itensCarrinho) {
            total += item.getSubtotal();
        }
        totalLabel.setText("Total: R$ " + String.format("%.2f", total));
    }

    // Retorna o valor total do carrinho
    public double getTotal() {
        double total = 0;
        for (ItemCarrinho item : itensCarrinho) {
            total += item.getSubtotal();
        }
        return total;
    }

    // Verifica se há estoque disponível para todos os itens no carrinho
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
                            estoque = rs.getInt("quantidade_p"); // Consideramos a quantidade P para itens únicos
                        }

                        if (quantidadeComprada > estoque) {
                            JOptionPane.showMessageDialog(this,
                                    "Quantidade indisponível para o item: " + produto.getDescricao() + " (Tam: " + tamanho + ").\n" +
                                            "Estoque disponível: " + estoque + ", Quantidade desejada: " + quantidadeComprada + ".",
                                    "Erro de Estoque", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar o estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    // Atualiza o estoque no banco de dados após a compra
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
                    updateSql = "UPDATE produtos SET quantidade_p = quantidade_p - ? WHERE id = ?";
                }

                if (!updateSql.isEmpty()) {
                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setInt(1, quantidadeComprada);
                        pstmt.setInt(2, produtoId);
                        pstmt.executeUpdate();
                    }
                }

                // Verifica se o produto ficou sem estoque em todos os tamanhos e o remove do banco
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
            JOptionPane.showMessageDialog(this, "Estoque atualizado no banco de dados.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o estoque no banco de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Exibe uma confirmação da compra para o usuário
    private void mostrarConfirmacaoCompra() {
        if (enderecoTextArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, adicione o endereço de entrega.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (metodoPagamentoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um método de pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (itensCarrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seu carrinho está vazio. Adicione produtos para comprar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Nova verificação de estoque antes de prosseguir com a compra
        if (!verificarEstoque()) {
            return; // Impede a finalização se houver problema de estoque
        }

        StringBuilder confirmacao = new StringBuilder("Confirmação da Compra:\n\nItens:\n");
        for (ItemCarrinho item : itensCarrinho) {
            confirmacao.append("- ").append(item).append("\n");
        }
        confirmacao.append("\nTotal: R$ ").append(String.format("%.2f", getTotal()));
        confirmacao.append("\nEndereço de Entrega: ").append(enderecoTextArea.getText());
        confirmacao.append("\nPagamento: ").append(metodoPagamentoSelecionado);

        int escolha = JOptionPane.showConfirmDialog(this, confirmacao.toString(), "Confirmar Compra", JOptionPane.OK_CANCEL_OPTION);
        if (escolha == JOptionPane.OK_OPTION) {
            atualizarEstoqueEBanco(); // Atualiza o estoque no banco
            JOptionPane.showMessageDialog(this, "Compra finalizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            itensCarrinho.clear(); // Limpa o carrinho
            carrinhoListModel.clear(); // Limpa a lista de exibição
            atualizarTotal(); // Reseta o total
            enderecoTextArea.setText(""); // Limpa o endereço
            pagamentoGroup.clearSelection(); // Desseleciona o método de pagamento
            metodoPagamentoSelecionado = null;
            // Atualiza a tela de catálogo se ela estiver visível
            if (getParent() instanceof TelaCatalogo) {
                ((TelaCatalogo) getParent()).carregarProdutos();
            }
        }
    }

    // Torna a tela do carrinho visível
    public void mostrar() {
        setVisible(true);
    }

    // Lida com os eventos de ação (cliques de botão, seleções de rádio button)
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
            mostrarConfirmacaoCompra();
        } else if (e.getSource() == pixRadioButton) {
            metodoPagamentoSelecionado = "Pix";
        } else if (e.getSource() == cartaoRadioButton) {
            metodoPagamentoSelecionado = "Cartão de Crédito";
        }
    }

    // Lida com os eventos de mudança de seleção na lista do carrinho
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            boolean itemSelecionado = carrinhoJList.getSelectedIndex() != -1;
            removerItemButton.setEnabled(itemSelecionado);
            alterarQuantidadeButton.setEnabled(itemSelecionado);
        }
    }

    // Método main para executar a TelaCarrinho individualmente (para testes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaCarrinho tela = new TelaCarrinho();
            Produto p1 = new Produto(1, 25.00, 10, 5, 2, "Camiseta Azul", "caminho/azul.jpg");
            Produto p2 = new Produto(2, 50.00, 2, 8, 3, "Calça Jeans", "caminho/jeans.jpg");
            tela.adicionarItem(p1, 1, "M");
            tela.adicionarItem(p2, 1, "G");
            tela.mostrar();
        });
    }
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
        return produto.getDescricao() + " (Tam: " + tamanho + ", Qtd: " + quantidade + ") - R$" + String.format("%.2f", getSubtotal());
    }
}