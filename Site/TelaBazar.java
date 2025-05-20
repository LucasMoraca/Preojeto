// TelaBazar.java
package Site;

import javax.swing.*; // Importa classes para interfaces gráficas Swing
import java.awt.*; // Importa classes de layout e componentes AWT
import java.awt.event.ActionEvent; // Importa a classe para eventos de ação (como cliques em botões)
import java.awt.event.ActionListener; // Importa a interface para lidar com eventos de ação

// A classe TelaBazar estende JFrame, o que significa que ela é uma janela,
// e implementa ActionListener, o que permite que ela reaja a eventos de ação.
public class TelaBazar extends JFrame implements ActionListener {

    // Declaração dos botões como atributos da classe
    private JButton adicionarProdutoButton;
    private JButton editarProdutoButton;
    private JButton eliminarProdutoButton;

    // Construtor da classe TelaBazar
    public TelaBazar() {
        // Define o título da janela
        setTitle("Página do Bazar");
        // Define o tamanho da janela (largura 400 pixels, altura 300 pixels)
        setSize(400, 300);
        // Define o comportamento padrão ao fechar a janela (apenas fecha esta janela)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Centraliza a janela na tela
        setLocationRelativeTo(null);
        // Define o layout da janela como FlowLayout, que organiza os componentes em fluxo
        // (centralizado, com espaçamento horizontal e vertical de 20 pixels)
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // Cria os botões com seus respectivos textos
        adicionarProdutoButton = new JButton("Adicionar Produto");
        editarProdutoButton = new JButton("Editar Produto");
        eliminarProdutoButton = new JButton("Eliminar Produto");

        // Adiciona a própria instância de TelaBazar como ouvinte de ação para cada botão.
        // Isso significa que o método actionPerformed desta classe será chamado quando
        // um desses botões for clicado.
        adicionarProdutoButton.addActionListener(this);
        editarProdutoButton.addActionListener(this);
        eliminarProdutoButton.addActionListener(this);

        // Adiciona os botões ao painel de conteúdo da janela, que está usando FlowLayout.
        add(adicionarProdutoButton);
        add(editarProdutoButton);
        add(eliminarProdutoButton);

        // Define a janela como inicialmente invisível. Ela será mostrada explicitamente
        // pela TelaLogin após um login de bazar bem-sucedido.
        setVisible(false);
    }

    // Método para tornar a janela visível
    public void mostrar() {
        setVisible(true);
    }

    // Este método é parte da interface ActionListener. Ele é chamado automaticamente
    // quando um evento de ação ocorre em um componente para o qual esta classe
    // está registrada como ouvinte (neste caso, os três botões).
    @Override
    public void actionPerformed(ActionEvent e) {
        // Verifica qual botão gerou o evento (foi clicado)
        if (e.getSource() == adicionarProdutoButton) {
            // Exibe uma mensagem informando que a funcionalidade ainda não foi implementada.
            // No futuro, aqui seria aberto o painel ou janela para adicionar um produto.
            JOptionPane.showMessageDialog(this, "Funcionalidade de Adicionar Produto (a implementar)");
        } else if (e.getSource() == editarProdutoButton) {
            // Similar ao anterior, para a funcionalidade de editar produto.
            JOptionPane.showMessageDialog(this, "Funcionalidade de Editar Produto (a implementar)");
        } else if (e.getSource() == eliminarProdutoButton) {
            // Similar aos anteriores, para a funcionalidade de eliminar produto.
            JOptionPane.showMessageDialog(this, "Funcionalidade de Eliminar Produto (a implementar)");
        }
    }

    // Método main para testar a TelaBazar independentemente.
    public static void main(String[] args) {
        // Executa a criação e exibição da TelaBazar na thread de despacho de eventos (EDT)
        // para garantir a segurança da thread da GUI.
        SwingUtilities.invokeLater(() -> new TelaBazar().mostrar());
    }
}