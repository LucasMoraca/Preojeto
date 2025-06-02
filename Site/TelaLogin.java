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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A classe {@code TelaLogin} representa a tela de login do sistema Bazar Online.
 * Permite que usuários existentes façam login com seu email e senha,
 * e oferece uma opção para novos usuários se cadastrarem.
 * A autenticação é feita consultando um banco de dados de usuários.
 * Implementa bloqueio de conta por 15 minutos após 3 tentativas falhas consecutivas para o mesmo email.
 */
public class TelaLogin extends JFrame {

    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton cadastrarButton;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Constantes para controle de tentativas de login
    private static final int MAX_TENTATIVAS_FALHAS = 3;
    private static final long TEMPO_BLOQUEIO_MINUTOS = 15;

    // Mapas para rastrear tentativas falhas e tempo de bloqueio por email
    private Map<String, Integer> tentativasFalhasMap = new HashMap<>();
    private Map<String, Long> tempoBloqueioMap = new HashMap<>();

    /**
     * Construtor da classe {@code TelaLogin}.
     * Inicializa a interface gráfica da tela de login, incluindo campos para email e senha,
     * e botões para login e cadastro.
     */
    public TelaLogin() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250); // Ajustado para melhor visualização da mensagem de bloqueio
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        add(senhaField);

        add(new JLabel("")); // Espaço em branco

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim(); // Remover espaços em branco
                String senha = new String(senhaField.getPassword());

                if (email.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Email e senha não podem estar vazios.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Verificar se o email está bloqueado
                if (tempoBloqueioMap.containsKey(email)) {
                    long tempoBloqueadoAte = tempoBloqueioMap.get(email);
                    long tempoAtual = System.currentTimeMillis();

                    if (tempoAtual < tempoBloqueadoAte) {
                        long tempoRestanteMs = tempoBloqueadoAte - tempoAtual;
                        long minutosRestantes = TimeUnit.MILLISECONDS.toMinutes(tempoRestanteMs);
                        long segundosRestantes = TimeUnit.MILLISECONDS.toSeconds(tempoRestanteMs) % 60;
                        JOptionPane.showMessageDialog(TelaLogin.this,
                                String.format("Conta bloqueada para o email '%s'. Tente novamente em %d minutos e %d segundos.", email, minutosRestantes, segundosRestantes),
                                "Conta Bloqueada", JOptionPane.WARNING_MESSAGE);
                        return;
                    } else {
                        // Tempo de bloqueio expirou, remover o email dos mapas
                        tempoBloqueioMap.remove(email);
                        tentativasFalhasMap.remove(email);
                    }
                }

                String tipoUsuario = autenticarUsuario(email, senha);

                if (tipoUsuario != null) {
                    JOptionPane.showMessageDialog(TelaLogin.this, "Login realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    // Resetar tentativas falhas para este email em caso de sucesso
                    tentativasFalhasMap.remove(email);
                    tempoBloqueioMap.remove(email); // Garante que qualquer bloqueio anterior seja removido

                    TelaLogin.this.dispose();
                    if (tipoUsuario.equals("bazar")) {
                        TelaBazar telaBazar = new TelaBazar();
                        telaBazar.setVisible(true);
                    } else if (tipoUsuario.equals("cliente")) {
                        TelaCatalogo telaCatalogo = new TelaCatalogo();
                        telaCatalogo.setVisible(true);
                    }
                } else {
                    // Incrementar tentativas falhas para este email
                    tentativasFalhasMap.put(email, tentativasFalhasMap.getOrDefault(email, 0) + 1);

                    if (tentativasFalhasMap.get(email) >= MAX_TENTATIVAS_FALHAS) {
                        // Bloquear a conta
                        long tempoBloqueioAte = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(TEMPO_BLOQUEIO_MINUTOS);
                        tempoBloqueioMap.put(email, tempoBloqueioAte);
                        JOptionPane.showMessageDialog(TelaLogin.this,
                                String.format("Email ou senha incorretos. A conta para o email '%s' foi bloqueada por %d minutos devido a múltiplas tentativas falhas.", email, TEMPO_BLOQUEIO_MINUTOS),
                                "Conta Bloqueada", JOptionPane.ERROR_MESSAGE);
                    } else {
                        int tentativasRestantes = MAX_TENTATIVAS_FALHAS - tentativasFalhasMap.get(email);
                        JOptionPane.showMessageDialog(TelaLogin.this,
                                String.format("Email ou senha incorretos. Tentativas restantes: %d", tentativasRestantes),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        add(loginButton);

        add(new JLabel("")); // Espaço em branco

        cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaCadastro telaCadastro = new TelaCadastro();
                telaCadastro.setVisible(true);
            }
        });
        add(cadastrarButton);

        setVisible(true);
    }

    /**
     * Autentica um usuário no banco de dados com base no email e senha fornecidos.
     *
     * @param email O email do usuário.
     * @param senha A senha do usuário.
     * @return O tipo de usuário ("bazar" ou "cliente") se a autenticação for bem-sucedida, caso contrário, retorna {@code null}.
     */
    private String autenticarUsuario(String email, String senha) {
        String tipo = null;
        String sql = "SELECT id, nome, senha, tipo, email FROM usuarios WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String senhaBanco = rs.getString("senha");
                if (senha.equals(senhaBanco)) {
                    tipo = rs.getString("tipo");
                    // Iniciar sessão aqui se necessário (SessaoUsuario)
                    if (tipo.equals("cliente")) {
                        int userId = rs.getInt("id");
                        String nomeUsuario = rs.getString("nome");
                        String emailUsuario = rs.getString("email");
                        SessaoUsuario.getInstance().iniciarSessao(userId, nomeUsuario, tipo, emailUsuario);
                    } else { // bazar ou outros tipos
                        SessaoUsuario.getInstance().iniciarSessao(rs.getInt("id"), rs.getString("nome"), tipo, rs.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao autenticar: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return tipo;
    }

    /**
     * Método principal para criar e exibir a {@code TelaLogin}.
     * Executa a criação da interface gráfica na thread de despacho de eventos (EDT).
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Exemplo de como outras classes poderiam ser chamadas (necessário criá-las)
        // class TelaBazar extends JFrame { public TelaBazar() { setTitle("Tela Bazar"); setSize(400,300); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); } }
        // class TelaCatalogo extends JFrame { public TelaCatalogo() { setTitle("Tela Catalogo"); setSize(400,300); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); } }
        // class TelaCadastro extends JFrame { public TelaCadastro() { setTitle("Tela Cadastro"); setSize(400,300); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); } }
        // class SessaoUsuario {
        //     private static SessaoUsuario instance;
        //     private int userId;
        //     private String nomeUsuario;
        //     private String tipoUsuario;
        //     private String emailUsuario;
        //     private SessaoUsuario() {}
        //     public static synchronized SessaoUsuario getInstance() {
        //         if (instance == null) instance = new SessaoUsuario();
        //         return instance;
        //     }
        //     public void iniciarSessao(int userId, String nome, String tipo, String email) {
        //         this.userId = userId; this.nomeUsuario = nome; this.tipoUsuario = tipo; this.emailUsuario = email;
        //         System.out.println("Sessão iniciada para: " + nome + " (" + email + ") - Tipo: " + tipo);
        //     }
        // }

        SwingUtilities.invokeLater(() -> new TelaLogin());
    }
}