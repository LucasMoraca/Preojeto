// TelaEditarProduto.java
package Site;

import java.util.List; // Importa a interface List para coleções ordenadas
import java.util.ArrayList; // Importa a classe ArrayList para implementar listas dinâmicas
import javax.swing.*; // Importa classes para criar interfaces gráficas Swing
import java.awt.*; // Importa classes para layouts e componentes gráficos AWT
import java.awt.event.ActionEvent; // Importa a classe para eventos de ação (como cliques de botão)
import java.awt.event.ActionListener; // Importa a interface para lidar com eventos de ação
import java.sql.Connection; // Importa a interface para a conexão com o banco de dados
import java.sql.DriverManager; // Importa a classe para gerenciar drivers JDBC
import java.sql.PreparedStatement; // Importa a classe para instruções SQL pré-compiladas
import java.sql.ResultSet; // Importa a interface para o resultado de uma consulta SQL
import java.sql.SQLException; // Importa a classe para exceções relacionadas ao SQL
import javax.swing.table.DefaultTableModel; // Importa a classe para o modelo padrão de tabelas Swing
import java.awt.image.BufferedImage; // Importa a classe para imagens buffered
import javax.imageio.ImageIO; // Importa classes para operações de leitura/escrita de imagens
import java.io.File; // Importa a classe para manipulação de arquivos
import java.io.IOException; // Importa a classe para exceções de I/O
import javax.swing.filechooser.FileNameExtensionFilter; // Importa a classe para filtros de extensão de arquivos
import javax.swing.event.ListSelectionEvent; // Importa a classe para eventos de seleção em listas
import javax.swing.event.ListSelectionListener; // Importa a interface para lidar com eventos de seleção em listas
// import java.sql.ResultSet; // Importa novamente (redundante, já importado)

// A classe TelaEditarProduto herda de JFrame (janela principal) e implementa ActionListener (para eventos de botões) e ListSelectionListener (para seleção de linhas na tabela)
public class TelaEditarProduto extends JFrame implements ActionListener, ListSelectionListener {

    private JTable tabelaProdutos; // Tabela para exibir a lista de produtos
    private DefaultTableModel modeloTabela; // Modelo da tabela para gerenciar os dados
    private JButton botaoSalvarEdicao; // Botão para salvar as alterações feitas em um produto
    private JButton botaoEliminarProduto; // Botão para eliminar (deletar) um produto

    // Painel para a seção de edição dos detalhes do produto
    private JPanel painelEdicao;

    // Componentes para edição dos detalhes do produto
    private List<JButton> botoesImagem; // Lista de botões para selecionar imagens
    private List<JLabel> labelsImagem; // Lista de labels para exibir as miniaturas das imagens
    private JTextField campoQuantidadeP; // Campo de texto para a quantidade do tamanho P
    private JTextField campoQuantidadeM; // Campo de texto para a quantidade do tamanho M
    private JTextField campoQuantidadeG; // Campo de texto para a quantidade do tamanho G
    private JTextField campoValor; // Campo de texto para o valor do produto
    private JTextArea campoDescricao; // Área de texto para a descrição do produto
    private JScrollPane scrollDescricao; // Painel de rolagem para a área de texto da descrição

    private static final int MAX_IMAGENS = 3; // Constante para o número máximo de imagens por produto
    private List<String> caminhosImagens; // Lista para armazenar os caminhos das imagens selecionadas para edição
    private int produtoIdSelecionado = -1; // Variável para rastrear o ID do produto atualmente selecionado na tabela
    private List<String> imagensAtuaisBanco; // Lista para armazenar os caminhos das imagens atualmente no banco para o produto selecionado

