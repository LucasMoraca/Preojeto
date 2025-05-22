package Site;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelaAdicionarProduto extends JFrame {

    private JTextField quantidadePField;
    private JTextField quantidadeMField;
    private JTextField quantidadeGField;
    private JTextField valorField;
    private JTextArea descricaoArea;
    private JButton salvarProdutoButton;
    private List<String> imagePaths;
    private JButton adicionarImagem1Button;
    private JButton adicionarImagem2Button;
    private JButton adicionarImagem3Button;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String IMAGES_DIRECTORY = "imagens_produtos"; // Diretório para salvar as imagens

    public TelaAdicionarProduto() {
        setTitle("Adicionar Produto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        imagePaths = new ArrayList<>();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adicionarImagem1Button = new JButton("Adicionar Imagem 1");
        adicionarImagem2Button = new JButton("Adicionar Imagem 2");
        adicionarImagem3Button = new JButton("Adicionar Imagem 3");
        topPanel.add(adicionarImagem1Button);
        topPanel.add(adicionarImagem2Button);
        topPanel.add(adicionarImagem3Button);
        add(topPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Quantidade (P):"));
        quantidadePField = new JTextField();
        inputPanel.add(quantidadePField);

        inputPanel.add(new JLabel("Quantidade (M):"));
        quantidadeMField = new JTextField();
        inputPanel.add(quantidadeMField);

        inputPanel.add(new JLabel("Quantidade (G):"));
        quantidadeGField = new JTextField();
        inputPanel.add(quantidadeGField);

        inputPanel.add(new JLabel("Valor:"));
        valorField = new JTextField();
        inputPanel.add(valorField);

        inputPanel.add(new JLabel("Descrição:"));
        descricaoArea = new JTextArea(5, 20);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        inputPanel.add(new JScrollPane(descricaoArea));

        add(inputPanel, BorderLayout.CENTER);

        salvarProdutoButton = new JButton("Salvar Produto");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(salvarProdutoButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Ação para adicionar imagens
        adicionarImagem1Button.addActionListener(e -> selecionarImagem(1));
        adicionarImagem2Button.addActionListener(e -> selecionarImagem(2));
        adicionarImagem3Button.addActionListener(e -> selecionarImagem(3));

        // Ação para salvar o produto
        salvarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarProdutoNoBanco();
            }
        });

        setVisible(true);
    }

    private void selecionarImagem(int imagemNumero) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (imagePaths.size() < imagemNumero) {
                imagePaths.add(selectedFile.getAbsolutePath());
            } else {
                imagePaths.set(imagemNumero - 1, selectedFile.getAbsolutePath());
            }
            JOptionPane.showMessageDialog(this, "Imagem " + imagemNumero + " selecionada: " + selectedFile.getName(), "Imagem Selecionada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void salvarProdutoNoBanco() {
        String quantidadePStr = quantidadePField.getText();
        String quantidadeMStr = quantidadeMField.getText();
        String quantidadeGStr = quantidadeGField.getText();
        String valorStr = valorField.getText();
        String descricao = descricaoArea.getText();

        if (quantidadePStr.isEmpty() || quantidadeMStr.isEmpty() || quantidadeGStr.isEmpty() || valorStr.isEmpty() || descricao.isEmpty() || imagePaths.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos e selecione pelo menos uma imagem.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantidadeP = Integer.parseInt(quantidadePStr);
            int quantidadeM = Integer.parseInt(quantidadeMStr);
            int quantidadeG = Integer.parseInt(quantidadeGStr);
            double valor = Double.parseDouble(valorStr);

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "INSERT INTO produtos (nome, descricao, valor, quantidade_p, quantidade_m, quantidade_g, imagens1_path, imagens2_path, imagens3_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Por enquanto, vamos usar o nome do arquivo como path no banco
            String imageName1 = imagePaths.size() > 0 ? Paths.get(imagePaths.get(0)).getFileName().toString() : null;
            String imageName2 = imagePaths.size() > 1 ? Paths.get(imagePaths.get(1)).getFileName().toString() : null;
            String imageName3 = imagePaths.size() > 2 ? Paths.get(imagePaths.get(2)).getFileName().toString() : null;

            pstmt.setString(1, "Nome do Produto (a implementar)"); // TODO: Adicionar campo nome
            pstmt.setString(2, descricao);
            pstmt.setDouble(3, valor);
            pstmt.setInt(4, quantidadeP);
            pstmt.setInt(5, quantidadeM);
            pstmt.setInt(6, quantidadeG);
            pstmt.setString(7, imageName1);
            pstmt.setString(8, imageName2);
            pstmt.setString(9, imageName3);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
                // TODO: Lógica para copiar as imagens para o diretório do servidor
                copiarImagensParaDiretorio();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao salvar o produto.", "Erro", JOptionPane.ERROR_MESSAGE);
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

    private void copiarImagensParaDiretorio() {
        Path directory = Paths.get(IMAGES_DIRECTORY);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao criar diretório de imagens: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        for (String sourcePath : imagePaths) {
            if (sourcePath != null && !sourcePath.isEmpty()) {
                Path source = Paths.get(sourcePath);
                Path destination = directory.resolve(source.getFileName());
                try {
                    Files.copy(source, destination);
                    System.out.println("Imagem copiada para: " + destination.toString());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Erro ao copiar imagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Imagens salvas no servidor.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limparCampos() {
        quantidadePField.setText("");
        quantidadeMField.setText("");
        quantidadeGField.setText("");
        valorField.setText("");
        descricaoArea.setText("");
        imagePaths.clear();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaAdicionarProduto());
    }
}