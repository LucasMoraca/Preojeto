// SessaoUsuario.java
package Site;

/**
 * {@code SessaoUsuario} é uma classe Singleton responsável por manter informações sobre a sessão do usuário logado
 * na aplicação. Ela armazena o ID do usuário, nome, tipo ('bazar' ou 'cliente') e email.
 */
public class SessaoUsuario {
    private static SessaoUsuario instance;
    private Integer usuarioId;
    private String nomeUsuario;
    private String tipoUsuario; // 'bazar' ou 'cliente'
    private String emailUsuario; // Adicionando o campo para o email

    /**
     * Construtor privado para garantir que apenas uma instância da classe seja criada (Singleton).
     */
    private SessaoUsuario() {}

    /**
     * Retorna a instância única da classe {@code SessaoUsuario}. Se a instância ainda não existir, ela é criada.
     *
     * @return A instância única de {@code SessaoUsuario}.
     */
    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    /**
     * Obtém o ID do usuário logado.
     *
     * @return O ID do usuário, ou {@code null} se nenhum usuário estiver logado.
     */
    public Integer getUsuarioId() {
        return usuarioId;
    }

    /**
     * Obtém o nome do usuário logado.
     *
     * @return O nome do usuário, ou {@code null} se nenhum usuário estiver logado.
     */
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    /**
     * Obtém o tipo do usuário logado ('bazar' ou 'cliente').
     *
     * @return O tipo do usuário, ou {@code null} se nenhum usuário estiver logado.
     */
    public String getTipoUsuario() {
        return tipoUsuario;
    }

    /**
     * Obtém o email do usuário logado.
     *
     * @return O email do usuário, ou {@code null} se nenhum usuário estiver logado.
     */
    public String getEmailUsuario() {
        return emailUsuario;
    }

    /**
     * Verifica se um usuário está logado no sistema.
     *
     * @return {@code true} se um usuário estiver logado ({@code usuarioId} não é {@code null}), {@code false} caso contrário.
     */
    public boolean isUsuarioLogado() {
        return usuarioId != null;
    }

    /**
     * Inicia a sessão do usuário, armazenando seu ID, nome, tipo e email.
     *
     * @param id    O ID do usuário.
     * @param nome  O nome do usuário.
     * @param tipo  O tipo do usuário ('bazar' ou 'cliente').
     * @param email O email do usuário.
     */
    public void iniciarSessao(int id, String nome, String tipo, String email) {
        this.usuarioId = id;
        this.nomeUsuario = nome;
        this.tipoUsuario = tipo;
        this.emailUsuario = email; // Inicializando o email
    }

    /**
     * Encerra a sessão do usuário, removendo todas as informações da sessão.
     */
    public void encerrarSessao() {
        this.usuarioId = null;
        this.nomeUsuario = null;
        this.tipoUsuario = null;
        this.emailUsuario = null; // Limpando o email ao encerrar a sessão
    }
}