    // Configurações do banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Construtor da TelaEditarProduto
    public TelaEditarProduto() {
        setTitle("Editar Produto"); // Define o título da janela
        setSize(1100, 850); // Define o tamanho inicial da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define o comportamento ao fechar a janela (apenas fecha esta tela)
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Abre a janela maximizada
        setLayout(new BorderLayout(10, 10)); // Define o layout principal como BorderLayout com espaçamento

        // Inicialização das listas para caminhos de imagens
        caminhosImagens = new ArrayList<>();
        imagensAtuaisBanco = new ArrayList<>();
        for (int i = 0; i < MAX_IMAGENS; i++) {
            caminhosImagens.add(null); // Inicializa com null
            imagensAtuaisBanco.add(null); // Inicializa com null
        }
        botoesImagem = new ArrayList<>(); // Inicializa a lista de botões de imagem
        labelsImagem = new ArrayList<>(); // Inicializa a lista de labels de imagem

        // Cria o modelo da tabela com as colunas
        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Valor", "Qtd. P", "Qtd. M", "Qtd. G", "Descrição", "Imagem 1", "Imagem 2", "Imagem 3"}, 0);
        tabelaProdutos = new JTable(modeloTabela); // Cria a tabela com o modelo
        tabelaProdutos.getSelectionModel().addListSelectionListener(this); // Adiciona um listener para detectar a seleção de linhas na tabela
        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos); // Adiciona barra de rolagem à tabela
        add(scrollTabela, BorderLayout.NORTH); // Adiciona a tabela na parte superior da janela

        // Cria o painel para os campos de edição
        painelEdicao = new JPanel();
        painelEdicao.setLayout(new GridBagLayout()); // Usa GridBagLayout para um layout mais flexível
        add(painelEdicao, BorderLayout.CENTER); // Adiciona o painel de edição no centro da janela
        inicializarCamposEdicao(); // Chama o método para criar e adicionar os campos de edição

        // Cria e configura o botão para salvar as edições
        botaoSalvarEdicao = new JButton("Salvar Alterações");
        botaoSalvarEdicao.addActionListener(this); // Adiciona um listener para o evento de clique
        GridBagConstraints gbcBotaoSalvar = new GridBagConstraints();
        gbcBotaoSalvar.gridx = 0;
        gbcBotaoSalvar.gridy = 12; // Posiciona o botão na linha 12
        gbcBotaoSalvar.gridwidth = 2; // Ocupa duas colunas
        gbcBotaoSalvar.insets = new Insets(10, 10, 10, 5); // Define as margens
        gbcBotaoSalvar.fill = GridBagConstraints.HORIZONTAL; // Faz o botão se expandir horizontalmente
        painelEdicao.add(botaoSalvarEdicao, gbcBotaoSalvar); // Adiciona o botão ao painel de edição

        // Cria e configura o botão para eliminar o produto
        botaoEliminarProduto = new JButton("Eliminar Produto");
        botaoEliminarProduto.addActionListener(this); // Adiciona um listener para o evento de clique
        GridBagConstraints gbcBotaoEliminar = new GridBagConstraints();
        gbcBotaoEliminar.gridx = 2;
        gbcBotaoEliminar.gridy = 12; // Posiciona o botão na linha 12
        gbcBotaoEliminar.gridwidth = 1;
        gbcBotaoEliminar.insets = new Insets(10, 5, 10, 10);
        gbcBotaoEliminar.fill = GridBagConstraints.HORIZONTAL;
        painelEdicao.add(botaoEliminarProduto, gbcBotaoEliminar); // Adiciona o botão ao painel de edição

