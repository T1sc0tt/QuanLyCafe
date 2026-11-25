package com.cafemanagement.view;

import com.cafemanagement.dao.CustomerDAO;
import com.cafemanagement.dao.OrderDAO;
import com.cafemanagement.dao.ProductDAO;
import com.cafemanagement.model.Order;
import com.cafemanagement.util.FormatUtil;
import com.cafemanagement.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Màn hình Bảng tổng quan hoạt động kinh doanh (Nền trắng, Thẻ chỉ số sắc nét)
 */
public class DashboardPanel extends JPanel {
    private final OrderDAO orderDAO = new OrderDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private JLabel lblRevenue;
    private JLabel lblTotalOrders;
    private JLabel lblTotalCustomers;
    private JLabel lblTotalProducts;
    private JTable tblRecentOrders;
    private DefaultTableModel tableModel;

    public static final Color BROWN_PRIMARY = new Color(74, 44, 17);

    public DashboardPanel() {
        setLayout(new BorderLayout(15, 20));
        setBackground(Color.WHITE); // Toàn bộ nền màu trắng
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(40, 40, 40));

        JButton btnRefresh = new JButton("Refresh", ImageUtil.getButtonIcon("icon_refresh.png", 16));
        btnRefresh.setBackground(BROWN_PRIMARY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadData());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content: Cards + Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 25));
        centerPanel.setBackground(Color.WHITE);

        // 4 Summary Metric Cards (Nền trắng, viền mềm mại, chữ số màu nổi bật)
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 18, 0));
        cardsGrid.setBackground(Color.WHITE);
        cardsGrid.setPreferredSize(new Dimension(800, 115));

        lblRevenue = new JLabel("0 VNĐ");
        lblTotalOrders = new JLabel("0");
        lblTotalCustomers = new JLabel("0");
        lblTotalProducts = new JLabel("0");

        cardsGrid.add(createStatCard("Total Sales", lblRevenue, new Color(46, 125, 50), "icon_totalsales.png"));
        cardsGrid.add(createStatCard("ORDERS", lblTotalOrders, BROWN_PRIMARY, "icon_orders.png"));
        cardsGrid.add(createStatCard("CUSTOMERS", lblTotalCustomers, new Color(190, 80, 20), "icon_customers.png"));
        cardsGrid.add(createStatCard("MENU", lblTotalProducts, new Color(110, 50, 140), "icon_menu.png"));

        centerPanel.add(cardsGrid, BorderLayout.NORTH);

        // Recent Orders Section
        JPanel tableSection = new JPanel(new BorderLayout(0, 12));
        tableSection.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Danh sách đơn hàng phát sinh gần nhất:");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSub.setForeground(new Color(40, 40, 40));
        tableSection.add(lblSub, BorderLayout.NORTH);

        String[] cols = {"Mã Đơn", "Khách Hàng", "Nhân Viên Lập", "Tổng Tiền", "Trạng Thái", "Thời Gian Đặt"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblRecentOrders = new JTable(tableModel);
        tblRecentOrders.setRowHeight(34);
        tblRecentOrders.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblRecentOrders.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblRecentOrders.getTableHeader().setBackground(new Color(245, 245, 247));
        tblRecentOrders.getTableHeader().setForeground(new Color(40, 40, 40));
        tblRecentOrders.setBackground(Color.WHITE);
        tblRecentOrders.setShowGrid(true);
        tblRecentOrders.setGridColor(new Color(235, 235, 235));

        // Canh giữa mã đơn và trạng thái
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblRecentOrders.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblRecentOrders.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tblRecentOrders);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        tableSection.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableSection, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor, String iconName) {
        JPanel card = new JPanel(new BorderLayout(12, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(new Color(110, 110, 110));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(accentColor);

        textPanel.add(lblTitle);
        textPanel.add(valueLabel);
        card.add(textPanel, BorderLayout.CENTER);

        JLabel lblIcon = new JLabel(ImageUtil.getButtonIcon(iconName, 36));
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    public void loadData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private double revenue;
            private int ordersCount;
            private int customersCount;
            private int productsCount;
            private List<Order> recentOrders;

            @Override
            protected Void doInBackground() {
                revenue = orderDAO.calculateTotalRevenue();
                ordersCount = orderDAO.getTotalOrderCount();
                customersCount = customerDAO.getTotalCustomerCount();
                productsCount = productDAO.getTotalProductCount();
                recentOrders = orderDAO.getRecentOrders(15);
                return null;
            }

            @Override
            protected void done() {
                lblRevenue.setText(FormatUtil.formatCurrency(revenue));
                lblTotalOrders.setText(String.valueOf(ordersCount));
                lblTotalCustomers.setText(String.valueOf(customersCount));
                lblTotalProducts.setText(String.valueOf(productsCount));

                tableModel.setRowCount(0);
                if (recentOrders != null) {
                    for (Order o : recentOrders) {
                        tableModel.addRow(new Object[]{
                                "#" + o.getId(),
                                o.getCustomerName() != null ? o.getCustomerName() : "Khách vãng lai",
                                o.getUserName() != null ? o.getUserName() : "Hệ thống",
                                FormatUtil.formatCurrency(o.getTotal()),
                                o.getStatus(),
                                FormatUtil.formatDateTime(o.getOrderDate())
                        });
                    }
                }
            }
        };
        worker.execute();
    }
}
