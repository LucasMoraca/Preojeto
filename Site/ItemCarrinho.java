package Site;

public class ItemCarrinho {
    private Produto produto;
    private int quantidade;
    private String tamanho;

    public ItemCarrinho(Produto produto, int quantidade, String tamanho) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.tamanho = tamanho;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return produto.getValor() * quantidade;
    }

    // Poderia adicionar método para atualizar quantidade (incrementar/decrementar)
    public void incrementarQuantidade(int valor) {
        if (valor < 0) throw new IllegalArgumentException("Valor deve ser positivo");
        this.quantidade += valor;
    }

    public void decrementarQuantidade(int valor) {
        if (valor < 0) throw new IllegalArgumentException("Valor deve ser positivo");
        if (this.quantidade - valor < 0) {
            throw new IllegalArgumentException("Quantidade não pode ficar negativa");
        }
        this.quantidade -= valor;
    }

    @Override
    public String toString() {
        return produto.getDescricao() + " (Tam: " + tamanho + ", Qtd: " + quantidade + ") - R$" + String.format("%.2f", getSubtotal());
    }
}
