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

public class TelaSelecaoTamanho extends JFrame {

    private int produtoId;
    private String nomeProduto;
    private double valorProduto;
    private JComboBox<String> tamanhoComboBox;
    private JSpinner quantidadeSpinner;
    private JButton adicionarAoCarrinhoButton;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaSelecaoTamanho(int produtoId, String nomeProduto, double valorProduto) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.valorProduto = valorProduto;

        setTitle("Selecionar Tamanho e Quantidade");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(new JLabel("Produto:"));
        add(new JLabel(nomeProduto));

        add(new JLabel("Tamanho:"));
        String[] tamanhosDisponiveis = obterTamanhosDisponiveis(produtoId);
        tamanhoComboBox = new JComboBox<>(tamanhosDisponiveis);
        add(tamanhoComboBox);

        add(new JLabel("Quantidade:"));
        SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1); // Valor inicial 1, mínimo 1, máximo 100, step 1
        quantidadeSpinner = new JSpinner(model);
        add(quantidadeSpinner);

        adicionarAoCarrinhoButton = new JButton("Adicionar ao Carrinho");
        adicionarAoCarrinhoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tamanhoSelecionado = (String) tamanhoComboBox.getSelectedItem();
                int quantidadeSelecionada = (int) quantidadeSpinner.getValue();

                // Aqui você precisará verificar se há estoque suficiente no banco de dados
                if (verificarEstoque(produtoId, tamanhoSelecionado, quantidadeSelecionada)) {
                    // Se houver estoque, você adicionaria o item ao carrinho (ainda não implementado)
                    JOptionPane.showMessageDialog(TelaSelecaoTamanho.this,
                            "Adicionado ao carrinho: " + quantidadeSelecionada + " x " + nomeProduto + " (" + tamanhoSelecionado + ")",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    TelaSelecaoTamanho.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(TelaSelecaoTamanho.this,
                            "Estoque insuficiente para " + nomeProduto + " (" + tamanhoSelecionado + ") na quantidade desejada.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(new JLabel("")); // Espaço em branco no grid
        add(adicionarAoCarrinhoButton);

        setVisible(true);
    }

    private String[] obterTamanhosDisponiveis(int produtoId) {
        java.util.List<String> tamanhos = new java.util.ArrayList<>();
        String sql = "SELECT DISTINCT tamanho FROM estoque WHERE produto_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tamanhos.add(rs.getString("tamanho"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao obter tamanhos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return tamanhos.toArray(new String[0]);
    }

    private boolean verificarEstoque(int produtoId, String tamanho, int quantidade) {
        String sql = "SELECT quantidade FROM estoque WHERE produto_id = ? AND tamanho = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            pstmt.setString(2, tamanho);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int estoqueDisponivel = rs.getInt("quantidade");
                return estoqueDisponivel >= quantidade;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false; // Se não encontrar o tamanho, considera sem estoque
    }

    public static void main(String[] args) {
        // Teste
        new TelaSelecaoTamanho(1, "Camiseta", 29.90);
    }
}