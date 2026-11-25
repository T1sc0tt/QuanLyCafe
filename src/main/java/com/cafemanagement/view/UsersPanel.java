package com.cafemanagement.view;

import com.cafemanagement.dao.UserDAO;
import com.cafemanagement.model.User;
import com.cafemanagement.util.ImageUtil;
import com.cafemanagement.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Quản lý tài khoản và phân quyền Admin/User (Nền trắng, Kiểm lỗi Email & SĐT)
 */
public class UsersPanel extends JPanel {
    private final UserDAO userDAO = new UserDAO();
    private JTable tblUsers;
    private DefaultTableModel tableModel;
    private List<User> userList;

    public static final Color BROWN_PRIMARY = new Color(74, 44, 17);

    public UsersPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE); // Toàn bộ nền màu trắng
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadUsers();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("User Management (ADMIN ONLY)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(40, 40, 40));
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Add", ImageUtil.getButtonIcon("icon_add.png", 16));
        btnAdd.setBackground(new Color(46, 125, 50)); // Xanh lá
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.addActionListener(e -> showAddEditDialog(null));

        JButton btnEdit = new JButton("Edit", ImageUtil.getButtonIcon("icon_edit.png", 16));
        btnEdit.setBackground(BROWN_PRIMARY); // Nâu cà phê
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEdit.addActionListener(e -> {
            int row = tblUsers.getSelectedRow();
            if (row >= 0 && userList != null) {
                showAddEditDialog(userList.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnDelete = new JButton("Delete", ImageUtil.getButtonIcon("icon_delete.png", 16));
        btnDelete.setBackground(new Color(192, 57, 43)); // Đỏ
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.addActionListener(e -> deleteUser());

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] cols = {"Mã ID", "Họ và Tên", "Email Đăng Nhập", "Số Điện Thoại", "Role"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers = new JTable(tableModel);
        tblUsers.setRowHeight(34);
        tblUsers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblUsers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblUsers.getTableHeader().setBackground(new Color(245, 245, 247));
        tblUsers.setBackground(Color.WHITE);
        tblUsers.setShowGrid(true);
        tblUsers.setGridColor(new Color(235, 235, 235));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblUsers.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblUsers.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblUsers.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tblUsers);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadUsers() {
        userList = userDAO.getAllUsers();
        tableModel.setRowCount(0);
        if (userList != null) {
            for (User u : userList) {
                tableModel.addRow(new Object[]{
                        "#" + u.getId(),
                        u.getFullname(),
                        u.getEmail(),
                        u.getPhone() != null ? u.getPhone() : "-",
                        u.getRole()
                });
            }
        }
    }

    private void deleteUser() {
        int row = tblUsers.getSelectedRow();
        if (row < 0 || userList == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần xóa!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User u = userList.get(row);
        if ("ADMIN".equalsIgnoreCase(u.getRole()) && u.getId() == 1) {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản Quản trị viên gốc!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tài khoản: " + u.getEmail() + "?",
                "Xác nhận xóa tài khoản", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(u.getId())) {
                JOptionPane.showMessageDialog(this, "Đã xóa tài khoản thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa tài khoản khỏi CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Hộp thoại tạo/sửa tài khoản với Bẫy lỗi Email có @ và SĐT 10 số
     */
    private void showAddEditDialog(User user) {
        boolean isEdit = (user != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Cập nhật tài khoản" : "Tạo tài khoản mới", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 16));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 30, 20, 30));

        JTextField txtName = new JTextField(isEdit ? user.getFullname() : "");
        JTextField txtEmail = new JTextField(isEdit ? user.getEmail() : "");
        JPasswordField txtPass = new JPasswordField(isEdit ? user.getPassword() : "");
        JTextField txtPhone = new JTextField(isEdit && user.getPhone() != null ? user.getPhone() : "");
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        if (isEdit) cbRole.setSelectedItem(user.getRole());

        form.add(new JLabel("Họ và Tên (*):")); form.add(txtName);
        form.add(new JLabel("Email đăng nhập (Có '@') (*):")); form.add(txtEmail);
        form.add(new JLabel("Mật khẩu (*):")); form.add(txtPass);
        form.add(new JLabel("Số điện thoại (10 chữ số):")); form.add(txtPhone);
        form.add(new JLabel("Phân quyền:")); form.add(cbRole);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        JButton btnSave = new JButton(isEdit ? "Lưu thay đổi" : "Tạo tài khoản", ImageUtil.getButtonIcon("icon_add.png", 16));
        btnSave.setBackground(BROWN_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            String phone = txtPhone.getText().trim();
            String role = (String) cbRole.getSelectedItem();

            // 1. KIỂM LỖI NHẬP LIỆU (VALIDATION)
            if (!ValidationUtil.requireNonEmpty(name, "Họ và Tên", dialog)) {
                txtName.requestFocus();
                return;
            }

            // 2. Bẫy lỗi Email: Bắt buộc phải có @ và đúng cấu trúc
            if (!ValidationUtil.validateEmail(email, dialog)) {
                txtEmail.requestFocus();
                return;
            }

            // 3. Bẫy lỗi Mật khẩu không được để trống
            if (!ValidationUtil.requireNonEmpty(pass, "Mật khẩu", dialog)) {
                txtPass.requestFocus();
                return;
            }

            // 4. Bẫy lỗi SĐT (nếu có nhập thì phải đủ đúng 10 số)
            if (!phone.isEmpty() && !ValidationUtil.validatePhone(phone, dialog)) {
                txtPhone.requestFocus();
                return;
            }

            if (isEdit) {
                user.setFullname(name);
                user.setEmail(email);
                user.setPassword(pass);
                user.setPhone(phone);
                user.setRole(role);
                if (userDAO.updateUser(user)) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật tài khoản thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật tài khoản vào CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                User newU = new User(0, name, email, pass, phone, role);
                if (userDAO.addUser(newU)) {
                    JOptionPane.showMessageDialog(dialog, "Tạo tài khoản thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi tạo tài khoản (Email có thể đã tồn tại)!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
