package Site;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelaEditarProduto extends JFrame {

    private JTable produtosTable;
    private DefaultTableModel tableModel;
    private JButton selecionarImagem1Button;
    private JButton selecionarImagem2Button;
    private JButton selecionarImagem3Button;
    private JTextField quantidadePField;
    private JTextField quantidadeMField;
    private JTextField quantidadeGField;
    private JTextField valorField;
    private JTextArea descricaoArea;
    private JButton salvarAlteracoesButton;
    private JButton removerProdutoButton;

    private List<String> newImagePaths = new ArrayList<>();
    private int selectedProductId = -1;
    private String currentImagePath1 = null;
    private String currentImagePath2 = null;
    private String currentImagePath3 = null;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaEditarProduto() {
        setTitle("Editar Produto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tabela de Produtos
        tableModel = new DefaultTableModel(new Object[]{"ID", "Valor", "Qtd. P", "Qtd. M", "Qtd. G", "Descrição", "Imagem 1", "Imagem 2", "Imagem 3"}, 0);
        produtosTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(produtosTable);
        add(tableScrollPane, BorderLayout.NORTH);

        // Painel de Edição
        JPanel edicaoPanel = new JPanel(new GridBagLayout());
        edicaoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        edicaoPanel.add(new JLabel("Imagem 1:"), gbc);
        gbc.gridx++;
        selecionarImagem1Button = new JButton("Selecionar");
        edicaoPanel.add(selecionarImagem1Button, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        edicaoPanel.add(new JLabel("Imagem 2:"), gbc);
        gbc.gridx++;
        selecionarImagem2Button = new JButton("Selecionar");
        edicaoPanel.add(selecionarImagem2Button, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        edicaoPanel.add(new JLabel("Imagem 3:"), gbc);
        gbc.gridx++;
        selecionarImagem3Button = new JButton("Selecionar");
        edicaoPanel.add(selecionarImagem3Button, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        edicaoPanel.add(new JLabel("Quantidade P:"), gbc);
        gbc.gridx++;
        quantidadePField = new JTextField(10);
        edicaoPanel.add(quantidadePField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        edicaoPanel.add(new JLabel("Quantidade M:"), gbc);
        gbc.gridx++;
        quantidadeMField = new JTextField(10);
        edicaoPanel.add(quantidadeMField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        edicaoPanel.add(new JLabel("Quantidade G:"), gbc);
        gbc.gridx++;
        quantidadeGField = new JTextField(10);
        edicaoPanel.add(quantidadeGField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        edicaoPanel.add(new JLabel("Valor:"), gbc);
        gbc.gridx++;
        valorField = new JTextField(10);
        edicaoPanel.add(valorField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        gbc.gridwidth = 2;
        edicaoPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        descricaoArea = new JTextArea(5, 20);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        edicaoPanel.add(new JScrollPane(descricaoArea), gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarAlteracoesButton = new JButton("Salvar Alterações");
        removerProdutoButton = new JButton("Remover Produto");
        botoesPanel.add(salvarAlteracoesButton);
        botoesPanel.add(removerProdutoButton);

        gbc.gridwidth = 2;
        edicaoPanel.add(botoesPanel, gbc);

        add(new JScrollPane(edicaoPanel), BorderLayout.CENTER);

        carregarProdutos();

        selecionarImagem1Button.addActionListener(e -> selecionarNovaImagem(1));
        selecionarImagem2Button.addActionListener(e -> selecionarNovaImagem(2));
        selecionarImagem3Button.addActionListener(e -> selecionarNovaImagem(3));

        produtosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = produtosTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedProductId = (int) tableModel.getValueAt(selectedRow, 0);
                    valorField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    quantidadePField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    quantidadeMField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    quantidadeGField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    descricaoArea.setText(tableModel.getValueAt(selectedRow, 5).toString());
                    currentImagePath1 = (String) tableModel.getValueAt(selectedRow, 6);
                    currentImagePath2 = (String) tableModel.getValueAt(selectedRow, 7);
                    currentImagePath3 = (String) tableModel.getValueAt(selectedRow, 8);
                    newImagePaths.clear();
                }
            }
        });

        salvarAlteracoesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedProductId != -1) {
                    salvarAlteracoesProduto();
                } else {
                    JOptionPane.showMessageDialog(TelaEditarProduto.this, "Selecione um produto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        removerProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedProductId != -1) {
                    removerProdutoDoBanco();
                } else {
                    JOptionPane.showMessageDialog(TelaEditarProduto.this, "Selecione um produto para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private void carregarProdutos() {
        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, valor, quantidade_p, quantidade_m, quantidade_g, descricao, imagens1_path, imagens2_path, imagens3_path FROM produtos")) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getDouble("valor"),
                        rs.getInt("quantidade_p"),
                        rs.getInt("quantidade_m"),
                        rs.getInt("quantidade_g"),
                        rs.getString("descricao"),
                        rs.getString("imagens1_path"),
                        rs.getString("imagens2_path"),
                        rs.getString("imagens3_path")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void selecionarNovaImagem(int imagemIndex) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            while (newImagePaths.size() < imagemIndex) {
                newImagePaths.add(null);
            }
            newImagePaths.set(imagemIndex - 1, selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Nova imagem " + imagemIndex + " selecionada: " + selectedFile.getName(), "Imagem Selecionada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void salvarAlteracoesProduto() {
        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(this, "Nenhum produto selecionado para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String quantidadePStr = quantidadePField.getText();
        String quantidadeMStr = quantidadeMField.getText();
        String quantidadeGStr = quantidadeGField.getText();
        String valorStr = valorField.getText();
        String descricao = descricaoArea.getText();

        try {
            int quantidadeP = Integer.parseInt(quantidadePStr);
            int quantidadeM = Integer.parseInt(quantidadeMStr);
            int quantidadeG = Integer.parseInt(quantidadeGStr);
            double valor = Double.parseDouble(valorStr);

            // Diretório onde as imagens serão salvas
            File diretorioImagens = new File("imagens");
            if (!diretorioImagens.exists()) {
                diretorioImagens.mkdirs();
            }

            // Salvar as imagens se novas forem selecionadas
            String imagem1Path = salvarImagemSeSelecionada(0, currentImagePath1);
            String imagem2Path = salvarImagemSeSelecionada(1, currentImagePath2);
            String imagem3Path = salvarImagemSeSelecionada(2, currentImagePath3);

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "UPDATE produtos SET valor = ?, quantidade_p = ?, quantidade_m = ?, quantidade_g = ?, descricao = ?, imagens1_path = ?, imagens2_path = ?, imagens3_path = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setDouble(1, valor);
            pstmt.setInt(2, quantidadeP);
            pstmt.setInt(3, quantidadeM);
            pstmt.setInt(4, quantidadeG);
            pstmt.setString(5, descricao);

            pstmt.setString(6, imagem1Path);
            pstmt.setString(7, imagem2Path);
            pstmt.setString(8, imagem3Path);
            pstmt.setInt(9, selectedProductId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProdutos();
                newImagePaths.clear();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao atualizar o produto.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

            pstmt.close();
            conn.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos válidos para quantidade e valor.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao acessar o banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String salvarImagemSeSelecionada(int index, String currentPath) {
        if (newImagePaths.size() > index && newImagePaths.get(index) != null) {
            File origem = new File(newImagePaths.get(index));
            String nomeArquivo = System.currentTimeMillis() + "_" + origem.getName();
            File destino = new File("imagens", nomeArquivo);

            try {
                java.nio.file.Files.copy(origem.toPath(), destino.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return "imagens/" + nomeArquivo; // Caminho relativo salvo no banco
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar imagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return currentPath;
            }
        } else {
            return currentPath;
        }
    }

    private void removerProdutoDoBanco() {
        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(this, "Nenhum produto selecionado para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este produto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM produtos WHERE id = ?")) {

                pstmt.setInt(1, selectedProductId);
                int rowsDeleted = pstmt.executeUpdate();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Produto removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarProdutos();
                    limparCampos();
                    selectedProductId = -1;
                    newImagePaths.clear();
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao remover o produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao acessar o banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void limparCampos() {
        quantidadePField.setText("");
        quantidadeMField.setText("");
        quantidadeGField.setText("");
        valorField.setText("");
        descricaoArea.setText("");
        currentImagePath1 = null;
        currentImagePath2 = null;
        currentImagePath3 = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEditarProduto());
    }
}

