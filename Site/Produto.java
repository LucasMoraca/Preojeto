// Produto.java
package Site;

public class Produto {
    private int id;
    private double valor;
    private int quantidade_p;
    private int quantidade_m;
    private int quantidade_g;
    private String descricao;
    private String imagem1Path;
    private String imagem2Path;
    private String imagem3Path;

    // Construtor
    public Produto(int id, double valor, int quantidade_p, int quantidade_m, int quantidade_g, String descricao, String imagem1Path, String imagem2Path, String imagem3Path) {
        this.id = id;
        this.valor = valor;
        this.quantidade_p = quantidade_p;
        this.quantidade_m = quantidade_m;
        this.quantidade_g = quantidade_g;
        this.descricao = descricao;
        this.imagem1Path = imagem1Path;
        this.imagem2Path = imagem2Path;
        this.imagem3Path = imagem3Path;
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public int getQuantidade_p() {
        return quantidade_p;
    }

    public int getQuantidade_m() {
        return quantidade_m;
    }

    public int getQuantidade_g() {
        return quantidade_g;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getImagem1Path() {
        return imagem1Path;
    }

    public String getImagem2Path() {
        return imagem2Path;
    }

    public String getImagem3Path() {
        return imagem3Path;
    }

    // Setters (se precisar modificar os atributos depois da criação)
    public void setQuantidade_p(int quantidade_p) {
        this.quantidade_p = quantidade_p;
    }

    public void setQuantidade_m(int quantidade_m) {
        this.quantidade_m = quantidade_m;
    }

    public void setQuantidade_g(int quantidade_g) {
        this.quantidade_g = quantidade_g;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setImagem1Path(String imagem1Path) {
        this.imagem1Path = imagem1Path;
    }

    public void setImagem2Path(String imagem2Path) {
        this.imagem2Path = imagem2Path;
    }

    public void setImagem3Path(String imagem3Path) {
        this.imagem3Path = imagem3Path;
    }
}