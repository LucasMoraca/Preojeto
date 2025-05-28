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
 * A classe {@code TelaPedidosCliente} exibe uma lista dos pedidos realizados pelo cliente logado.
 * Ela consulta o banco de dados para buscar os pedidos associados ao ID do usuário na sessão.
 */
public class TelaPedidosCliente extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;
    private Integer clienteId; // ID do cliente logado

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Construtor da classe {@code TelaPedidosCliente}.
     * Inicializa a interface gráfica para exibir os pedidos do cliente.
     * Recupera o ID do cliente da sessão do usuário. Se nenhum usuário estiver logado,
     * exibe uma mensagem de aviso e fecha a tela.
     */
    public TelaPedidosCliente() {
        setTitle("Meus Pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Obtém o ID do usuário logado da sessão
        clienteId = SessaoUsuario.getInstance().getUsuarioId();
        // Verifica se há um usuário logado
        if (clienteId == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            dispose(); // Fecha a tela se não houver usuário logado
            return;
        }

        // Cria um modelo de tabela para exibir os dados dos pedidos
        tableModel = new DefaultTableModel(new Object[]{"ID Pedido", "Data", "Endereço", "Pagamento", "Total", "Email"}, 0);
        // Cria a tabela com o modelo
        pedidosTable = new JTable(tableModel);
        // Adiciona a tabela a um painel de rolagem para lidar com muitos pedidos
        JScrollPane scrollPane = new JScrollPane(pedidosTable);

        // Adiciona o painel de rolagem ao centro da janela
        add(scrollPane, BorderLayout.CENTER);

        // Carrega os pedidos do cliente do banco de dados e os exibe na tabela
        carregarPedidos();

        setVisible(true);
    }

    /**
     * Carrega os pedidos do cliente logado do banco de dados e os adiciona à tabela.
     * Utiliza o {@code clienteId} para filtrar os pedidos do usuário específico.
     */
    private void carregarPedidos() {
        String sql = "SELECT id, data_pedido, endereco_entrega, forma_pagamento, total, email FROM pedidos WHERE usuario_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Define o ID do cliente na consulta SQL
            pstmt.setInt(1, clienteId);
            // Executa a consulta
            ResultSet rs = pstmt.executeQuery();
            // Limpa as linhas existentes na tabela antes de adicionar os novos dados
            tableModel.setRowCount(0);

            // Itera sobre os resultados da consulta e adiciona cada pedido como uma nova linha na tabela
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
            // Exibe uma mensagem de erro caso ocorra algum problema ao carregar os pedidos
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Método principal para testes da {@code TelaPedidosCliente}.
     * Cria e exibe uma instância da tela de pedidos do cliente.
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPedidosCliente());
    }
}