        carregarProdutos(); // Chama o método para carregar os produtos do banco de dados e exibir na tabela
    }

    /**
     * Inicializa e adiciona os campos de edição ao painel de edição usando GridBagLayout.
     */
    private void inicializarCamposEdicao() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Define as margens padrão
        gbc.fill = GridBagConstraints.HORIZONTAL; // Faz os componentes se expandirem horizontalmente
        gbc.gridx = 0; // Coluna inicial
        gbc.gridy = 0; // Linha inicial
        gbc.gridwidth = 1; // Largura padrão de 1 célula

        // Labels e botões para as imagens
        for (int i = 0; i < MAX_IMAGENS; i++) {
            JLabel label = new JLabel("Imagem " + (i + 1) + ":");
            painelEdicao.add(label, gbc); // Adiciona o label
            gbc.gridx++; // Move para a próxima coluna

            labelsImagem.add(new JLabel()); // Cria um label para exibir a imagem
            painelEdicao.add(labelsImagem.get(i), gbc); // Adiciona o label da imagem
            gbc.gridx++; // Move para a próxima coluna

            JButton botaoImagem = new JButton("Selecionar");
            botaoImagem.setActionCommand("imagem_" + i); // Define um comando de ação para identificar qual botão foi clicado
            botaoImagem.addActionListener(this); // Adiciona um listener para o evento de clique
            botoesImagem.add(botaoImagem); // Adiciona o botão à lista
            painelEdicao.add(botaoImagem, gbc); // Adiciona o botão
            gbc.gridx = 0; // Volta para a primeira coluna na próxima linha
            gbc.gridy++; // Move para a próxima linha
        }

        // Campo para Quantidade P
        painelEdicao.add(new JLabel("Quantidade P:"), gbc);
        gbc.gridx++;
        campoQuantidadeP = new JTextField(10); // Cria um campo de texto com 10 colunas
        gbc.gridwidth = 2; // O campo ocupa duas colunas
        painelEdicao.add(campoQuantidadeP, gbc);
        gbc.gridwidth = 1; // Reseta a largura para o próximo componente
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

        // Campo para Descrição
        painelEdicao.add(new JLabel("Descrição:"), gbc);
        gbc.gridx++;
        campoDescricao = new JTextArea(10, 60); // Cria uma área de texto com 10 linhas e 60 colunas
        scrollDescricao = new JScrollPane(campoDescricao); // Adiciona barra de rolagem se o texto for muito longo
        gbc.gridwidth = 2;
        painelEdicao.add(scrollDescricao, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;
    }

    // Carrega os produtos do banco de dados e os exibe na tabela
    private void carregarProdutos() {
        modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar novos dados
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

    // Carrega os dados de um produto específico para os campos de edição
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

                // Carregar caminhos das imagens do banco de dados para a lista
                caminhosImagens.set(0, rs.getString("imagem1_path"));
                caminhosImagens.set(1, rs.getString("imagem2_path"));
                caminhosImagens.set(2, rs.getString("imagem3_path"));

                atualizarLabelsImagens(caminhosImagens); // Atualiza a exibição das imagens
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Atualiza os labels de imagem com as miniaturas das imagens dos caminhos fornecidos
    private void atualizarLabelsImagens(List<String> caminhos) {
        for (int i = 0; i < MAX_IMAGENS; i++) {
            labelsImagem.get(i).setIcon(null); // Limpa o ícone anterior
            if (caminhos.get(i) != null && !caminhos.get(i).isEmpty()) {
                try {
                    BufferedImage img = ImageIO.read(new File(caminhos.get(i)));
                    if (img != null) {
                        Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        labelsImagem.get(i).setIcon(new ImageIcon(scaledImg));
                        labelsImagem.get(i).setText(null); // Remove qualquer texto anterior
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

    // Abre um diálogo para o usuário selecionar uma imagem
    private void selecionarImagem(int index) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            caminhosImagens.set(index, selectedFile.getAbsolutePath()); // Salva o caminho da imagem selecionada
            atualizarLabelsImagens(caminhosImagens); // Atualiza a label da imagem
        }
    }

    // Exclui o produto selecionado do banco de dados
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
                carregarProdutos(); // Recarrega a tabela após a eliminação
                // Limpar os campos de edição após a eliminação
                campoValor.setText("");
                campoQuantidadeP.setText("");
                campoQuantidadeM.setText("");
                campoQuantidadeG.setText("");
                campoDescricao.setText("");
                produtoIdSelecionado = -1;
                // Limpar caminhos de imagem e labels
                for (int i = 0; i < MAX_IMAGENS; i++) {
                    caminhosImagens.set(i, null);
                    labelsImagem.get(i).setIcon(null);
                    labelsImagem.get(i).setText("Sem imagem");
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

// Salva as edições feitas no produto no banco de dados
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

// Lida com os eventos de ação (cliques de botão)
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

// Lida com os eventos de mudança de seleção na tabela de produtos
@Override
public void valueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {
            produtoIdSelecionado = (int) modeloTabela.getValueAt(selectedRow, 0);
            carregarDadosProduto(produtoIdSelecionado); // Carrega os dados do produto selecionado para os campos de edição
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

// Método main para executar a TelaEditarProduto individualmente
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TelaEditarProduto());
}
}