package Site;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class TelaEditarProduto extends JFrame implements ActionListener, ListSelectionListener {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JButton botaoSalvarEdicao;
    private JButton botaoEliminarProduto;

    private JPanel painelEdicao;
    private List<JButton> botoesImagem;
    private List<JLabel> labelsImagem;
    private JTextField campoQuantidadeP;
    private JTextField campoQuantidadeM;
    private JTextField campoQuantidadeG;
    private JTextField campoValor;
    private JTextArea campoDescricao;
    private JScrollPane scrollDescricao;

    private static final int MAX_IMAGENS = 3;
    private List<String> caminhosImagens;
    private int produtoIdSelecionado = -1;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaEditarProduto() {
        setTitle("Editar Produto");
        setSize(1100, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(10, 10));

        caminhosImagens = new ArrayList<>();
        for (int i = 0; i < MAX_IMAGENS; i++) {
            caminhosImagens.add(null);
        }

        botoesImagem = new ArrayList<>();
        labelsImagem = new ArrayList<>();

        // Tabela de produtos
        modeloTabela = new DefaultTableModel(
            new Object[]{"ID", "Valor", "Qtd. P", "Qtd. M", "Qtd. G", "Descrição", "Imagem 1", "Imagem 2", "Imagem 3"}, 0
        );
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.getSelectionModel().addListSelectionListener(this);
        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        add(scrollTabela, BorderLayout.NORTH);

        // Painel de edição
        painelEdicao = new JPanel(new GridBagLayout());
        add(painelEdicao, BorderLayout.CENTER);

        inicializarCamposEdicao();

        // Botões
        botaoSalvarEdicao = new JButton("Salvar Alterações");
        botaoSalvarEdicao.addActionListener(this);
        GridBagConstraints gbcSalvar = new GridBagConstraints();
        gbcSalvar.gridx = 0;
        gbcSalvar.gridy = 12;
        gbcSalvar.gridwidth = 2;
        gbcSalvar.insets = new Insets(10, 10, 10, 5);
        gbcSalvar.fill = GridBagConstraints.HORIZONTAL;
        painelEdicao.add(botaoSalvarEdicao, gbcSalvar);

        botaoEliminarProduto = new JButton("Eliminar Produto");
        botaoEliminarProduto.addActionListener(this);
        GridBagConstraints gbcEliminar = new GridBagConstraints();
        gbcEliminar.gridx = 2;
        gbcEliminar.gridy = 12;
        gbcEliminar.gridwidth = 1;
        gbcEliminar.insets = new Insets(10, 5, 10, 10);
        gbcEliminar.fill = GridBagConstraints.HORIZONTAL;
        painelEdicao.add(botaoEliminarProduto, gbcEliminar);

        carregarProdutos();
    }

    private void inicializarCamposEdicao() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (int i = 0; i < MAX_IMAGENS; i++) {
            JLabel label = new JLabel("Imagem " + (i + 1) + ":");
            painelEdicao.add(label, gbc);
            gbc.gridx++;

            JLabel labelImagem = new JLabel("Sem imagem");
            labelsImagem.add(labelImagem);
            painelEdicao.add(labelImagem, gbc);
            gbc.gridx++;

            JButton botaoImagem = new JButton("Selecionar");
            botaoImagem.setActionCommand("imagem_" + i);
            botaoImagem.addActionListener(this);
            botoesImagem.add(botaoImagem);
            painelEdicao.add(botaoImagem, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
        }

        painelEdicao.add(new JLabel("Quantidade P:"), gbc);
        gbc.gridx++;
        campoQuantidadeP = new JTextField(10);
        gbc.gridwidth = 2;
        painelEdicao.add(campoQuantidadeP, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        painelEdicao.add(new JLabel("Quantidade M:"), gbc);
        gbc.gridx++;
        campoQuantidadeM = new JTextField(10);
        gbc.gridwidth = 2;
        painelEdicao.add(campoQuantidadeM, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        painelEdicao.add(new JLabel("Quantidade G:"), gbc);
        gbc.gridx++;
        campoQuantidadeG = new JTextField(10);
        gbc.gridwidth = 2;
        painelEdicao.add(campoQuantidadeG, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        painelEdicao.add(new JLabel("Valor:"), gbc);
        gbc.gridx++;
        campoValor = new JTextField(10);
        gbc.gridwidth = 2;
        painelEdicao.add(campoValor, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;

        painelEdicao.add(new JLabel("Descrição:"), gbc);
        gbc.gridx++;
        campoDescricao = new JTextArea(10, 60);
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
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void carregarDadosProduto(int produtoId) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
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

                caminhosImagens.set(0, rs.getString("imagem1_path"));
                caminhosImagens.set(1, rs.getString("imagem2_path"));
                caminhosImagens.set(2, rs.getString("imagem3_path"));

                atualizarLabelsImagens();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do produto: " + e.getMessage());
        }
    }

    private void atualizarLabelsImagens() {
        for (int i = 0; i < MAX_IMAGENS; i++) {
            labelsImagem.get(i).setIcon(null);
            String caminho = caminhosImagens.get(i);
            if (caminho != null && !caminho.isEmpty()) {
                try {
                    BufferedImage img = ImageIO.read(new File(caminho));
                    if (img != null) {
                        Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        labelsImagem.get(i).setIcon(new ImageIcon(scaledImg));
                        labelsImagem.get(i).setText("");
                    } else {
                        labelsImagem.get(i).setText("Imagem inválida");
                    }
                } catch (IOException e) {
                    labelsImagem.get(i).setText("Erro na imagem");
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
        int retorno = fileChooser.showOpenDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            caminhosImagens.set(index, arquivo.getAbsolutePath());
            atualizarLabelsImagens();
        }
    }

    private void eliminarProduto() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para eliminar.");
            return;
        }

        int idProduto = (int) modeloTabela.getValueAt(linhaSelecionada, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Deseja eliminar o produto ID " + idProduto + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM produtos WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idProduto);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Produto eliminado com sucesso.");
                carregarProdutos();

                limparCampos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao eliminar produto: " + e.getMessage());
            }
        }
    }

    private void salvarEdicaoProduto() {
        if (produtoIdSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para editar.");
            return;
        }

        try {
            double valor = Double.parseDouble(campoValor.getText());
            int qtdP = Integer.parseInt(campoQuantidadeP.getText());
            int qtdM = Integer.parseInt(campoQuantidadeM.getText());
            int qtdG = Integer.parseInt(campoQuantidadeG.getText());
            String descricao = campoDescricao.getText();

            String sql = "UPDATE produtos SET valor = ?, quantidade_p = ?, quantidade_m = ?, quantidade_g = ?, descricao = ?, imagem1_path = ?, imagem2_path = ?, imagem3_path = ? WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDouble(1, valor);
                pstmt.setInt(2, qtdP);
                pstmt.setInt(3, qtdM);
                pstmt.setInt(4, qtdG);
                pstmt.setString(5, descricao);
                pstmt.setString(6, caminhosImagens.get(0));
                pstmt.setString(7, caminhosImagens.get(1));
                pstmt.setString(8, caminhosImagens.get(2));
                pstmt.setInt(9, produtoIdSelecionado);

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso.");
                carregarProdutos();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Insira valores válidos para quantidade e valor.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar produto: " + e.getMessage());
        }
    }

    private void limparCampos() {
        campoValor.setText("");
        campoQuantidadeP.setText("");
        campoQuantidadeM.setText("");
        campoQuantidadeG.setText("");
        campoDescricao.setText("");
        produtoIdSelecionado = -1;
        for (int i = 0; i < MAX_IMAGENS; i++) {
            caminhosImagens.set(i, null);
            labelsImagem.get(i).setIcon(null);
            labelsImagem.get(i).setText("Sem imagem");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String acao = e.getActionCommand();
        if (acao.startsWith("imagem_")) {
            int index = Integer.parseInt(acao.substring(7));
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
            int linhaSelecionada = tabelaProdutos.getSelectedRow();
            if (linhaSelecionada != -1) {
                produtoIdSelecionado = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
                carregarDadosProduto(produtoIdSelecionado);
            } else {
                limparCampos();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEditarProduto().setVisible(true));
    }
}
