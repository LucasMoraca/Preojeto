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
import java.sql.ResultSet;

public class TelaEditarProduto extends JFrame implements ActionListener, ListSelectionListener {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JButton botaoSalvarEdicao;
    private JButton botaoEliminarProduto; // Novo botão para eliminar

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
        setSize(1100, 850); // Aumentei um pouco mais a largura
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        // Tabela para listar os produtos (agora inclui colunas de imagem no modelo)
        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Valor", "Qtd. P", "Qtd. M", "Qtd. G", "Descrição", "Imagem 1", "Imagem 2", "Imagem 3"}, 0);
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
        gbcBotaoSalvar.gridy = 12; // Ajustei a posição
        gbcBotaoSalvar.gridwidth = 2; // Ocupa duas colunas agora
        gbcBotaoSalvar.insets = new Insets(10, 10, 10, 5);
        gbcBotaoSalvar.fill = GridBagConstraints.HORIZONTAL;
        painelEdicao.add(botaoSalvarEdicao, gbcBotaoSalvar);

        // Botão Eliminar Produto
        botaoEliminarProduto = new JButton("Eliminar Produto");
        botaoEliminarProduto.addActionListener(this);
        GridBagConstraints gbcBotaoEliminar = new GridBagConstraints();
        gbcBotaoEliminar.gridx = 2;
        gbcBotaoEliminar.gridy = 12; // Ajustei a posição
        gbcBotaoEliminar.gridwidth = 1;
        gbcBotaoEliminar.insets = new Insets(10, 5, 10, 10);
        gbcBotaoEliminar.fill = GridBagConstraints.HORIZONTAL;
        painelEdicao.add(botaoEliminarProduto, gbcBotaoEliminar);

        carregarProdutos();
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
        gbc.gridwidth = 1; // Reset gridwidth

