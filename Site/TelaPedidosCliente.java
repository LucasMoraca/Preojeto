package Site;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaPedidosCliente extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;
    private Integer clienteId; // ID do cliente logado (agora Integer)

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public TelaPedidosCliente() {
        setTitle("Meus Pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        clienteId = SessaoUsuario.getInstance().getUsuarioId();
        if (clienteId == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        tableModel = new DefaultTableModel(new Object[]{"ID Pedido", "Data", "Endereço", "Pagamento", "Total", "Email"}, 0);
        pedidosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);

        add(scrollPane, BorderLayout.CENTER);

        carregarPedidos();

        setVisible(true);
    }

    private void carregarPedidos() {
        String sql = "SELECT id, data_pedido, endereco_entrega, forma_pagamento, total, email FROM pedidos WHERE usuario_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clienteId);
            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // Limpa a tabela antes de carregar

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("data_pedido"),
                        rs.getString("endereco_entrega"),
                        rs.getString("forma_pagamento"),
                        rs.getDouble("total"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPedidosCliente());
    }
}