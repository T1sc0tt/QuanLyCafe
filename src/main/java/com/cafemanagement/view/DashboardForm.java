package com.cafemanagement.view;

import com.cafemanagement.model.User;
import com.cafemanagement.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Giao diện chính (Khung sườn ứng dụng với Sidebar màu nâu cà phê, vùng nội dung màu trắng)
 */
public class DashboardForm extends JFrame {
    private final User currentUser;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String activeNav = "dashboard";

    // Bảng màu chuẩn: Panel điều khiển màu nâu cà phê, còn lại màu trắng
    public static final Color BROWN_PRIMARY = new Color(74, 44, 17);    // #4A2C11
    public static final Color BROWN_DARK = new Color(50, 28, 9);        // #321C09
    public static final Color BROWN_HOVER = new Color(98, 59, 25);      // #623B19
    public static final Color BROWN_ACCENT = new Color(212, 163, 115);  // #D4A373

    public DashboardForm(User user) {
        this.currentUser = user;
        setTitle("Coffee Shop - " + user.getFullname() + " [" + user.getRole() + "]");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1024, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Color.WHITE);

        // ========================================================
        // 1. PANEL ĐIỀU KHIỂN BÊN TRÁI (SIDEBAR - MÀU NÂU CÀ PHÊ)
        // ========================================================
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, 750));
        sidebar.setBackground(BROWN_PRIMARY);

        // Header thương hiệu & người dùng (Nâu đậm hơn một chút để tạo chiều sâu)
        JPanel brandPanel = new JPanel(new BorderLayout(10, 10));
        brandPanel.setBackground(BROWN_DARK);
        brandPanel.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel lblLogo = new JLabel("CAFE MANAGEMENT", ImageUtil.getButtonIcon("cafe_logo.png", 32), SwingConstants.LEFT);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setIconTextGap(10);

        JPanel userBox = new JPanel(new GridLayout(2, 1, 0, 3));
        userBox.setOpaque(false);
        userBox.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblUser = new JLabel("" + currentUser.getFullname());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(250, 240, 230));

        JLabel lblRole = new JLabel("Role: " + (currentUser.isAdmin() ? "Quản trị viên (Admin)" : "Nhân viên (User)"));
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(BROWN_ACCENT);

        userBox.add(lblUser);
        userBox.add(lblRole);

        brandPanel.add(lblLogo, BorderLayout.NORTH);
        brandPanel.add(userBox, BorderLayout.CENTER);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Danh sách các nút chức năng điều khiển
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(BROWN_PRIMARY);
        menuPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        addNavButton(menuPanel, "Dashboard", "dashboard", "icon_dashboard.png");
        addNavButton(menuPanel, "Menu", "menu", "icon_menu.png");
        addNavButton(menuPanel, "Customers", "customers", "icon_customers.png");
        addNavButton(menuPanel, "Orders", "orders", "icon_orders.png");

        if (currentUser.isAdmin()) {
            addNavButton(menuPanel, "Users", "users", "icon_users.png");
        }

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Nút Đăng xuất ở cuối sidebar điều khiển
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BROWN_PRIMARY);
        bottomPanel.setBorder(new EmptyBorder(15, 15, 20, 15));

        JButton btnLogout = new JButton("Sign Out", ImageUtil.getButtonIcon("icon_logout.png", 18));
        btnLogout.setPreferredSize(new Dimension(220, 42));
        btnLogout.setBackground(new Color(150, 40, 30)); // Đỏ nâu
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnLogout.addActionListener(e -> logout());
        bottomPanel.add(btnLogout, BorderLayout.CENTER);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);
        rootPanel.add(sidebar, BorderLayout.WEST);

        // ========================================================
        // 2. KHU VỰC NỘI DUNG CHÍNH (TOÀN BỘ CÒN LẠI LÀ MÀU TRẮNG)
        // ========================================================
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Color.WHITE);

        DashboardPanel pnlDashboard = new DashboardPanel();
        MenuPanel pnlMenu = new MenuPanel(currentUser);
        CustomersPanel pnlCustomers = new CustomersPanel();
        OrdersPanel pnlOrders = new OrdersPanel(currentUser);

        mainContentPanel.add(pnlDashboard, "dashboard");
        mainContentPanel.add(pnlMenu, "menu");
        mainContentPanel.add(pnlCustomers, "customers");
        mainContentPanel.add(pnlOrders, "orders");

        if (currentUser.isAdmin()) {
            UsersPanel pnlUsers = new UsersPanel();
            mainContentPanel.add(pnlUsers, "users");
        }

        rootPanel.add(mainContentPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // Mặc định chọn tab Tổng quan
        setActiveNav("dashboard");
    }

    private void addNavButton(JPanel container, String text, String cardName, String iconName) {
        JButton btn = new JButton(text, ImageUtil.getButtonIcon(iconName, 20));
        btn.setMaximumSize(new Dimension(230, 44));
        btn.setPreferredSize(new Dimension(230, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(12);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(BROWN_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 8, 4, 8),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, cardName);
            setActiveNav(cardName);
        });

        navButtons.put(cardName, btn);
        container.add(btn);
        container.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void setActiveNav(String cardName) {
        this.activeNav = cardName;
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(cardName)) {
                btn.setBackground(BROWN_HOVER);
                btn.setForeground(BROWN_ACCENT);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, BROWN_ACCENT),
                        BorderFactory.createEmptyBorder(6, 8, 6, 10)
                ));
            } else {
                btn.setBackground(BROWN_PRIMARY);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 10));
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            dispose();
        }
    }
}