        // Labels e botões para as imagens
        for (int i = 0; i < MAX_IMAGENS; i++) {
            JLabel label = new JLabel("Imagem " + (i + 1) + ":");
            painelEdicao.add(label, gbc);
            gbc.gridx++;

            labelsImagem.add(new JLabel());
            painelEdicao.add(labelsImagem.get(i), gbc);
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
        gbc.gridwidth = 2;
        painelEdicao.add(campoQuantidadeP, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        // Campo para Quantidade M
        painelEdicao.add(new JLabel("Quantidade M:"), gbc);
        gbc.gridx++;
        campoQuantidadeM = new JTextField(10);
        gbc.gridwidth = 2;
        painelEdicao.add(campoQuantidadeM, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        // Campo para Quantidade G
        painelEdicao.add(new JLabel("Quantidade G:"), gbc);
        gbc.gridx++;
        campoQuantidadeG = new JTextField(10);
        gbc.gridwidth = 2;
        painelEdicao.add(campoQuantidadeG, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        // Campo para Valor
        painelEdicao.add(new JLabel("Valor:"), gbc);
        gbc.gridx++;
        campoValor = new JTextField(10);
        gbc.gridwidth = 2;
        painelEdicao.add(campoValor, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        // Campo para Descrição (largura aumentada)
        painelEdicao.add(new JLabel("Descrição:"), gbc);
        gbc.gridx++;
        campoDescricao = new JTextArea(10, 60); // Aumentei as colunas para 60
        scrollDescricao = new JScrollPane(campoDescricao);
        gbc.gridwidth = 2;
        painelEdicao.add(scrollDescricao, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void carregarProdutos() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT id, valor, quantidade_p, quantidade_m, quantidade_g, descricao, imagem1_path, imagem2_path, imagem3_path FROM produtos";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getDouble("valor"),
                        rs.getInt("quantidade_p"),
                        rs.getInt("quantidade_m"),
                        rs.getInt("quantidade_g"),
                        rs.getString("descricao"),
                        rs.getString("imagem1_path"),
                        rs.getString("imagem2_path"),
                        rs.getString("imagem3_path")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void carregarDadosProduto(int produtoId) {
        String sql = "SELECT valor, quantidade_p, quantidade_m, quantidade_g, descricao, imagem1_path, imagem2_path, imagem3_path FROM produtos WHERE id = ?";
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

                // Carregar caminhos das imagens
                caminhosImagens.set(0, rs.getString("imagem1_path"));
                caminhosImagens.set(1, rs.getString("imagem2_path"));
                caminhosImagens.set(2, rs.getString("imagem3_path"));

                atualizarLabelsImagens(caminhosImagens);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void atualizarLabelsImagens(List<String> caminhos) {
        for (int i = 0; i < MAX_IMAGENS; i++) {
            labelsImagem.get(i).setIcon(null);
            if (caminhos.get(i) != null && !caminhos.get(i).isEmpty()) {
                try {
                    BufferedImage img = ImageIO.read(new File(caminhos.get(i)));
                    if (img != null) {
                        Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        labelsImagem.get(i).setIcon(new ImageIcon(scaledImg));
                        labelsImagem.get(i).setText(null);
                    } else {
                        labelsImagem.get(i).setText("Imagem " + (i + 1) + " inválida");
                    }
                } catch (IOException e) {
                    labelsImagem.get(i).setText("Erro ao carregar imagem " + (i + 1));
                    e.printStackTrace();
                }
            } else {
                labelsImagem.get(i).setText("Sem imagem");
            }
        }
    }

    private void selecionarImagem(int index) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            caminhosImagens.set(index, selectedFile.getAbsolutePath());
            atualizarLabelsImagens(caminhosImagens);
        }
    }

    private void eliminarProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int produtoIdParaEliminar = (int) modeloTabela.getValueAt(selectedRow, 0);
        String nomeProduto = (String) modeloTabela.getValueAt(selectedRow, 5);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente eliminar o produto com ID " + produtoIdParaEliminar + "?\nDescrição: " + nomeProduto,
                "Confirmar Eliminação", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM produtos WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, produtoIdParaEliminar);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Produto eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarProdutos(); // Recarrega a tabela
                    // Limpar os campos de edição após a eliminação
                    campoValor.setText("");
                    campoQuantidadeP.setText("");
                    campoQuantidadeM.setText("");
                    campoQuantidadeG.setText("");
                    campoDescricao.setText("");
                    produtoIdSelecionado = -1;
                    // Limpar caminhos de imagem
                    for (int i = 0; i < MAX_IMAGENS; i++) {
                        caminhosImagens.set(i, null);
                        atualizarLabelsImagens(caminhosImagens);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao eliminar o produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao eliminar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
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
        String imagem1Path = caminhosImagens.get(0);
        String imagem2Path = caminhosImagens.get(1);
        String imagem3Path = caminhosImagens.get(2);

        try {
            double valor = Double.parseDouble(valorStr);
            int quantidadeP = Integer.parseInt(qtdPStr);
            int quantidadeM = Integer.parseInt(qtdMStr);
            int quantidadeG = Integer.parseInt(qtdGStr);

            String sql = "UPDATE produtos SET valor = ?, quantidade_p = ?, quantidade_m = ?, quantidade_g = ?, descricao = ?, imagem1_path = ?, imagem2_path = ?, imagem3_path = ? WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, valor);
                pstmt.setInt(2, quantidadeP);
                pstmt.setInt(3, quantidadeM);
                pstmt.setInt(4, quantidadeG);
                pstmt.setString(5, descricao);
                pstmt.setString(6, imagem1Path);
                pstmt.setString(7, imagem2Path);
                pstmt.setString(8, imagem3Path);
                pstmt.setInt(9, produtoIdSelecionado);

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
            int index = Integer.parseInt(actionCommand.substring(actionCommand.indexOf("_") + 1));
            selecionarImagem(index);
        } else if (e.getSource() == botaoSalvarEdicao) {
            salvarEdicaoProduto();
        } else if (e.getSource() == botaoEliminarProduto) {
            eliminarProduto();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = tabelaProdutos.getSelectedRow();
            if (selectedRow != -1) {
                produtoIdSelecionado = (int) modeloTabela.getValueAt(selectedRow, 0);
                carregarDadosProduto(produtoIdSelecionado);
            } else {
                // Limpar campos de edição se nenhuma linha estiver selecionada
                campoValor.setText("");
                campoQuantidadeP.setText("");
                campoQuantidadeM.setText("");
                campoQuantidadeG.setText("");
                campoDescricao.setText("");
                for (int i = 0; i < MAX_IMAGENS; i++) {
                    caminhosImagens.set(i, null);
                    labelsImagem.get(i).setIcon(null);
                    labelsImagem.get(i).setText("Sem imagem");
                }
                produtoIdSelecionado = -1;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEditarProduto());
    }
}