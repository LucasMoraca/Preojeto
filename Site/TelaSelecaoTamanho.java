package Site;

import javax.swing.JFrame;

public class TelaSelecaoTamanho extends JFrame {

    public TelaSelecaoTamanho(int produtoId, String nomeProduto, double valorProduto) {
        setTitle("Selecionar Tamanho");
        setSize(300, 200);
        setLocationRelativeTo(null);
        // Aqui você adicionará os componentes para selecionar o tamanho
        setVisible(true); // Por enquanto, apenas torna a janela visível
    }

    public static void main(String[] args) {
        // Para teste rápido
        new TelaSelecaoTamanho(1, "Produto Teste", 25.99);
    }
}