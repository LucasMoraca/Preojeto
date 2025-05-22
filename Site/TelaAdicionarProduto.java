package Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TelaAdicionarProduto extends JFrame implements ActionListener {

    private List<JButton> botoesImagem;
    private List<JLabel> labelsImagem;
    private JTextField campoQuantidadeP;
    private JTextField campoQuantidadeM;
    private JTextField campoQuantidadeG;
    private JTextField campoValor;
    private JTextArea campoDescricao;
    private JButton botaoSalvar;

    private static final int MAX_IMAGENS = 3;
    private List<String> caminhosImagens;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaAdicionarProduto() {
        setTitle("Adicionar Produto");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        caminhosImagens = new ArrayList<>();
        botoesImagem = new ArrayList<>();
        labelsImagem = new ArrayList<>();

        // Painel imagens
        JPanel painelImagens = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (int i = 0; i < MAX_IMAGENS; i++) {
            JButton botaoImagem = new JButton("Adicionar Imagem " + (i + 1));
            botaoImagem.setActionCommand("imagem_" + i);
            botaoImagem.addActionListener(this);
            botoesImagem.add(botaoImagem);

            JLabel labelImagem = new JLabel();
            labelImagem.setPreferredSize(new Dimension(100, 100));
            labelImagem.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            labelsImagem.add(labelImagem);

            JPanel painelImagemIndividual = new JPanel(new BorderLayout());
            painelImagemIndividual.add(botaoImagem, BorderLayout.NORTH);
            painelImagemIndividual.add(labelImagem, BorderLayout.CENTER);
            painelImagens.add(painelImagemIndividual);
        }
        add(painelImagens, BorderLayout.NORTH);

        // Painel tamanhos
        JPanel painelTamanhos = new JPanel(new GridLayout(3, 2, 10, 10));
        painelTamanhos.add(new JLabel("Quantidade (P):", SwingConstants.RIGHT));
        campoQuantidadeP = new JTextField();
        painelTamanhos.add(campoQuantidadeP);
        painelTamanhos.add(new JLabel("Quantidade (M):", SwingConstants.RIGHT));
        campoQuantidadeM = new JTextField();
        painelTamanhos.add(campoQuantidadeM);
        painelTamanhos.add(new JLabel("Quantidade (G):", SwingConstants.RIGHT));
        campoQuantidadeG = new JTextField();
        painelTamanhos.add(campoQuantidadeG);

        // Painel valor e descrição
        JPanel painelDetalhes = new JPanel(new GridLayout(2, 2, 10, 10));
        painelDetalhes.add(new JLabel("Valor:", SwingConstants.RIGHT));
        campoValor = new JTextField();
        painelDetalhes.add(campoValor);
        painelDetalhes.add(new JLabel("Descrição:", SwingConstants.RIGHT));
        campoDescricao = new JTextArea();
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);
        JScrollPane scrollDescricao = new JScrollPane(campoDescricao);
        painelDetalhes.add(scrollDescricao);

        JPanel painelInfoProduto = new JPanel(new BorderLayout());
        painelInfoProduto.add(painelTamanhos, BorderLayout.NORTH);
        painelInfoProduto.add(painelDetalhes, BorderLayout.CENTER);

        add(painelInfoProduto, BorderLayout.CENTER);

        botaoSalvar = new JButton("Salvar Produto");
        botaoSalvar.addActionListener(this);
        JPanel painelSalvar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelSalvar.add(botaoSalvar);
        add(painelSalvar, BorderLayout.SOUTH);
    }

    public void mostrar() {
        setVisible(true);
        toFront();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private boolean salvarProdutoNoBanco(List<String> caminhosImagens, int quantidadeP, int quantidadeM, int quantidadeG, double valor, String descricao) {
        String sql = "INSERT INTO produtos (imagem1_path, imagem2_path, imagem3_path, quantidade_p, quantidade_m, quantidade_g, valor, descricao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, caminhosImagens.size() > 0 ? caminhosImagens.get(0) : null);
            pstmt.setString(2, caminhosImagens.size() > 1 ? caminhosImagens.get(1) : null);
            pstmt.setString(3, caminhosImagens.size() > 2 ? caminhosImagens.get(2) : null);
            pstmt.setInt(4, quantidadeP);
            pstmt.setInt(5, quantidadeM);
            pstmt.setInt(6, quantidadeG);
            pstmt.setDouble(7, valor);
            pstmt.setString(8, descricao);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.startsWith("imagem_")) {
            int index = Integer.parseInt(command.split("_")[1]);
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String caminho = selectedFile.getAbsolutePath();

                if (index < caminhosImagens.size()) {
                    caminhosImagens.set(index, caminho);
                } else {
                    // Preenche com null até o índice para evitar IndexOutOfBounds
                    while (caminhosImagens.size() < index) {
                        caminhosImagens.add(null);
                    }
                    caminhosImagens.add(caminho);
                }

                ImageIcon icon = new ImageIcon(new ImageIcon(caminho).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                labelsImagem.get(index).setIcon(icon);
                labelsImagem.get(index).setText(null);
            }
        } else if (e.getSource() == botaoSalvar) {
            try {
                int quantidadeP = Integer.parseInt(campoQuantidadeP.getText().isEmpty() ? "0" : campoQuantidadeP.getText());
                int quantidadeM = Integer.parseInt(campoQuantidadeM.getText().isEmpty() ? "0" : campoQuantidadeM.getText());
                int quantidadeG = Integer.parseInt(campoQuantidadeG.getText().isEmpty() ? "0" : campoQuantidadeG.getText());

                String valorTexto = campoValor.getText().trim();
                if (valorTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, informe o valor do produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double valor = Double.parseDouble(valorTexto);

                String descricao = campoDescricao.getText();

                if (salvarProdutoNoBanco(caminhosImagens, quantidadeP, quantidadeM, quantidadeG, valor, descricao)) {
                    JOptionPane.showMessageDialog(this, "Produto adicionado com sucesso!");

                    // Limpar campos e imagens
                    for (JLabel label : labelsImagem) {
                        label.setIcon(null);
                        label.setText("");
                    }
                    caminhosImagens.clear();
                    campoQuantidadeP.setText("");
                    campoQuantidadeM.setText("");
                    campoQuantidadeG.setText("");
                    campoValor.setText("");
                    campoDescricao.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao adicionar o produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "As quantidades e o valor devem ser números válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaAdicionarProduto().mostrar());
    }
}
