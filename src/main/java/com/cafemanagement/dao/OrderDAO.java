package com.cafemanagement.dao;

import com.cafemanagement.model.Order;
import com.cafemanagement.model.OrderDetail;
import com.cafemanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /**
     * Tạo đơn hàng mới và trả về mã ID tự sinh (int > 0 nếu thành công, 0 nếu thất bại)
     */
    public int createOrder(Order order) {
        String sqlOrder = "INSERT INTO orders (customer_id, user_id, total, status, order_date) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {

            if (order.getCustomerId() > 0) stmt.setInt(1, order.getCustomerId());
            else stmt.setNull(1, Types.INTEGER);

            if (order.getUserId() > 0) stmt.setInt(2, order.getUserId());
            else stmt.setNull(2, Types.INTEGER);

            stmt.setDouble(3, order.getTotal());
            stmt.setString(4, order.getStatus() != null ? order.getStatus() : "Pending");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = stmt.getGeneratedKeys()) {
                    if (gk.next()) {
                        int id = gk.getInt(1);
                        order.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lưu danh sách các món chi tiết của đơn hàng vào bảng order_details
     */
    public boolean saveOrderDetails(int orderId, List<OrderDetail> items) {
        if (items == null || items.isEmpty()) return true;
        String sqlDetail = "INSERT INTO order_details (order_id, product_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlDetail)) {
            for (OrderDetail item : items) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getUnitPrice());
                stmt.setDouble(5, item.getLineTotal());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.name AS customer_name, u.fullname AS user_name " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN users u ON o.user_id = u.id " +
                     "ORDER BY o.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.name AS customer_name, u.fullname AS user_name " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN users u ON o.user_id = u.id " +
                     "WHERE o.status = ? ORDER BY o.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.*, p.name AS product_name FROM order_details od " +
                     "JOIN products p ON od.product_id = p.id WHERE od.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail od = new OrderDetail(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("line_total")
                    );
                    od.setProductName(rs.getString("product_name"));
                    list.add(od);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        return updateStatus(orderId, newStatus);
    }

    public double calculateTotalRevenue() {
        return getTotalRevenue();
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(total) FROM orders WHERE status = 'Completed'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getTotalOrderCount() {
        return countOrders();
    }

    public int countOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Order> getRecentOrders(int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.name AS customer_name, u.fullname AS user_name " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "LEFT JOIN users u ON o.user_id = u.id " +
                     "ORDER BY o.id DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order o = new Order(
            rs.getInt("id"),
            rs.getInt("customer_id"),
            rs.getInt("user_id"),
            rs.getDouble("total"),
            rs.getString("status"),
            rs.getTimestamp("order_date")
        );
        o.setCustomerName(rs.getString("customer_name"));
        o.setUserName(rs.getString("user_name"));
        return o;
    }
}
