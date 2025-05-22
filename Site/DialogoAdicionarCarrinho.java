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

public class DialogoAdicionarCarrinho extends JDialog {

    private JSpinner quantidadeSpinner;
    private JComboBox<String> tamanhoComboBox;
    private JButton okButton;
    private JButton cancelButton;
    private String nomeProduto;
    private int produtoId;
    private Map<String, Integer> quantidadesPorTamanho;
    private TelaCarrinho telaCarrinho; // Referência à TelaCarrinho

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public DialogoAdicionarCarrinho(JFrame parent, int produtoId, String nomeProduto, TelaCarrinho telaCarrinho) {
        super(parent, "Escolher Quantidade e Tamanho", true);
        this.nomeProduto = nomeProduto;
        this.produtoId = produtoId;
        this.telaCarrinho = telaCarrinho; // Recebe a referência
        this.quantidadesPorTamanho = carregarQuantidadesPorTamanho(produtoId);

        setLayout(new GridLayout(3, 2, 10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Quantidade
        add(new JLabel("Quantidade:"));
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 1, 1); // Valor inicial 1, mínimo 1
        quantidadeSpinner = new JSpinner(model);
        add(quantidadeSpinner);

        // Tamanho
        add(new JLabel("Tamanho:"));
        DefaultComboBoxModel<String> tamanhoModel = new DefaultComboBoxModel<>();
        for (Map.Entry<String, Integer> entry : quantidadesPorTamanho.entrySet()) {
            if (entry.getValue() > 0) {
                tamanhoModel.addElement(entry.getKey());
            }
        }
        tamanhoComboBox = new JComboBox<>(tamanhoModel);
        add(tamanhoComboBox);

        // Atualizar a quantidade máxima quando o tamanho muda
        tamanhoComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tamanhoSelecionado = (String) tamanhoComboBox.getSelectedItem();
                setQuantidadeMaxima(tamanhoSelecionado);
            }
        });
        // Define a quantidade máxima para o primeiro tamanho selecionado
        if (tamanhoComboBox.getSelectedItem() != null) {
            setQuantidadeMaxima((String) tamanhoComboBox.getSelectedItem());
        }

        // Botões
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancelar");

        add(okButton);
        add(cancelButton);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantidadeSelecionada = (int) quantidadeSpinner.getValue();
                String tamanhoSelecionado = (String) tamanhoComboBox.getSelectedItem();

                // Buscar o valor unitário do produto do banco de dados
                double valorUnitario = buscarValorUnitario(produtoId);

                if (valorUnitario > 0) {
                    // Adicionar o item ao carrinho através da referência da TelaCarrinho
                    telaCarrinho.adicionarItem(produtoId, nomeProduto, tamanhoSelecionado, quantidadeSelecionada, valorUnitario);
                    JOptionPane.showMessageDialog(DialogoAdicionarCarrinho.this,
                            "Adicionado " + quantidadeSelecionada + " (tam. " + tamanhoSelecionado + ") de '" + nomeProduto + "' ao carrinho.",
                            "Carrinho", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(DialogoAdicionarCarrinho.this, "Erro ao obter o valor do produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(parent);
        setVisible(false);
    }

    private double buscarValorUnitario(int produtoId) {
        String sql = "SELECT valor FROM produtos WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("valor");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar valor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return 0.0;
    }

    private Map<String, Integer> carregarQuantidadesPorTamanho(int produtoId) {
        Map<String, Integer> quantidades = new HashMap<>();
        String sql = "SELECT quantidade_p, quantidade_m, quantidade_g FROM produtos WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                quantidades.put("P", rs.getInt("quantidade_p"));
                quantidades.put("M", rs.getInt("quantidade_m"));
                quantidades.put("G", rs.getInt("quantidade_g"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar quantidades: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return quantidades;
    }

    public void setQuantidadeMaxima(String tamanho) {
        Integer maxQuantidade = quantidadesPorTamanho.get(tamanho);
        if (maxQuantidade != null && maxQuantidade > 0) {
            SpinnerNumberModel model = (SpinnerNumberModel) quantidadeSpinner.getModel();
            model.setMaximum(maxQuantidade);
            quantidadeSpinner.setValue(Math.min((int) quantidadeSpinner.getValue(), maxQuantidade));
            model.setMinimum(1);
            if ((int) quantidadeSpinner.getValue() < 1) {
                quantidadeSpinner.setValue(1);
            }
        } else {
            SpinnerNumberModel model = (SpinnerNumberModel) quantidadeSpinner.getModel();
            model.setMaximum(0);
            quantidadeSpinner.setValue(0);
            model.setMinimum(0);
        }
    }

    // Removido o main de teste para evitar conflitos
}