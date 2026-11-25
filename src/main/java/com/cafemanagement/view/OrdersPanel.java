package com.cafemanagement.view;

import com.cafemanagement.dao.CustomerDAO;
import com.cafemanagement.dao.OrderDAO;
import com.cafemanagement.dao.ProductDAO;
import com.cafemanagement.model.*;
import com.cafemanagement.util.FormatUtil;
import com.cafemanagement.util.ImageUtil;
import com.cafemanagement.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý Bán hàng & Đơn hàng (Nền trắng, Kiểm lỗi giỏ hàng & số lượng)
 */
public class OrdersPanel extends JPanel {
    private final User currentUser;
    private final OrderDAO orderDAO = new OrderDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private JTable tblOrders;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbStatusFilter;
    private List<Order> currentOrders;

    public static final Color BROWN_PRIMARY = new Color(74, 44, 17);

    public OrdersPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE); // Toàn bộ nền màu trắng
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadOrders();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Cafe Orders");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(40, 40, 40));
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton btnNewOrder = new JButton("Add", ImageUtil.getButtonIcon("icon_add.png", 16));
        btnNewOrder.setBackground(new Color(46, 125, 50)); // Xanh lá
        btnNewOrder.setForeground(Color.WHITE);
        btnNewOrder.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNewOrder.setFocusPainted(false);
        btnNewOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNewOrder.addActionListener(e -> showNewOrderDialog());

        JButton btnViewDetails = new JButton("Print", ImageUtil.getButtonIcon("icon_print.png", 16));
        btnViewDetails.setBackground(BROWN_PRIMARY); // Nâu cà phê
        btnViewDetails.setForeground(Color.WHITE);
        btnViewDetails.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnViewDetails.setFocusPainted(false);
        btnViewDetails.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewDetails.addActionListener(e -> showOrderDetailsDialog());

        JButton btnChangeStatus = new JButton("Edit", ImageUtil.getButtonIcon("icon_edit.png", 16));
        btnChangeStatus.setBackground(new Color(190, 80, 20)); // Cam nâu
        btnChangeStatus.setForeground(Color.WHITE);
        btnChangeStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnChangeStatus.setFocusPainted(false);
        btnChangeStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangeStatus.addActionListener(e -> changeOrderStatus());

        actionPanel.add(btnNewOrder);
        actionPanel.add(btnViewDetails);
        actionPanel.add(btnChangeStatus);
        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Filter & Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setBackground(Color.WHITE);

        filterBar.add(new JLabel("Status:"));
        cbStatusFilter = new JComboBox<>(new String[]{
                "-- Tất cả trạng thái --", "Pending", "Processing", "Completed", "Cancelled"
        });
        cbStatusFilter.setPreferredSize(new Dimension(200, 36));
        cbStatusFilter.addActionListener(e -> filterOrders());
        filterBar.add(cbStatusFilter);

        JButton btnRefresh = new JButton("Refresh", ImageUtil.getButtonIcon("icon_refresh.png", 15));
        btnRefresh.setBackground(new Color(240, 240, 240));
        btnRefresh.addActionListener(e -> loadOrders());
        filterBar.add(btnRefresh);

        centerPanel.add(filterBar, BorderLayout.NORTH);

        String[] cols = {"Mã Đơn", "Khách Hàng", "Thu Ngân Phụ Trách", "Tổng Tiền", "Trạng Thái", "Ngày Giờ Đặt"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblOrders = new JTable(tableModel);
        tblOrders.setRowHeight(34);
        tblOrders.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblOrders.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblOrders.getTableHeader().setBackground(new Color(245, 245, 247));
        tblOrders.getTableHeader().setForeground(new Color(40, 40, 40));
        tblOrders.setBackground(Color.WHITE);
        tblOrders.setShowGrid(true);
        tblOrders.setGridColor(new Color(235, 235, 235));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblOrders.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblOrders.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblOrders.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tblOrders);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadOrders() {
        currentOrders = orderDAO.getAllOrders();
        renderTable(currentOrders);
    }

    private void filterOrders() {
        String selected = (String) cbStatusFilter.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            loadOrders();
        } else {
            currentOrders = orderDAO.getOrdersByStatus(selected);
            renderTable(currentOrders);
        }
    }

    private void renderTable(List<Order> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (Order o : list) {
                tableModel.addRow(new Object[]{
                        "#" + o.getId(),
                        o.getCustomerName() != null ? o.getCustomerName() : "Khách vãng lai",
                        o.getUserName() != null ? o.getUserName() : "Thu ngân",
                        FormatUtil.formatCurrency(o.getTotal()),
                        o.getStatus(),
                        FormatUtil.formatDateTime(o.getOrderDate())
                });
            }
        }
    }

    /**
     * Hộp thoại tạo đơn hàng bán lẻ với Validation đầy đủ
     */
    private void showNewOrderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo đơn hàng mới - POS Quầy bán lẻ", true);
        dialog.setSize(750, 580);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // 1. Top Customer Info
        JPanel custPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        custPanel.setBackground(Color.WHITE);
        custPanel.setBorder(BorderFactory.createTitledBorder("1. Thông tin khách hàng"));

        JComboBox<Customer> cbCustomers = new JComboBox<>();
        List<Customer> customers = customerDAO.getAllCustomers();
        cbCustomers.addItem(new Customer(0, "Khách vãng lai", "", "", "", 0));
        for (Customer c : customers) cbCustomers.addItem(c);
        cbCustomers.setPreferredSize(new Dimension(320, 36));

        custPanel.add(new JLabel("Chọn khách hàng:"));
        custPanel.add(cbCustomers);
        dialog.add(custPanel, BorderLayout.NORTH);

        // 2. Center: Chọn món & Giỏ hàng
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(Color.WHITE);
        center.setBorder(new EmptyBorder(0, 15, 0, 15));

        // Selector Bar
        JPanel selectBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        selectBar.setBackground(Color.WHITE);
        selectBar.setBorder(BorderFactory.createTitledBorder("2. Chọn món & Số lượng"));

        JComboBox<Product> cbProducts = new JComboBox<>();
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) cbProducts.addItem(p);
        cbProducts.setPreferredSize(new Dimension(280, 36));

        JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spQty.setPreferredSize(new Dimension(70, 36));

        JButton btnAddProduct = new JButton("Add", ImageUtil.getButtonIcon("icon_add.png", 15));
        btnAddProduct.setBackground(BROWN_PRIMARY);
        btnAddProduct.setForeground(Color.WHITE);
        btnAddProduct.setFont(new Font("Segoe UI", Font.BOLD, 12));

        selectBar.add(new JLabel("Món:"));
        selectBar.add(cbProducts);
        selectBar.add(new JLabel("Số lượng:"));
        selectBar.add(spQty);
        selectBar.add(btnAddProduct);
        center.add(selectBar, BorderLayout.NORTH);

        // Cart Table
        String[] cartCols = {"Tên Món", "Đơn Giá", "Số Lượng", "Thành Tiền"};
        DefaultTableModel cartModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblCart = new JTable(cartModel);
        tblCart.setRowHeight(30);
        tblCart.setBackground(Color.WHITE);

        List<OrderDetail> cartItems = new ArrayList<>();
        JLabel lblTotal = new JLabel("Tổng tiền thanh toán: 0 VNĐ", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTotal.setForeground(new Color(46, 125, 50));
        lblTotal.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnAddProduct.addActionListener(e -> {
            Product p = (Product) cbProducts.getSelectedItem();
            if (p != null) {
                int qty = (int) spQty.getValue();
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng món phải lớn hơn 0!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                OrderDetail od = new OrderDetail(p.getId(), p.getName(), qty, p.getPrice());
                cartItems.add(od);
                cartModel.addRow(new Object[]{
                        p.getName(),
                        FormatUtil.formatCurrency(p.getPrice()),
                        qty,
                        FormatUtil.formatCurrency(od.getLineTotal())
                });

                double sum = cartItems.stream().mapToDouble(OrderDetail::getLineTotal).sum();
                lblTotal.setText("Tổng tiền thanh toán: " + FormatUtil.formatCurrency(sum));
            }
        });

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(Color.WHITE);
        cartPanel.setBorder(BorderFactory.createTitledBorder("3. Danh mục món đã chọn"));
        cartPanel.add(new JScrollPane(tblCart), BorderLayout.CENTER);
        cartPanel.add(lblTotal, BorderLayout.SOUTH);

        center.add(cartPanel, BorderLayout.CENTER);
        dialog.add(center, BorderLayout.CENTER);

        // 3. Bottom Actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setBackground(Color.WHITE);

        JComboBox<String> cbInitialStatus = new JComboBox<>(new String[]{"Completed", "Processing", "Pending"});
        cbInitialStatus.setPreferredSize(new Dimension(140, 36));

        JButton btnFinish = new JButton("Hoàn tất & In Hóa Đơn", ImageUtil.getButtonIcon("icon_print.png", 16));
        btnFinish.setBackground(new Color(46, 125, 50));
        btnFinish.setForeground(Color.WHITE);
        btnFinish.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnFinish.addActionListener(e -> {
            // KIỂM LỖI NHẬP LIỆU: Bắt buộc giỏ hàng phải có ít nhất 1 món
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                        "Đơn hàng chưa có món nào!\nVui lòng chọn ít nhất 1 món trước khi lưu đơn.", 
                        "Cảnh Báo Giỏ Hàng", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Customer c = (Customer) cbCustomers.getSelectedItem();
            int custId = (c != null && c.getId() > 0) ? c.getId() : 0;
            double sum = cartItems.stream().mapToDouble(OrderDetail::getLineTotal).sum();
            String st = (String) cbInitialStatus.getSelectedItem();

            Order newOrder = new Order();
            newOrder.setCustomerId(custId);
            newOrder.setUserId(currentUser.getId());
            newOrder.setTotal(sum);
            newOrder.setStatus(st != null ? st : "Completed");

            int createdId = orderDAO.createOrder(newOrder);
            if (createdId > 0) {
                orderDAO.saveOrderDetails(createdId, cartItems);
                if (custId > 0) {
                    customerDAO.incrementOrderCount(custId);
                }
                JOptionPane.showMessageDialog(dialog, "Đã tạo đơn hàng #" + createdId + " thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadOrders();

                // Tự động mở xem hóa đơn
                newOrder.setId(createdId);
                newOrder.setCustomerName(c != null ? c.getName() : "Khách vãng lai");
                newOrder.setUserName(currentUser.getFullname());
                showReceiptDialog(newOrder, cartItems);
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi tạo đơn hàng trong CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.addActionListener(e -> dialog.dispose());

        bottom.add(new JLabel("Trạng thái:"));
        bottom.add(cbInitialStatus);
        bottom.add(btnFinish);
        bottom.add(btnCancel);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void changeOrderStatus() {
        int row = tblOrders.getSelectedRow();
        if (row < 0 || currentOrders == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần cập nhật trạng thái!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Order o = currentOrders.get(row);
        String[] statuses = {"Pending", "Processing", "Completed", "Cancelled"};
        String chosen = (String) JOptionPane.showInputDialog(this,
                "Chọn trạng thái mới cho Đơn hàng #" + o.getId() + ":",
                "Cập nhật trạng thái đơn",
                JOptionPane.QUESTION_MESSAGE, null, statuses, o.getStatus());

        if (chosen != null && !chosen.equals(o.getStatus())) {
            if (orderDAO.updateOrderStatus(o.getId(), chosen)) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái đơn thành: " + chosen, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showOrderDetailsDialog() {
        int row = tblOrders.getSelectedRow();
        if (row < 0 || currentOrders == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để xem chi tiết!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Order o = currentOrders.get(row);
        List<OrderDetail> items = orderDAO.getOrderDetails(o.getId());
        showReceiptDialog(o, items);
    }

    private void showReceiptDialog(Order order, List<OrderDetail> items) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Hóa Đơn Thanh Toán - #" + order.getId(), true);
        dialog.setSize(480, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea txtReceipt = new JTextArea();
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setEditable(false);
        txtReceipt.setBackground(Color.WHITE);

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("                   COFFEE SHOP                       \n");
        sb.append("      Đ/c: 216 Đường Lĩnh Nam, Hoàng Mai, TP.Hà Nội   \n");
        sb.append("               Hotline: 0336106980                \n");
        sb.append("====================================================\n");
        sb.append("HÓA ĐƠN BÁN HÀNG: #").append(order.getId()).append("\n");
        sb.append("Ngày giờ : ").append(FormatUtil.formatDateTime(order.getOrderDate())).append("\n");
        sb.append("Thu ngân : ").append(order.getUserName() != null ? order.getUserName() : currentUser.getFullname()).append("\n");
        sb.append("Khách    : ").append(order.getCustomerName() != null ? order.getCustomerName() : "Khách vãng lai").append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("%-22s %5s %10s %10s\n", "Tên Món", "SL", "Đơn Giá", "Thành Tiền"));
        sb.append("----------------------------------------------------\n");

        if (items != null) {
            for (OrderDetail item : items) {
                sb.append(String.format("%-22s %5d %10s %10s\n",
                        item.getProductName().length() > 20 ? item.getProductName().substring(0, 19) : item.getProductName(),
                        item.getQuantity(),
                        FormatUtil.formatCurrency(item.getUnitPrice()),
                        FormatUtil.formatCurrency(item.getLineTotal())));
            }
        }

        sb.append("----------------------------------------------------\n");
        sb.append(String.format("TỔNG TIỀN PHẢI TRẢ: %30s\n", FormatUtil.formatCurrency(order.getTotal())));
        sb.append("Trạng thái: ").append(order.getStatus()).append("\n");
        sb.append("====================================================\n");
        sb.append("            CẢM ƠN QUÝ KHÁCH - HẸN GẶP LẠI!         \n");
        sb.append("====================================================\n");

        txtReceipt.setText(sb.toString());
        dialog.add(new JScrollPane(txtReceipt), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(Color.WHITE);

        JButton btnPrint = new JButton("In Hóa Đơn Ra Giấy", ImageUtil.getButtonIcon("icon_print.png", 16));
        btnPrint.setBackground(BROWN_PRIMARY);
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrint.addActionListener(e -> {
            try {
                boolean ok = txtReceipt.print();
                if (ok) JOptionPane.showMessageDialog(dialog, "Đã gửi lệnh in hóa đơn thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi in: " + ex.getMessage());
            }
        });

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
