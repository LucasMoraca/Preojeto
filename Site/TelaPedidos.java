package Site;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaPedidos extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaPedidos() {
        setTitle("Pedidos");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        pedidosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        add(scrollPane, BorderLayout.CENTER);

        // Adicionar as colunas da tabela
        tableModel.addColumn("ID Pedido");
        tableModel.addColumn("ID Usuário");
        tableModel.addColumn("Data Pedido");
        tableModel.addColumn("Status");
        tableModel.addColumn("Endereço Entrega");
        tableModel.addColumn("Forma Pagamento");
        tableModel.addColumn("Total");

        carregarPedidosDoBanco();

        setVisible(true);
    }

    private void carregarPedidosDoBanco() {
        String sql = "SELECT id, usuario_id, data_pedido, status, endereco_entrega, forma_pagamento, total FROM pedidos";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int usuarioId = rs.getInt("usuario_id");
                java.sql.Timestamp dataPedido = rs.getTimestamp("data_pedido");
                String status = rs.getString("status");
                String enderecoEntrega = rs.getString("endereco_entrega");
                String formaPagamento = rs.getString("forma_pagamento");
                double total = rs.getDouble("total");
                tableModel.addRow(new Object[]{id, usuarioId, dataPedido, status, enderecoEntrega, formaPagamento, total});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPedidos::new);
    }
}