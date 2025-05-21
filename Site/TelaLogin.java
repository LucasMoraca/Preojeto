// TelaLogin.java
package Site;

import javax.swing.*; // Importa classes para criar interfaces gráficas Swing
import java.awt.*; // Importa classes para layouts e componentes gráficos AWT
import java.awt.event.ActionEvent; // Importa a classe para eventos de ação (como cliques de botão)
import java.awt.event.ActionListener; // Importa a interface para lidar com eventos de ação
import java.sql.Connection; // Importa a interface para a conexão com o banco de dados
import java.sql.DriverManager; // Importa a classe para gerenciar drivers JDBC
import java.sql.PreparedStatement; // Importa a classe para instruções SQL pré-compiladas
import java.sql.ResultSet; // Importa a interface para o resultado de uma consulta SQL
import java.sql.SQLException; // Importa a classe para exceções relacionadas ao SQL

// A classe TelaLogin herda de JFrame (janela principal)
public class TelaLogin extends JFrame {
    // Declaração de componentes da interface gráfica
    private JTextField campoEmail; // Campo de texto para o email do usuário
    private JPasswordField campoSenha; // Campo de senha para a senha do usuário
    private JButton botaoEntrar; // Botão para iniciar o processo de login

    // Declaração de referências para outras telas
    private TelaCatalogo telaCatalogo; // Referência à tela de catálogo para usuários logados
    private TelaBazar telaBazar; // Referência à tela específica para bazares logados
    private TelaCadastro telaCadastro; // Referência à tela de cadastro

    // Configurações para o banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto"; // URL de conexão com o banco de dados MySQL
    private static final String DB_USER = "root"; // Nome de usuário do MySQL
    private static final String DB_PASSWORD = ""; // Senha do MySQL (vazia neste caso)

