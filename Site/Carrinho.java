package Site;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Carrinho} é uma classe Singleton que representa o carrinho de compras do usuário.
 * Ela gerencia uma lista de {@link ItemCarrinho} e oferece métodos para adicionar, remover itens,
 * obter a lista de itens, calcular o total e limpar o carrinho.
 */
public class Carrinho {
    private static Carrinho instance;
    private List<ItemCarrinho> itens;

    /**
     * Construtor privado para garantir que apenas uma instância da classe seja criada (Singleton).
     */
    private Carrinho() {
        itens = new ArrayList<>();
    }

    /**
     * Retorna a instância única da classe {@code Carrinho}. Se a instância ainda não existir, ela é criada.
     *
     * @return A instância única de {@code Carrinho}.
     */
    public static Carrinho getInstance() {
        if (instance == null) {
            instance = new Carrinho();
        }
        return instance;
    }

    /**
     * Adiciona um item ao carrinho de compras. Se um item com o mesmo ID de produto e tamanho já existir,
     * apenas a quantidade é aumentada. Caso contrário, um novo {@link ItemCarrinho} é criado e adicionado.
     *
     * @param produtoId     O ID do produto a ser adicionado.
     * @param nome          O nome do produto.
     * @param valorUnitario O valor unitário do produto.
     * @param tamanho       O tamanho do produto a ser adicionado.
     * @param quantidade    A quantidade a ser adicionada.
     */
    public void adicionarItem(int produtoId, String nome, double valorUnitario, String tamanho, int quantidade) {
        // Verifica se o item já existe no carrinho com o mesmo ID e tamanho
        for (ItemCarrinho item : itens) {
            if (item.getProdutoId() == produtoId && item.getTamanho().equals(tamanho)) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        itens.add(new ItemCarrinho(produtoId, nome, tamanho, quantidade, valorUnitario));
    }

    /**
     * Obtém a lista de itens atualmente no carrinho de compras.
     *
     * @return Uma {@code List} de {@link ItemCarrinho}.
     */
    public List<ItemCarrinho> getItens() {
        return itens;
    }

    /**
     * Remove um item do carrinho de compras com base no ID do produto e no tamanho.
     * Se houver múltiplos itens com o mesmo ID e tamanho, todos serão removidos.
     *
     * @param produtoId O ID do produto a ser removido.
     * @param tamanho   O tamanho do produto a ser removido.
     */
    public void removerItem(int produtoId, String tamanho) {
        itens.removeIf(item -> item.getProdutoId() == produtoId && item.getTamanho().equals(tamanho));
    }

    /**
     * Calcula o valor total dos itens no carrinho de compras.
     *
     * @return O valor total como um {@code double}.
     */
    public double getTotal() {
        double total = 0;
        for (ItemCarrinho item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    /**
     * Remove todos os itens do carrinho de compras, esvaziando-o.
     */
    public void limparCarrinho() {
        itens.clear();
    }
}