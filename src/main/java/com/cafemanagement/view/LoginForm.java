package com.cafemanagement.view;

import com.cafemanagement.dao.UserDAO;
import com.cafemanagement.model.User;
import com.cafemanagement.util.ImageUtil;
import com.cafemanagement.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Màn hình Đăng nhập (Panel điều khiển màu nâu cà phê, thân form màu trắng)
 */
public class LoginForm extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    private final UserDAO userDAO = new UserDAO();

    public static final Color BROWN_PRIMARY = new Color(74, 44, 17);    // #4A2C11
    public static final Color BROWN_ACCENT = new Color(212, 163, 115);  // #D4A373

    public LoginForm() {
        setTitle("Login - Coffee Shop");
        setSize(460, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Color.WHITE);

        // Header Panel màu nâu cà phê đậm sang trọng
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BROWN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblLogo = new JLabel("COFFEE SHOP MANAGEMENT", ImageUtil.getButtonIcon("cafe_logo.png", 36), SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setIconTextGap(12);

        JLabel lblSub = new JLabel("COFFEE SHOP", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(BROWN_ACCENT);
        lblSub.setBorder(new EmptyBorder(6, 0, 0, 0));

        headerPanel.add(lblLogo, BorderLayout.CENTER);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Thân Form màu trắng tinh tế
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        // Email Label & Field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblEmail = new JLabel("Email đăng nhập (*):");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmail.setForeground(new Color(40, 40, 40));
        formPanel.add(lblEmail, gbc);

        gbc.gridy = 1;
        txtEmail = new JTextField("admin@gmail.com");
        txtEmail.setPreferredSize(new Dimension(300, 38));
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(txtEmail, gbc);

        // Password Label & Field
        gbc.gridy = 2;
        JLabel lblPass = new JLabel("Mật khẩu (*):");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(40, 40, 40));
        formPanel.add(lblPass, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField("admin123");
        txtPassword.setPreferredSize(new Dimension(300, 38));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(txtPassword, gbc);

        // Status Label
        gbc.gridy = 4;
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(200, 40, 40));
        formPanel.add(lblStatus, gbc);

        // Nút Đăng nhập màu nâu cà phê
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 5, 0);
        btnLogin = new JButton("ĐĂNG NHẬP HỆ THỐNG", ImageUtil.getButtonIcon("icon_dashboard.png", 18));
        btnLogin.setPreferredSize(new Dimension(300, 44));
        btnLogin.setBackground(BROWN_PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this::handleLogin);
        formPanel.add(btnLogin, gbc);

        // Ghi chú tài khoản thử nghiệm
        gbc.gridy = 6;
        gbc.insets = new Insets(15, 0, 0, 0);
        
       

        rootPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // Nhấn Enter để đăng nhập
        getRootPane().setDefaultButton(btnLogin);
    }

    private void handleLogin(ActionEvent e) {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // 1. Kiểm lỗi nhập liệu (Validation)
        if (!ValidationUtil.validateEmail(email, this)) {
            txtEmail.requestFocus();
            return;
        }

        if (!ValidationUtil.requireNonEmpty(password, "Mật khẩu", this)) {
            txtPassword.requestFocus();
            return;
        }

        lblStatus.setText("Đang kiểm tra thông tin tài khoản...");
        btnLogin.setEnabled(false);

        // 2. Xác thực tài khoản với Database
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return userDAO.login(email, password);
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    User user = get();
                    if (user != null) {
                        lblStatus.setText("Đăng nhập thành công!");
                        new DashboardForm(user).setVisible(true);
                        dispose();
                    } else {
                        lblStatus.setText("Email hoặc mật khẩu không chính xác!");
                        JOptionPane.showMessageDialog(LoginForm.this,
                                "Thông tin đăng nhập không chính xác hoặc tài khoản không tồn tại!\n" +
                                "Vui lòng kiểm tra lại email và mật khẩu.",
                                "Đăng Nhập Thất Bại", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    lblStatus.setText("Lỗi kết nối cơ sở dữ liệu!");
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Không thể kết nối đến cơ sở dữ liệu MySQL (cafe_db)!\n" +
                            "Vui lòng đảm bảo XAMPP/MySQL đã được khởi động.",
                            "Lỗi Kết Nối CSDL", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
