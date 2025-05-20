// TelaAdicionarProduto.java
package Site;

import javax.swing.*; // Importa classes para a interface gráfica Swing
import java.awt.*; // Importa classes para layouts e componentes gráficos
import java.awt.event.ActionEvent; // Importa a classe para eventos de ação (cliques em botões)
import java.awt.event.ActionListener; // Importa a interface para lidar com eventos de ação
import java.util.ArrayList; // Importa a classe ArrayList para listas dinâmicas
import java.util.List; // Importa a interface List para trabalhar com listas
import java.sql.Connection; // Importa a classe Connection para a conexão com o banco de dados
import java.sql.DriverManager; // Importa a classe DriverManager para gerenciar conexões JDBC
import java.sql.PreparedStatement; // Importa a classe PreparedStatement para consultas SQL parametrizadas
import java.sql.SQLException; // Importa a classe SQLException para lidar com erros de SQL
import java.io.File; // Importa a classe File para manipulação de arquivos
import javax.swing.filechooser.FileNameExtensionFilter; // Importa a classe para filtros de tipo de arquivo

// A classe TelaAdicionarProduto estende JFrame (janela) e implementa ActionListener (para eventos de botão)
public class TelaAdicionarProduto extends JFrame implements ActionListener {

    private List<JButton> botoesImagem; // Lista para os botões de adicionar imagem
    private List<JLabel> labelsImagem; // Lista para exibir as prévias das imagens
    private JTextField campoQuantidadeP; // Campo para inserir a quantidade do tamanho P
    private JTextField campoQuantidadeM; // Campo para inserir a quantidade do tamanho M
    private JTextField campoQuantidadeG; // Campo para inserir a quantidade do tamanho G
    private JTextField campoValor; // Campo para inserir o valor do produto
    private JTextArea campoDescricao; // Área de texto para a descrição do produto
    private JButton botaoSalvar; // Botão para salvar o produto

    private static final int MAX_IMAGENS = 3; // Define o número máximo de imagens que podem ser adicionadas
    private List<String> caminhosImagens; // Lista para armazenar os caminhos dos arquivos de imagem selecionados

    // Configurações do banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Construtor da TelaAdicionarProduto
    public TelaAdicionarProduto() {
        setTitle("Adicionar Produto"); // Define o título da janela
        setSize(600, 550); // Define o tamanho da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define a ação ao fechar a janela
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setLayout(new BorderLayout(10, 10)); // Define o layout principal como BorderLayout com espaçamento

        caminhosImagens = new ArrayList<>(); // Inicializa a lista de caminhos de imagens
        botoesImagem = new ArrayList<>(); // Inicializa a lista de botões de imagem
        labelsImagem = new ArrayList<>(); // Inicializa a lista de labels de imagem

        // Painel para os botões e prévias das imagens
        JPanel painelImagens = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (int i = 0; i < MAX_IMAGENS; i++) {
            JButton botaoImagem = new JButton("Adicionar Imagem " + (i + 1));
            botaoImagem.setActionCommand("imagem_" + i); // Define um comando de ação para identificar o botão
            botaoImagem.addActionListener(this); // Adiciona este objeto como ouvinte de ação
            botoesImagem.add(botaoImagem); // Adiciona o botão à lista

            JLabel labelImagem = new JLabel();
            labelImagem.setPreferredSize(new Dimension(100, 100)); // Define o tamanho preferido do label
            labelImagem.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Adiciona uma borda ao label
            labelsImagem.add(labelImagem); // Adiciona o label à lista

            JPanel painelImagemIndividual = new JPanel(new BorderLayout());
            painelImagemIndividual.add(botaoImagem, BorderLayout.NORTH); // Botão na parte superior
            painelImagemIndividual.add(labelImagem, BorderLayout.CENTER); // Label no centro
            painelImagens.add(painelImagemIndividual); // Adiciona o painel individual ao painel de imagens
        }
        add(painelImagens, BorderLayout.NORTH); // Adiciona o painel de imagens à parte superior da janela

        // Painel para as quantidades por tamanho
        JPanel painelTamanhos = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 linhas, 2 colunas, com espaçamento
        painelTamanhos.add(new JLabel("Quantidade (P):", SwingConstants.RIGHT)); // Rótulo para quantidade P
        campoQuantidadeP = new JTextField();
        painelTamanhos.add(campoQuantidadeP); // Campo para quantidade P
        painelTamanhos.add(new JLabel("Quantidade (M):", SwingConstants.RIGHT)); // Rótulo para quantidade M
        campoQuantidadeM = new JTextField();
        painelTamanhos.add(campoQuantidadeM); // Campo para quantidade M
        painelTamanhos.add(new JLabel("Quantidade (G):", SwingConstants.RIGHT)); // Rótulo para quantidade G
        campoQuantidadeG = new JTextField();
        painelTamanhos.add(campoQuantidadeG); // Campo para quantidade G

        // Painel para o valor e a descrição
        JPanel painelDetalhes = new JPanel(new GridLayout(2, 2, 10, 10)); // 2 linhas, 2 colunas, com espaçamento
        painelDetalhes.add(new JLabel("Valor:", SwingConstants.RIGHT)); // Rótulo para o valor
        campoValor = new JTextField();
        painelDetalhes.add(campoValor); // Campo para o valor
        painelDetalhes.add(new JLabel("Descrição:", SwingConstants.RIGHT)); // Rótulo para a descrição
        campoDescricao = new JTextArea();
        campoDescricao.setLineWrap(true); // Ativa a quebra de linha automática
        campoDescricao.setWrapStyleWord(true); // Quebra a linha nas palavras
        JScrollPane scrollDescricao = new JScrollPane(campoDescricao); // Adiciona scroll se a descrição for longa
        painelDetalhes.add(scrollDescricao); // Área de texto para a descrição

        // Painel para agrupar as informações de tamanho e detalhes
        JPanel painelInfoProduto = new JPanel(new BorderLayout());
        painelInfoProduto.add(painelTamanhos, BorderLayout.NORTH); // Quantidades por tamanho na parte superior
        painelInfoProduto.add(painelDetalhes, BorderLayout.CENTER); // Valor e descrição no centro

        add(painelInfoProduto, BorderLayout.CENTER); // Adiciona o painel de informações do produto ao centro da janela

        // Botão para salvar o produto
        botaoSalvar = new JButton("Salvar Produto");
        botaoSalvar.addActionListener(this); // Adiciona este objeto como ouvinte de ação
        JPanel painelSalvar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelSalvar.add(botaoSalvar); // Adiciona o botão ao painel
        add(painelSalvar, BorderLayout.SOUTH); // Adiciona o painel do botão à parte inferior da janela
    }

