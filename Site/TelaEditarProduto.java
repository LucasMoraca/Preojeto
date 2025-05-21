// TelaEditarProduto.java
package Site;

import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TelaEditarProduto extends JFrame implements ActionListener, ListSelectionListener {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JButton botaoSalvarEdicao;

    // Painel para a seção de edição
    private JPanel painelEdicao;

    // Componentes para edição
    private List<JButton> botoesImagem;
    private List<JLabel> labelsImagem;
    private JTextField campoQuantidadeP;
    private JTextField campoQuantidadeM;
    private JTextField campoQuantidadeG;
    private JTextField campoValor;
    private JTextArea campoDescricao;
    private JScrollPane scrollDescricao; // Para controlar a barra de rolagem da descrição

    private static final int MAX_IMAGENS = 3;
    private List<String> caminhosImagens; // Caminhos das imagens sendo editadas
    private int produtoIdSelecionado = -1; // Para rastrear o ID do produto sendo editado
    private List<String> imagensAtuaisBanco; // Caminhos das imagens atuais no banco

    // Configurações do banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaEditarProduto() {
        setTitle("Editar Produto");
        setSize(1250, 950); // Mantive o tamanho da janela maior
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Inicialização das listas
        caminhosImagens = new ArrayList<>();
        imagensAtuaisBanco = new ArrayList<>();
        for (int i = 0; i < MAX_IMAGENS; i++) {
            caminhosImagens.add(null);
            imagensAtuaisBanco.add(null);
        }
        botoesImagem = new ArrayList<>();
        labelsImagem = new ArrayList<>();

        // Tabela para listar os produtos
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Valor", "Qtd. P", "Qtd. M", "Qtd. G", "Descrição"}, 0);
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.getSelectionModel().addListSelectionListener(this); // Adiciona listener de seleção
        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        add(scrollTabela, BorderLayout.NORTH);

        // Painel para os campos de edição
        painelEdicao = new JPanel();
        painelEdicao.setLayout(new GridBagLayout());
        add(painelEdicao, BorderLayout.CENTER);
        inicializarCamposEdicao();

        // Botão Salvar Edição
        botaoSalvarEdicao = new JButton("Salvar Alterações");
        botaoSalvarEdicao.addActionListener(this);
        GridBagConstraints gbcBotaoSalvar = new GridBagConstraints();
        gbcBotaoSalvar.gridx = 0;
        gbcBotaoSalvar.gridy = 11; // Ajustei a posição
        gbcBotaoSalvar.gridwidth = 3;
        gbcBotaoSalvar.insets = new Insets(10, 10, 10, 10);
        gbcBotaoSalvar.fill = GridBagConstraints.HORIZONTAL;
        painelEdicao.add(botaoSalvarEdicao, gbcBotaoSalvar);

        carregarProdutos();
        // REMOVI A LINHA setVisible(true);
    }

    /**
     * Inicializa e adiciona os campos de edição ao painel de edição.
     */
    private void inicializarCamposEdicao() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Labels e botões para as imagens
        for (int i = 0; i < MAX_IMAGENS; i++) {
            JLabel label = new JLabel("Imagem " + (i + 1) + ":");
            painelEdicao.add(label, gbc);
            gbc.gridx++;

            JLabel imagemLabel = new JLabel("Sem imagem");
            labelsImagem.add(imagemLabel);
            painelEdicao.add(imagemLabel, gbc);
            gbc.gridx++;

            JButton botaoImagem = new JButton("Selecionar");
            botaoImagem.setActionCommand("imagem_" + i);
            botaoImagem.addActionListener(this);
            botoesImagem.add(botaoImagem);
            painelEdicao.add(botaoImagem, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }

        // Campo para Quantidade P
        painelEdicao.add(new JLabel("Quantidade P:"), gbc);
        gbc.gridx++;
        campoQuantidadeP = new JTextField(10);
        painelEdicao.add(campoQuantidadeP, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Campo para Quantidade M
        painelEdicao.add(new JLabel("Quantidade M:"), gbc);
        gbc.gridx++;
        campoQuantidadeM = new JTextField(10);
        painelEdicao.add(campoQuantidadeM, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Campo para Quantidade G
        painelEdicao.add(new JLabel("Quantidade G:"), gbc);
        gbc.gridx++;
        campoQuantidadeG = new JTextField(10);
        painelEdicao.add(campoQuantidadeG, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Campo para Valor
        painelEdicao.add(new JLabel("Valor:"), gbc);
        gbc.gridx++;
        campoValor = new JTextField(10);
        painelEdicao.add(campoValor, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Campo para Descrição (ainda mais aumentado)
        painelEdicao.add(new JLabel("Descrição:"), gbc);
        gbc.gridx++;
        campoDescricao = new JTextArea(15, 40); // Aumentei ainda mais as linhas e colunas
        scrollDescricao = new JScrollPane(campoDescricao);
        painelEdicao.add(scrollDescricao, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void carregarProdutos() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT id, valor, quantidade_p, quantidade_m, quantidade_g, descricao FROM produtos";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{rs.getInt("id"), rs.getDouble("valor"),
                        rs.getInt("quantidade_p"), rs.getInt("quantidade_m"),
                        rs.getInt("quantidade_g"), rs.getString("descricao")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void carregarDadosProduto(int produtoId) {
        String sql = "SELECT valor, quantidade_p, quantidade_m, quantidade_g, descricao FROM produtos WHERE id = ?"; // Removi as colunas de imagem
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                campoValor.setText(String.valueOf(rs.getDouble("valor")));
                campoQuantidadeP.setText(String.valueOf(rs.getInt("quantidade_p")));
                campoQuantidadeM.setText(String.valueOf(rs.getInt("quantidade_m")));
                campoQuantidadeG.setText(String.valueOf(rs.getInt("quantidade_g")));
                campoDescricao.setText(rs.getString("descricao"));

                // Não carregamos mais dados de imagem aqui
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void atualizarLabelsImagens(List<String> caminhos) {
        for (int i = 0; i < MAX_IMAGENS; i++) {
            labelsImagem.get(i).setIcon(null);
            labelsImagem.get(i).setText("Funcionalidade de imagem desativada");
        }
    }

    private void salvarEdicaoProduto() {
        if (produtoIdSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String valorStr = campoValor.getText();
        String qtdPStr = campoQuantidadeP.getText();
        String qtdMStr = campoQuantidadeM.getText();
        String qtdGStr = campoQuantidadeG.getText();
        String descricao = campoDescricao.getText();

        try {
            double valor = Double.parseDouble(valorStr);
            int quantidadeP = Integer.parseInt(qtdPStr);
            int quantidadeM = Integer.parseInt(qtdMStr);
            int quantidadeG = Integer.parseInt(qtdGStr);

            String sql = "UPDATE produtos SET valor = ?, quantidade_p = ?, quantidade_m = ?, quantidade_g = ?, descricao = ? WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, valor);
                pstmt.setInt(2, quantidadeP);
                pstmt.setInt(3, quantidadeM);
                pstmt.setInt(4, quantidadeG);
                pstmt.setString(5, descricao);
                pstmt.setInt(6, produtoIdSelecionado);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarProdutos(); // Recarrega a tabela para mostrar as alterações
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao atualizar o produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar edição: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos válidos para valor e quantidades.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.startsWith("imagem_")) {
            int index = Integer.parseInt(actionCommand.split("_")[1]);
            // A funcionalidade de seleção de imagem está desativada para evitar erros com as colunas ausentes.
            JOptionPane.showMessageDialog(this, "Funcionalidade de seleção de imagem desativada.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } else if (e.getSource() == botaoSalvarEdicao) {
            salvarEdicaoProduto();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = tabelaProdutos.getSelectedRow();
            if (selectedRow != -1) {
                produtoIdSelecionado = (int) modeloTabela.getValueAt(selectedRow, 0);
                carregarDadosProduto(produtoIdSelecionado);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEditarProduto());
    }
}