package com.cafemanagement.view;

import com.cafemanagement.dao.CustomerDAO;
import com.cafemanagement.model.Customer;
import com.cafemanagement.util.ImageUtil;
import com.cafemanagement.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Quản lý hồ sơ khách hàng (Nền trắng, Kiểm lỗi nhập liệu SĐT 10 số & Email có @)
 */
public class CustomersPanel extends JPanel {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private JTable tblCustomers;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private List<Customer> customerList;

    public static final Color BROWN_PRIMARY = new Color(74, 44, 17);

    public CustomersPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE); // Toàn bộ nền màu trắng
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadCustomers();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Cafe Clients");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(40, 40, 40));
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Add", ImageUtil.getButtonIcon("icon_add.png", 16));
        btnAdd.setBackground(new Color(46, 125, 50)); // Xanh lá
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> showAddEditDialog(null));

        JButton btnEdit = new JButton("Edit", ImageUtil.getButtonIcon("icon_edit.png", 16));
        btnEdit.setBackground(BROWN_PRIMARY); // Nâu cà phê
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> {
            int row = tblCustomers.getSelectedRow();
            if (row >= 0 && customerList != null) {
                showAddEditDialog(customerList.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng trong bảng để sửa!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnDelete = new JButton("Delete", ImageUtil.getButtonIcon("icon_delete.png", 16));
        btnDelete.setBackground(new Color(192, 57, 43)); // Đỏ
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deleteCustomer());

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center: Search + Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchBar.setBackground(Color.WHITE);

        searchBar.add(new JLabel("Tìm khách hàng (Tên, SĐT, Email):"));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(280, 36));
        searchBar.add(txtSearch);

        JButton btnSearch = new JButton("Tìm", ImageUtil.getButtonIcon("icon_search.png", 15));
        btnSearch.setBackground(BROWN_PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> search());
        searchBar.add(btnSearch);

        JButton btnAll = new JButton("Tất cả");
        btnAll.setBackground(new Color(240, 240, 240));
        btnAll.addActionListener(e -> loadCustomers());
        searchBar.add(btnAll);

        centerPanel.add(searchBar, BorderLayout.NORTH);

        String[] cols = {"Mã KH", "Họ và Tên", "Số Điện Thoại", "Email", "Địa Chỉ", "Đơn Tích Lũy"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCustomers = new JTable(tableModel);
        tblCustomers.setRowHeight(34);
        tblCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblCustomers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblCustomers.getTableHeader().setBackground(new Color(245, 245, 247));
        tblCustomers.setBackground(Color.WHITE);
        tblCustomers.setShowGrid(true);
        tblCustomers.setGridColor(new Color(235, 235, 235));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblCustomers.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblCustomers.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tblCustomers.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tblCustomers);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadCustomers() {
        customerList = customerDAO.getAllCustomers();
        renderTable(customerList);
    }

    private void search() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) loadCustomers();
        else {
            customerList = customerDAO.search(kw);
            renderTable(customerList);
        }
    }

    private void renderTable(List<Customer> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (Customer c : list) {
                tableModel.addRow(new Object[]{
                        "KH" + String.format("%03d", c.getId()),
                        c.getName(),
                        c.getPhone() != null ? c.getPhone() : "-",
                        c.getEmail() != null ? c.getEmail() : "-",
                        c.getAddress() != null ? c.getAddress() : "-",
                        c.getOrdersCount() + " đơn"
                });
            }
        }
    }

    private void deleteCustomer() {
        int row = tblCustomers.getSelectedRow();
        if (row < 0 || customerList == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Customer c = customerList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa khách hàng: " + c.getName() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (customerDAO.deleteCustomer(c.getId())) {
                JOptionPane.showMessageDialog(this, "Đã xóa khách hàng thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng khỏi CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Hộp thoại Thêm/Sửa Khách Hàng với Kiểm lỗi nhập liệu (SĐT đủ 10 số, Email phải có @)
     */
    private void showAddEditDialog(Customer customer) {
        boolean isEdit = (customer != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Cập nhật thông tin khách hàng" : "Thêm khách hàng mới", true);
        dialog.setSize(480, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 16));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 30, 20, 30));

        JTextField txtName = new JTextField(isEdit ? customer.getName() : "");
        JTextField txtPhone = new JTextField(isEdit ? customer.getPhone() : "");
        JTextField txtEmail = new JTextField(isEdit ? customer.getEmail() : "");
        JTextField txtAddress = new JTextField(isEdit ? customer.getAddress() : "");

        form.add(new JLabel("Họ và Tên (*):")); form.add(txtName);
        form.add(new JLabel("Số điện thoại (Đủ 10 số) (*):")); form.add(txtPhone);
        form.add(new JLabel("Email liên hệ (Có '@') (*):")); form.add(txtEmail);
        form.add(new JLabel("Địa chỉ:")); form.add(txtAddress);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        JButton btnSave = new JButton(isEdit ? "Lưu thay đổi" : "Thêm khách", ImageUtil.getButtonIcon("icon_add.png", 16));
        btnSave.setBackground(BROWN_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String address = txtAddress.getText().trim();

            // 1. KIỂM LỖI NHẬP LIỆU (VALIDATION)
            if (!ValidationUtil.requireNonEmpty(name, "Họ và Tên khách hàng", dialog)) {
                txtName.requestFocus();
                return;
            }

            // 2. Bẫy lỗi SĐT: Bắt buộc phải đủ đúng 10 số
            if (!ValidationUtil.validatePhone(phone, dialog)) {
                txtPhone.requestFocus();
                return;
            }

            // 3. Bẫy lỗi Email: Bắt buộc phải có @ và đúng cú pháp
            if (!ValidationUtil.validateEmail(email, dialog)) {
                txtEmail.requestFocus();
                return;
            }

            if (isEdit) {
                customer.setName(name);
                customer.setPhone(phone);
                customer.setEmail(email);
                customer.setAddress(address);
                if (customerDAO.updateCustomer(customer)) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thông tin khách hàng thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadCustomers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật vào CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Customer newC = new Customer(0, name, email, phone, address, 0);
                if (customerDAO.addCustomer(newC)) {
                    JOptionPane.showMessageDialog(dialog, "Thêm khách hàng thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadCustomers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm khách hàng vào CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
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
