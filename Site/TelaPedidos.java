package Site;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A classe {@code TelaPedidos} exibe uma lista de todos os pedidos registrados no sistema.
 * Ela consulta o banco de dados para obter informações detalhadas sobre cada pedido
 * e as apresenta em uma tabela. Esta tela destina-se provavelmente a administradores.
 */
public class TelaPedidos extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Construtor da classe {@code TelaPedidos}.
     * Inicializa a interface gráfica para exibir a lista de pedidos.
     * Configura a janela, cria o modelo da tabela e a tabela em si,
     * define as colunas da tabela e carrega os dados dos pedidos do banco de dados.
     */
    public TelaPedidos() {
        setTitle("Pedidos");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cria um modelo de tabela padrão para armazenar os dados dos pedidos
        tableModel = new DefaultTableModel();
        // Cria a tabela utilizando o modelo criado
        pedidosTable = new JTable(tableModel);
        // Adiciona a tabela a um painel de rolagem para permitir a visualização de muitos pedidos
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        // Adiciona o painel de rolagem ao centro da janela
        add(scrollPane, BorderLayout.CENTER);

        // Adiciona as colunas à tabela
        tableModel.addColumn("ID Pedido");
        tableModel.addColumn("ID Usuário");
        tableModel.addColumn("Data Pedido");
        tableModel.addColumn("Status");
        tableModel.addColumn("Endereço Entrega");
        tableModel.addColumn("Forma Pagamento");
        tableModel.addColumn("Total");

        // Carrega os dados dos pedidos do banco de dados e preenche a tabela
        carregarPedidosDoBanco();

        setVisible(true);
    }

    /**
     * Carrega os dados de todos os pedidos do banco de dados e os adiciona à tabela.
     * Consulta a tabela 'pedidos' e extrai as informações relevantes para exibição.
     */
    private void carregarPedidosDoBanco() {
        String sql = "SELECT id, usuario_id, data_pedido, status, endereco_entrega, forma_pagamento, total FROM pedidos";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Itera sobre cada linha retornada pela consulta
            while (rs.next()) {
                // Extrai os valores de cada coluna para o pedido atual
                int id = rs.getInt("id");
                int usuarioId = rs.getInt("usuario_id");
                java.sql.Timestamp dataPedido = rs.getTimestamp("data_pedido");
                String status = rs.getString("status");
                String enderecoEntrega = rs.getString("endereco_entrega");
                String formaPagamento = rs.getString("forma_pagamento");
                double total = rs.getDouble("total");
                // Adiciona uma nova linha à tabela com os dados do pedido
                tableModel.addRow(new Object[]{id, usuarioId, dataPedido, status, enderecoEntrega, formaPagamento, total});
            }
        } catch (SQLException e) {
            // Exibe uma mensagem de erro se ocorrer algum problema ao acessar o banco de dados
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Método principal para criar e exibir a {@code TelaPedidos}.
     * Executa a criação da interface gráfica na thread de despacho de eventos (EDT).
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPedidos::new);
    }
}