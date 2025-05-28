package Site;

/**
 * {@code ItemCarrinho} representa um item adicionado ao carrinho de compras.
 * Contém informações sobre o produto, como seu ID, nome, tamanho selecionado,
 * quantidade e o valor unitário.
 */
public class ItemCarrinho {
    private int produtoId;
    private String nome;
    private String tamanho;
    private int quantidade;
    private double valorUnitario;

    /**
     * Construtor para criar uma instância de {@code ItemCarrinho}.
     *
     * @param produtoId     O ID do produto.
     * @param nome          O nome do produto.
     * @param tamanho       O tamanho selecionado do produto.
     * @param quantidade    A quantidade desejada do produto.
     * @param valorUnitario O valor unitário do produto.
     */
    public ItemCarrinho(int produtoId, String nome, String tamanho, int quantidade, double valorUnitario) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.tamanho = tamanho;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    /**
     * Obtém o ID do produto.
     *
     * @return O ID do produto.
     */
    public int getProdutoId() {
        return produtoId;
    }

    /**
     * Obtém o nome do produto.
     *
     * @return O nome do produto.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Obtém o tamanho selecionado do produto.
     *
     * @return O tamanho do produto.
     */
    public String getTamanho() {
        return tamanho;
    }

    /**
     * Obtém a quantidade do produto no carrinho.
     *
     * @return A quantidade do produto.
     */
    public int getQuantidade() {
        return quantidade;
    }

    /**
     * Obtém o valor unitário do produto.
     *
     * @return O valor unitário do produto.
     */
    public double getValorUnitario() {
        return valorUnitario;
    }

    /**
     * Calcula o subtotal do item (quantidade multiplicada pelo valor unitário).
     *
     * @return O subtotal do item no carrinho.
     */
    public double getSubtotal() {
        return quantidade * valorUnitario;
    }

    /**
     * Retorna uma representação em String do item do carrinho.
     * Formato: "nome (Tam: tamanho, Qtd: quantidade) - R$subtotal".
     *
     * @return Uma String representando o item do carrinho.
     */
    @Override
    public String toString() {
        return String.format("%s (Tam: %s, Qtd: %d) - R$%.2f", nome, tamanho, quantidade, getSubtotal());
    }

    /**
     * Define uma nova quantidade para o item no carrinho.
     *
     * @param novaQuantidade A nova quantidade do item.
     */
    public void setQuantidade(int novaQuantidade) {
        this.quantidade = novaQuantidade;
    }

    /**
     * Define um novo tamanho para o item no carrinho.
     *
     * @param novoTamanho O novo tamanho do item.
     */
    public void setTamanho(String novoTamanho) {
        this.tamanho = novoTamanho;
    }
}