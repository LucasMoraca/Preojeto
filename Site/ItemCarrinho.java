// ItemCarrinho.java
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
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return produto.getValor() * quantidade;
    }

    @Override
    public String toString() {
        return produto.getDescricao() + " (Tam: " + tamanho + ", Qtd: " + quantidade + ") - R$" + String.format("%.2f", getSubtotal());
    }
}
