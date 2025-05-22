package Site;

import java.util.ArrayList;
import java.util.List;

public class Carrinho {
    private static Carrinho instance;
    private List<ItemCarrinho> itens;

    private Carrinho() {
        itens = new ArrayList<>();
    }

    public static Carrinho getInstance() {
        if (instance == null) {
            instance = new Carrinho();
        }
        return instance;
    }

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

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void removerItem(int produtoId, String tamanho) {
        itens.removeIf(item -> item.getProdutoId() == produtoId && item.getTamanho().equals(tamanho));
    }

    public double getTotal() {
        double total = 0;
        for (ItemCarrinho item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void limparCarrinho() {
        itens.clear();
    }
}