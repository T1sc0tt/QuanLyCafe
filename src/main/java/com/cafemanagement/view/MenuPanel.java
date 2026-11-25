package com.cafemanagement.view;

import com.cafemanagement.dao.CategoryDAO;
import com.cafemanagement.dao.ProductDAO;
import com.cafemanagement.model.Category;
import com.cafemanagement.model.Product;
import com.cafemanagement.model.User;
import com.cafemanagement.util.FormatUtil;
import com.cafemanagement.util.ImageUtil;
import com.cafemanagement.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Quản lý thực đơn và danh mục món ăn (Nền trắng, Panel điều khiển thao tác chuẩn xác)
 */
public class MenuPanel extends JPanel {
    private final User currentUser;
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private JTable tblProducts;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbCategories;
    private JTextField txtSearch;
    private JLabel lblImagePreview;
    private JLabel lblPreviewName;
    private JLabel lblPreviewPrice;
    private List<Product> currentProducts;
    private List<Category> categoryList;

    public static final Color BROWN_PRIMARY = new Color(74, 44, 17);

    public MenuPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE); // Toàn bộ nền màu trắng
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadCategories();
        loadProducts();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Menu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(40, 40, 40));
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Action Buttons
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
            int selected = tblProducts.getSelectedRow();
            if (selected >= 0 && currentProducts != null) {
                showAddEditDialog(currentProducts.get(selected));
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một món trong bảng để sửa!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnDelete = new JButton("Delete", ImageUtil.getButtonIcon("icon_delete.png", 16));
        btnDelete.setBackground(new Color(192, 57, 43)); // Đỏ
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deleteSelectedProduct());

        if (currentUser.isAdmin()) {
            actionPanel.add(btnAdd);
            actionPanel.add(btnEdit);
            actionPanel.add(btnDelete);
        }

        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center: Filter Bar + Table + Image Preview Sidebar
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setBackground(Color.WHITE);

        filterBar.add(new JLabel("Phân loại:"));
        cbCategories = new JComboBox<>();
        cbCategories.setPreferredSize(new Dimension(200, 36));
        cbCategories.addActionListener(e -> filterByCategory());
        filterBar.add(cbCategories);

        filterBar.add(new JLabel("Tìm kiếm món:"));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(220, 36));
        filterBar.add(txtSearch);

        JButton btnSearch = new JButton("Tìm", ImageUtil.getButtonIcon("icon_search.png", 15));
        btnSearch.setBackground(BROWN_PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> searchProducts());
        filterBar.add(btnSearch);

        JButton btnReset = new JButton("Tất cả");
        btnReset.setBackground(new Color(240, 240, 240));
        btnReset.addActionListener(e -> loadProducts());
        filterBar.add(btnReset);

        centerPanel.add(filterBar, BorderLayout.NORTH);

        // Split view: Table (Left/Center) and Image Preview Card (Right)
        JPanel contentGrid = new JPanel(new BorderLayout(15, 0));
        contentGrid.setBackground(Color.WHITE);

        String[] cols = {"Mã ID", "Loại", "Tên Món / Đồ Uống", "Đơn Giá", "Mô Tả"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblProducts = new JTable(tableModel);
        tblProducts.setRowHeight(34);
        tblProducts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblProducts.getTableHeader().setBackground(new Color(245, 245, 247));
        tblProducts.setBackground(Color.WHITE);
        tblProducts.setShowGrid(true);
        tblProducts.setGridColor(new Color(235, 235, 235));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblProducts.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        contentGrid.add(scrollPane, BorderLayout.CENTER);

        // Sidebar xem trước hình ảnh món ăn
        JPanel previewPanel = new JPanel(new BorderLayout(0, 10));
        previewPanel.setPreferredSize(new Dimension(240, 400));
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblPreviewTitle = new JLabel("HÌNH ẢNH MÓN", SwingConstants.CENTER);
        lblPreviewTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPreviewTitle.setForeground(new Color(90, 90, 90));
        previewPanel.add(lblPreviewTitle, BorderLayout.NORTH);

        lblImagePreview = new JLabel(ImageUtil.getImage("cafe_den.png", 180, 180), SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(190, 190));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        previewPanel.add(lblImagePreview, BorderLayout.CENTER);

        JPanel previewInfo = new JPanel(new GridLayout(2, 1, 0, 4));
        previewInfo.setBackground(Color.WHITE);
        lblPreviewName = new JLabel("Cà phê đen đá", SwingConstants.CENTER);
        lblPreviewName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPreviewName.setForeground(BROWN_PRIMARY);

        lblPreviewPrice = new JLabel("25,000 VNĐ", SwingConstants.CENTER);
        lblPreviewPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPreviewPrice.setForeground(new Color(46, 125, 50));

        previewInfo.add(lblPreviewName);
        previewInfo.add(lblPreviewPrice);
        previewPanel.add(previewInfo, BorderLayout.SOUTH);

        contentGrid.add(previewPanel, BorderLayout.EAST);
        centerPanel.add(contentGrid, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Lắng nghe sự kiện click chọn dòng trong bảng để cập nhật hình ảnh xem trước
        tblProducts.getSelectionModel().addListSelectionListener(e -> {
            int selected = tblProducts.getSelectedRow();
            if (selected >= 0 && currentProducts != null && selected < currentProducts.size()) {
                Product p = currentProducts.get(selected);
                lblPreviewName.setText(p.getName());
                lblPreviewPrice.setText(FormatUtil.formatCurrency(p.getPrice()));
                String imgPath = p.getImagePath();
                if (imgPath == null || imgPath.trim().isEmpty()) {
                    imgPath = "default_dish.png";
                }
                lblImagePreview.setIcon(ImageUtil.getImage(imgPath, 180, 180));
            }
        });
    }

    private void loadCategories() {
        categoryList = categoryDAO.getAllCategories();
        cbCategories.removeAllItems();
        cbCategories.addItem("-- Tất cả danh mục --");
        for (Category c : categoryList) {
            cbCategories.addItem(c.getName());
        }
    }

    public void loadProducts() {
        currentProducts = productDAO.getAllProducts();
        renderTable(currentProducts);
    }

    private void filterByCategory() {
        int idx = cbCategories.getSelectedIndex();
        if (idx <= 0) {
            loadProducts();
        } else if (categoryList != null && idx - 1 < categoryList.size()) {
            int catId = categoryList.get(idx - 1).getId();
            currentProducts = productDAO.getProductsByCategory(catId);
            renderTable(currentProducts);
        }
    }

    private void searchProducts() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) {
            loadProducts();
        } else {
            currentProducts = productDAO.search(kw);
            renderTable(currentProducts);
        }
    }

    private void renderTable(List<Product> list) {
        tableModel.setRowCount(0);
        if (list != null && !list.isEmpty()) {
            for (Product p : list) {
                tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getCategoryName() != null ? p.getCategoryName() : "Khác",
                        p.getName(),
                        FormatUtil.formatCurrency(p.getPrice()),
                        p.getDescription()
                });
            }
            tblProducts.setRowSelectionInterval(0, 0);
        }
    }

    private void deleteSelectedProduct() {
        int selected = tblProducts.getSelectedRow();
        if (selected < 0 || currentProducts == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần xóa!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Product p = currentProducts.get(selected);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa món: " + p.getName() + " khỏi thực đơn?",
                "Xác nhận xóa món", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(p.getId())) {
                JOptionPane.showMessageDialog(this, "Đã xóa món thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa món này do đã có đơn hàng liên kết!", "Lỗi Xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Hộp thoại Thêm / Sửa món ăn với Kiểm lỗi nhập liệu (Validation) và Chọn ảnh từ thư mục images/
     */
    private void showAddEditDialog(Product product) {
        boolean isEdit = (product != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Chỉnh sửa thông tin món" : "Thêm món mới vào thực đơn", true);
        dialog.setSize(520, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 15));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 30, 20, 30));

        JTextField txtName = new JTextField(isEdit ? product.getName() : "");
        JComboBox<Category> cbCat = new JComboBox<>();
        for (Category c : categoryList) cbCat.addItem(c);
        if (isEdit && product.getCategoryId() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getId() == product.getCategoryId()) {
                    cbCat.setSelectedIndex(i);
                    break;
                }
            }
        }

        JTextField txtPrice = new JTextField(isEdit ? String.valueOf((long) product.getPrice()) : "");
        JTextArea txtDesc = new JTextArea(isEdit ? product.getDescription() : "", 3, 20);
        txtDesc.setLineWrap(true);

        // Panel chọn ảnh từ thư mục images/
        JPanel imgChooserPanel = new JPanel(new BorderLayout(5, 0));
        imgChooserPanel.setBackground(Color.WHITE);
        JTextField txtImg = new JTextField(isEdit && product.getImagePath() != null ? product.getImagePath() : "images/cafe_den.png");
        JButton btnBrowse = new JButton("📁 Chọn...");
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(new File("images"));
            int res = fc.showOpenDialog(dialog);
            if (res == JFileChooser.APPROVE_OPTION) {
                File chosen = fc.getSelectedFile();
                txtImg.setText("images/" + chosen.getName());
            }
        });
        imgChooserPanel.add(txtImg, BorderLayout.CENTER);
        imgChooserPanel.add(btnBrowse, BorderLayout.EAST);

        form.add(new JLabel("Tên món / đồ uống (*):"));
        form.add(txtName);
        form.add(new JLabel("Danh mục (*):"));
        form.add(cbCat);
        form.add(new JLabel("Đơn giá VNĐ (Số dương > 0) (*):"));
        form.add(txtPrice);
        form.add(new JLabel("Tệp ảnh (trong thư mục images/):"));
        form.add(imgChooserPanel);
        form.add(new JLabel("Mô tả chi tiết:"));
        form.add(new JScrollPane(txtDesc));

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        JButton btnSave = new JButton(isEdit ? "Lưu thay đổi" : "Thêm vào Menu", ImageUtil.getButtonIcon("icon_add.png", 16));
        btnSave.setBackground(BROWN_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String priceStr = txtPrice.getText().trim();

            // 1. KIỂM LỖI NHẬP LIỆU (VALIDATION)
            if (!ValidationUtil.requireNonEmpty(name, "Tên món / đồ uống", dialog)) {
                txtName.requestFocus();
                return;
            }

            // 2. Bẫy lỗi giá tiền: Bắt buộc phải là số dương lớn hơn 0
            Double price = ValidationUtil.parsePositiveDouble(priceStr, "Đơn giá", dialog);
            if (price == null) {
                txtPrice.requestFocus();
                return;
            }

            Category selectedCat = (Category) cbCat.getSelectedItem();
            int catId = selectedCat != null ? selectedCat.getId() : 1;
            String imagePath = txtImg.getText().trim();
            if (imagePath.isEmpty()) {
                imagePath = "images/default_dish.png";
            }

            if (isEdit) {
                product.setName(name);
                product.setCategoryId(catId);
                product.setPrice(price);
                product.setDescription(txtDesc.getText().trim());
                product.setImagePath(imagePath);
                if (productDAO.updateProduct(product)) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thông tin món thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi cập nhật CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Product newP = new Product(0, catId, name, txtDesc.getText().trim(), price, imagePath);
                if (productDAO.addProduct(newP)) {
                    JOptionPane.showMessageDialog(dialog, "Thêm món vào Menu thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi thêm món vào CSDL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
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
