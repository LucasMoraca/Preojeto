// TelaLogin.java
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

public class TelaLogin extends JFrame {
    private JTextField campoEmail; // Campo de texto para o email do usuário
    private JPasswordField campoSenha; // Campo de senha para a senha do usuário
    private JButton botaoEntrar; // Botão para iniciar o processo de login
    private TelaCatalogo telaCatalogo; // Referência à tela de catálogo para usuários
    private TelaBazar telaBazar; // Referência à tela específica para bazares

    // Configurações para o banco de dados MySQL
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projeto"; // URL de conexão com o banco de dados MySQL
    private static final String DB_USER = "root"; // Nome de usuário do MySQL
    private static final String DB_PASSWORD = ""; // Senha do MySQL

    // Construtor da TelaLogin
    public TelaLogin(TelaCatalogo telaCatalogo, TelaBazar telaBazar) {
        this.telaCatalogo = telaCatalogo; // Recebe a instância da TelaCatalogo
        this.telaBazar = telaBazar; // Recebe a instância da TelaBazar

        setTitle("Login"); // Define o título da janela
        setSize(350, 200); // Define o tamanho da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define o comportamento ao fechar a janela
        setLocationRelativeTo(null); // Centraliza a janela na tela

        // Cria um painel com layout de grade para os campos de email e senha
        JPanel painelCampos = new JPanel(new GridLayout(2, 2, 10, 10)); // 2 linhas, 2 colunas, espaçamento de 10 pixels
        painelCampos.add(new JLabel("Email:", SwingConstants.RIGHT)); // Rótulo "Email:" alinhado à direita
        campoEmail = new JTextField();
        painelCampos.add(campoEmail); // Adiciona o campo de texto para o email

        painelCampos.add(new JLabel("Senha:", SwingConstants.RIGHT)); // Rótulo "Senha:" alinhado à direita
        campoSenha = new JPasswordField();
        painelCampos.add(campoSenha); // Adiciona o campo de senha

        botaoEntrar = new JButton("Entrar"); // Cria o botão "Entrar"

        // Cria um painel com FlowLayout para centralizar o botão "Entrar"
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.add(botaoEntrar); // Adiciona o botão ao painel

        // Define o layout principal da janela como BorderLayout
        setLayout(new BorderLayout(10, 10));
        add(painelCampos, BorderLayout.CENTER); // Adiciona o painel de campos ao centro
        add(painelBotao, BorderLayout.SOUTH); // Adiciona o painel do botão ao sul
        add(Box.createVerticalStrut(10), BorderLayout.NORTH); // Adiciona um espaço vertical no topo
        add(Box.createHorizontalStrut(10), BorderLayout.WEST); // Adiciona um espaço horizontal à esquerda
        add(Box.createHorizontalStrut(10), BorderLayout.EAST); // Adiciona um espaço horizontal à direita

        // Adiciona um ActionListener ao botão "Entrar" para lidar com a lógica de login
        botaoEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = campoEmail.getText(); // Obtém o email digitado
                String senha = new String(campoSenha.getPassword()); // Obtém a senha digitada

                // Tenta autenticar o usuário na tabela 'usuarios'
                if (autenticarUsuario(email, senha)) {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Login de usuário bem-sucedido!");
                    SwingUtilities.invokeLater(() -> {
                        telaCatalogo.setLocationRelativeTo(TelaLogin.this); // Centraliza a tela de catálogo em relação à tela de login
                        telaCatalogo.mostrar(); // Torna a tela de catálogo visível
                        dispose(); // Fecha a tela de login
                    });
                    return; // Sai do método para não tentar autenticar como bazar se já logou como usuário
                }

                // Se a autenticação como usuário falhou, tenta autenticar na tabela 'bazares'
                if (autenticarBazar(email, senha)) {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Login de bazar bem-sucedido!");
                    SwingUtilities.invokeLater(() -> {
                        telaBazar.setLocationRelativeTo(TelaLogin.this); // Centraliza a tela de bazar em relação à tela de login
                        telaBazar.mostrar(); // Torna a tela de bazar visível
                        dispose(); // Fecha a tela de login
                    });
                    return; // Sai do método para não mostrar a mensagem de erro genérica
                }

                // Se nenhuma das autenticações for bem-sucedida, exibe uma mensagem de erro
                JOptionPane.showMessageDialog(TelaLogin.this, "Email ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(false); // A tela de login começa invisível e é mostrada pelo método mostrar()
    }

    // Método para obter uma conexão com o banco de dados MySQL
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Método para autenticar um usuário na tabela 'usuarios'
    private boolean autenticarUsuario(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        try (Connection conn = getConnection(); // Obtém a conexão com o banco
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara a consulta SQL
            pstmt.setString(1, email); // Define o valor do primeiro parâmetro da consulta (email)
            pstmt.setString(2, senha); // Define o valor do segundo parâmetro da consulta (senha)
            ResultSet rs = pstmt.executeQuery(); // Executa a consulta e obtém o resultado
            return rs.next(); // Retorna true se houver alguma linha no resultado (ou seja, o usuário existe)
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário: " + e.getMessage());
            return false; // Retorna false em caso de erro
        }
    }

    // Método para autenticar um bazar na tabela 'bazares'
    private boolean autenticarBazar(String email, String senha) {
        String sql = "SELECT * FROM bazares WHERE email = ? AND senha = ?";
        try (Connection conn = getConnection(); // Obtém a conexão com o banco
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara a consulta SQL
            pstmt.setString(1, email); // Define o valor do primeiro parâmetro da consulta (email)
            pstmt.setString(2, senha); // Define o valor do segundo parâmetro da consulta (senha)
            ResultSet rs = pstmt.executeQuery(); // Executa a consulta e obtém o resultado
            return rs.next(); // Retorna true se houver alguma linha no resultado (ou seja, o bazar existe)
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar bazar: " + e.getMessage());
            return false; // Retorna false em caso de erro
        }
    }

    // Método para tornar a tela de login visível
    public void mostrar() {
        setVisible(true);
    }

    // Método main removido daqui, pois a inicialização agora é feita na TelaInicial
}