package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * A classe {@code TelaSelecaoTamanho} permite que o usuário selecione o tamanho e a quantidade
 * de um produto antes de adicioná-lo ao carrinho.
 * Ela consulta o banco de dados para obter os tamanhos disponíveis para um determinado produto.
 */
public class TelaSelecaoTamanho extends JFrame {

    private int produtoId;
    private String nomeProduto;
    private double valorProduto;
    private JComboBox<String> tamanhoComboBox;
    private JSpinner quantidadeSpinner;
    private JButton adicionarAoCarrinhoButton;
    private Map<String, String> tamanhoParaColuna = new HashMap<>();

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Construtor da classe {@code TelaSelecaoTamanho}.
     * Inicializa a interface gráfica para selecionar o tamanho e a quantidade do produto.
     *
     * @param produtoId    O ID do produto a ser selecionado.
     * @param nomeProduto  O nome do produto.
     * @param valorProduto O valor unitário do produto.
     */
    public TelaSelecaoTamanho(int produtoId, String nomeProduto, double valorProduto) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.valorProduto = valorProduto;

        // Mapeia os tamanhos para os nomes das colunas correspondentes no banco de dados
        tamanhoParaColuna.put("P", "quantidade_p");
        tamanhoParaColuna.put("M", "quantidade_m");
        tamanhoParaColuna.put("G", "quantidade_g");

        setTitle("Selecionar Tamanho e Quantidade");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Exibe o nome do produto
        add(new JLabel("Produto:"));
        add(new JLabel(nomeProduto));

        // Permite ao usuário selecionar o tamanho disponível
        add(new JLabel("Tamanho:"));
        String[] tamanhosDisponiveis = obterTamanhosDisponiveis(produtoId);
        tamanhoComboBox = new JComboBox<>(tamanhosDisponiveis);
        add(tamanhoComboBox);

        // Permite ao usuário selecionar a quantidade desejada
        add(new JLabel("Quantidade:"));
        SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1); // Valor inicial 1, mínimo 1, máximo 100, step 1
        quantidadeSpinner = new JSpinner(model);
        add(quantidadeSpinner);

        // Botão para adicionar o item selecionado ao carrinho
        adicionarAoCarrinhoButton = new JButton("Adicionar ao Carrinho");
        adicionarAoCarrinhoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tamanhoSelecionado = (String) tamanhoComboBox.getSelectedItem();
                int quantidadeSelecionada = (int) quantidadeSpinner.getValue();

                // Obtém a instância da sessão do usuário para verificar se o usuário está logado
                SessaoUsuario sessao = SessaoUsuario.getInstance();
                if (sessao.isUsuarioLogado()) {
                    // Verifica se há estoque disponível para a seleção
                    if (verificarEstoque(produtoId, tamanhoSelecionado, quantidadeSelecionada)) {
                        // Adiciona o item ao carrinho
                        Carrinho.getInstance().adicionarItem(produtoId, nomeProduto, valorProduto, tamanhoSelecionado, quantidadeSelecionada);
                        // Exibe uma mensagem de sucesso
                        JOptionPane.showMessageDialog(TelaSelecaoTamanho.this,
                                "Adicionado ao carrinho: " + quantidadeSelecionada + " x " + nomeProduto + " (" + tamanhoSelecionado + ")",
                                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        // Fecha a tela de seleção
                        TelaSelecaoTamanho.this.dispose();
                    } else {
                        // Exibe uma mensagem de erro se o estoque for insuficiente
                        JOptionPane.showMessageDialog(TelaSelecaoTamanho.this,
                                "Estoque insuficiente para " + nomeProduto + " (" + tamanhoSelecionado + ") na quantidade desejada.",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Informa ao usuário que ele precisa estar logado para adicionar itens ao carrinho
                    JOptionPane.showMessageDialog(TelaSelecaoTamanho.this,
                            "Você precisa estar logado para adicionar itens ao carrinho.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    // Opcional: Redirecionar para a tela de login
                    TelaLogin telaLogin = new TelaLogin();
                    telaLogin.setVisible(true);
                }
            }
        });
        add(new JLabel("")); // Espaço em branco para alinhar o botão
        add(adicionarAoCarrinhoButton);

        setVisible(true);
    }

    /**
     * Consulta o banco de dados para obter os tamanhos disponíveis para um determinado produto.
     *
     * @param produtoId O ID do produto.
     * @return Um array de strings contendo os tamanhos disponíveis ("P", "M", "G").
     */
    private String[] obterTamanhosDisponiveis(int produtoId) {
        java.util.List<String> tamanhos = new java.util.ArrayList<>();
        String sql = "SELECT quantidade_p, quantidade_m, quantidade_g FROM produtos WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("quantidade_p") > 0) tamanhos.add("P");
                if (rs.getInt("quantidade_m") > 0) tamanhos.add("M");
                if (rs.getInt("quantidade_g") > 0) tamanhos.add("G");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao obter tamanhos disponíveis: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return tamanhos.toArray(new String[0]);
    }

    /**
     * Verifica se há estoque suficiente para o tamanho e a quantidade desejados de um produto.
     *
     * @param produtoId   O ID do produto.
     * @param tamanho     O tamanho selecionado ("P", "M", "G").
     * @param quantidade A quantidade desejada.
     * @return {@code true} se houver estoque suficiente, {@code false} caso contrário.
     */
    private boolean verificarEstoque(int produtoId, String tamanho, int quantidade) {
        String colunaEstoque = tamanhoParaColuna.get(tamanho);
        if (colunaEstoque == null) {
            return false; // Tamanho inválido
        }
        String sql = "SELECT " + colunaEstoque + " FROM produtos WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int estoqueDisponivel = rs.getInt(colunaEstoque);
                return estoqueDisponivel >= quantidade;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método principal para testes da {@code TelaSelecaoTamanho}.
     * Cria e exibe uma instância da tela para um produto específico.
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaSelecaoTamanho(1, "Camiseta", 29.90));
    }
}