    // Método para tornar a janela visível
    public void mostrar() {
        setVisible(true);
    }

    // Método para obter uma conexão com o banco de dados MySQL
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Método para salvar as informações do produto no banco de dados
    private boolean salvarProdutoNoBanco(List<String> caminhosImagens, int quantidadeP, int quantidadeM, int quantidadeG, double valor, String descricao) {
        String sql = "INSERT INTO produtos (imagem1_path, imagem2_path, imagem3_path, quantidade_p, quantidade_m, quantidade_g, valor, descricao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); // Obtém a conexão com o banco
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara a instrução SQL
            pstmt.setString(1, caminhosImagens.size() > 0 ? caminhosImagens.get(0) : null); // Caminho da primeira imagem (se existir)
            pstmt.setString(2, caminhosImagens.size() > 1 ? caminhosImagens.get(1) : null); // Caminho da segunda imagem (se existir)
            pstmt.setString(3, caminhosImagens.size() > 2 ? caminhosImagens.get(2) : null); // Caminho da terceira imagem (se existir)
            pstmt.setInt(4, quantidadeP); // Quantidade do tamanho P
            pstmt.setInt(5, quantidadeM); // Quantidade do tamanho M
            pstmt.setInt(6, quantidadeG); // Quantidade do tamanho G
            pstmt.setDouble(7, valor); // Valor do produto
            pstmt.setString(8, descricao); // Descrição do produto
            int affectedRows = pstmt.executeUpdate(); // Executa a inserção e retorna o número de linhas afetadas
            return affectedRows > 0; // Retorna true se pelo menos uma linha foi inserida (sucesso)
        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
            return false; // Retorna false em caso de erro de SQL
        }
    }

    // Método chamado quando uma ação ocorre (clique em botão)
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        // Se o comando de ação começar com "imagem_", significa que um botão de imagem foi clicado
        if (command.startsWith("imagem_")) {
            int index = Integer.parseInt(command.split("_")[1]); // Extrai o índice do botão de imagem
            JFileChooser fileChooser = new JFileChooser(); // Cria um seletor de arquivos
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif"); // Filtra apenas arquivos de imagem
            fileChooser.setFileFilter(filter); // Aplica o filtro
            int returnVal = fileChooser.showOpenDialog(this); // Abre a janela de seleção de arquivos
            if (returnVal == JFileChooser.APPROVE_OPTION) { // Se o usuário clicou em "Abrir"
                File selectedFile = fileChooser.getSelectedFile(); // Obtém o arquivo selecionado
                String caminho = selectedFile.getAbsolutePath(); // Obtém o caminho absoluto do arquivo
                // Adiciona ou atualiza o caminho da imagem na lista
                if (index < caminhosImagens.size()) {
                    caminhosImagens.set(index, caminho);
                } else {
                    caminhosImagens.add(caminho);
                }
                // Cria um ícone redimensionado para a prévia
                ImageIcon icon = new ImageIcon(new ImageIcon(caminho).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                labelsImagem.get(index).setIcon(icon); // Define o ícone no label correspondente
                labelsImagem.get(index).setText(null); // Remove qualquer texto anterior no label
            }
        } else if (e.getSource() == botaoSalvar) { // Se o botão "Salvar Produto" foi clicado
            try {
                // Obtém e converte as quantidades por tamanho, tratando campos vazios como 0
                int quantidadeP = Integer.parseInt(campoQuantidadeP.getText().isEmpty() ? "0" : campoQuantidadeP.getText());
                int quantidadeM = Integer.parseInt(campoQuantidadeM.getText().isEmpty() ? "0" : campoQuantidadeM.getText());
                int quantidadeG = Integer.parseInt(campoQuantidadeG.getText().isEmpty() ? "0" : campoQuantidadeG.getText());
                double valor = Double.parseDouble(campoValor.getText()); // Obtém e converte o valor
                String descricao = campoDescricao.getText(); // Obtém a descrição
                // Salva o produto no banco de dados
                if (salvarProdutoNoBanco(caminhosImagens, quantidadeP, quantidadeM, quantidadeG, valor, descricao)) {
                    JOptionPane.showMessageDialog(this, "Produto adicionado com sucesso!");
                    // Limpa os campos após salvar
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

    // Método main para testar a TelaAdicionarProduto independentemente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaAdicionarProduto().mostrar());
    }
}