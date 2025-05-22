package Site;

public class SessaoUsuario {
    private static SessaoUsuario instance;
    private Integer usuarioId;
    private String nomeUsuario;
    private String tipoUsuario; // 'bazar' ou 'cliente'

    private SessaoUsuario() {}

    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public boolean isUsuarioLogado() {
        return usuarioId != null;
    }

    public void iniciarSessao(int id, String nome, String tipo) {
        this.usuarioId = id;
        this.nomeUsuario = nome;
        this.tipoUsuario = tipo;
    }

    public void encerrarSessao() {
        this.usuarioId = null;
        this.nomeUsuario = null;
        this.tipoUsuario = null;
    }
}