    // Construtor da TelaLogin
    public TelaLogin(TelaCatalogo telaCatalogo, TelaBazar telaBazar, TelaCadastro telaCadastro) {
        this.telaCatalogo = telaCatalogo; // Recebe a instância da TelaCatalogo da tela inicial
        this.telaBazar = telaBazar; // Recebe a instância da TelaBazar da tela inicial
        this.telaCadastro = telaCadastro; // Recebe a instância da TelaCadastro da tela inicial

        setTitle("Login"); // Define o título da janela
        setSize(350, 200); // Define o tamanho da janela (largura 350, altura 200 pixels)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define o comportamento ao fechar a janela (apenas fecha esta tela)
        setLocationRelativeTo(null); // Centraliza a janela na tela

        // Cria um painel com layout de grade para os campos de email e senha
        JPanel painelCampos = new JPanel(new GridLayout(2, 2, 10, 10)); // 2 linhas, 2 colunas, espaçamento horizontal e vertical de 10 pixels
        painelCampos.add(new JLabel("Email:", SwingConstants.RIGHT)); // Adiciona o rótulo "Email:" alinhado à direita
        campoEmail = new JTextField(); // Cria o campo de texto para o email
        painelCampos.add(campoEmail); // Adiciona o campo de texto ao painel

        painelCampos.add(new JLabel("Senha:", SwingConstants.RIGHT)); // Adiciona o rótulo "Senha:" alinhado à direita
        campoSenha = new JPasswordField(); // Cria o campo de senha (oculta os caracteres digitados)
        painelCampos.add(campoSenha); // Adiciona o campo de senha ao painel

        botaoEntrar = new JButton("Entrar"); // Cria o botão "Entrar"

        // Cria um painel com FlowLayout para centralizar o botão "Entrar"
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.add(botaoEntrar); // Adiciona o botão ao painel

        // Define o layout principal da janela como BorderLayout
        setLayout(new BorderLayout(10, 10)); // Layout com bordas e espaçamento de 10 pixels
        add(painelCampos, BorderLayout.CENTER); // Adiciona o painel de campos no centro
        add(painelBotao, BorderLayout.SOUTH); // Adiciona o painel do botão na parte inferior
        add(Box.createVerticalStrut(10), BorderLayout.NORTH); // Adiciona um espaço vertical de 10 pixels na parte superior
        add(Box.createHorizontalStrut(10), BorderLayout.WEST); // Adiciona um espaço horizontal de 10 pixels à esquerda
        add(Box.createHorizontalStrut(10), BorderLayout.EAST); // Adiciona um espaço horizontal de 10 pixels à direita

        // Adiciona um ActionListener ao botão "Entrar" para lidar com a lógica de login
        botaoEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = campoEmail.getText(); // Obtém o texto digitado no campo de email
                String senha = new String(campoSenha.getPassword()); // Obtém a senha digitada no campo de senha

                // Tenta autenticar o usuário na tabela 'usuarios' do banco de dados
                Integer usuarioId = autenticarUsuario(email, senha);
                if (usuarioId != null) {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Login de usuário bem-sucedido!");
                    // Informa à TelaCatalogo que um usuário está logado (pode ser usado para exibir funcionalidades específicas)
                    TelaCatalogo.setUsuarioLogado(true);
                    // Define o ID do usuário logado na classe TelaUsuario (se existir)
                    TelaUsuario.setUsuarioLogadoId(usuarioId);
                    SwingUtilities.invokeLater(() -> {
                        telaCatalogo.setLocationRelativeTo(TelaLogin.this); // Centraliza a tela de catálogo em relação à tela de login
                        telaCatalogo.mostrar(); // Torna a tela de catálogo visível
                        dispose(); // Fecha a tela de login atual
                    });
                    return; // Sai do método para não tentar autenticar como bazar se o login de usuário foi bem-sucedido
                }

                // Se a autenticação como usuário falhou, tenta autenticar na tabela 'bazares'
                if (autenticarBazar(email, senha)) {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Login de bazar bem-sucedido!");
                    SwingUtilities.invokeLater(() -> {
                        telaBazar.setLocationRelativeTo(TelaLogin.this); // Centraliza a tela de bazar em relação à tela de login
                        telaBazar.mostrar(); // Torna a tela de bazar visível
                        dispose(); // Fecha a tela de login atual
                    });
                    return; // Sai do método para não mostrar a mensagem de erro genérica se o login de bazar foi bem-sucedido
                }

                // Se nenhuma das autenticações for bem-sucedida, exibe uma mensagem de erro
                JOptionPane.showMessageDialog(TelaLogin.this, "Email ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(false); // A tela de login começa invisível e é mostrada explicitamente
    }

    // Método para obter uma conexão com o banco de dados MySQL
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Método para autenticar um usuário na tabela 'usuarios' e retornar o ID se a autenticação for bem-sucedida
    private Integer autenticarUsuario(String email, String senha) {
        String sql = "SELECT id FROM usuarios WHERE email = ? AND senha = ?";
        try (Connection conn = getConnection(); // Abre uma conexão com o banco de dados (será fechada automaticamente)
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara a instrução SQL
            pstmt.setString(1, email); // Define o valor do primeiro parâmetro (email)
            pstmt.setString(2, senha); // Define o valor do segundo parâmetro (senha)
            ResultSet rs = pstmt.executeQuery(); // Executa a consulta SQL e obtém o resultado

            if (rs.next()) {
                return rs.getInt("id"); // Se houver um resultado, retorna o ID do usuário
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário: " + e.getMessage());
        }
        return null; // Retorna null se a autenticação falhar
    }

    // Método para autenticar um bazar na tabela 'bazares'
    private boolean autenticarBazar(String email, String senha) {
        String sql = "SELECT * FROM bazares WHERE email = ? AND senha = ?";
        try (Connection conn = getConnection(); // Abre uma conexão com o banco de dados (será fechada automaticamente)
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara a instrução SQL
            pstmt.setString(1, email); // Define o valor do primeiro parâmetro (email)
            pstmt.setString(2, senha); // Define o valor do segundo parâmetro (senha)
            ResultSet rs = pstmt.executeQuery(); // Executa a consulta SQL e obtém o resultado

            return rs.next(); // Retorna true se houver alguma linha no resultado (o bazar foi encontrado)
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar bazar: " + e.getMessage());
            return false; // Retorna false em caso de erro
        }
    }

    // Método para tornar a tela de login visível
    public void mostrar() {
        setVisible(true);
    }

    // Getter para a tela de cadastro (permite que outras telas acessem a tela de cadastro)
    public TelaCadastro getTelaCadastro() {
        return telaCadastro;
    }

    // Método main removido daqui, pois a inicialização da interface começa na TelaInicial
}