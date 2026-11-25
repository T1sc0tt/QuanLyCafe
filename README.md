# HỆ THỐNG QUẢN LÝ QUÁN CÀ PHÊ (COFFEE SHOP MANAGEMENT SYSTEM)

**Môi trường phát triển tối ưu: Apache NetBeans IDE 29 (hoặc NetBeans 12+) | Java Swing | MySQL 8.x | Maven**

---

CẤU TRÚC DỰ ÁN TRÊN NETBEANS

```text
CafeManagementSystem/
├── pom.xml                               # File cấu hình Maven (MySQL Connector 8.3 & FlatLaf UI)
├── nbactions.xml                         # Cấu hình phím tắt F6 (Run Project) trong NetBeans
├── images/                               # THƯ MỤC ẢNH MÓN ĂN & BIỂU TƯỢNG (Dễ dàng thay thế)
│   ├── cafe_den.png
│   ├── cafe_sua.png
│   ├── cappuccino.png
│   ├── latte.png
│   ├── tra_dao.png
│   ├── banh_tiramisu.png
│   └── icon_*.png
├── database/
│   └── cafe_db.sql                       # Toàn bộ mã nguồn tạo CSDL MySQL và dữ liệu mẫu
└── src/main/
    ├── resources/images/                 # Bản sao tài nguyên ảnh trong Classpath
    └── java/com/cafemanagement/
        ├── model/                        # Các Entity: User, Customer, Product, Order, OrderDetail...
        ├── dao/                          # Tầng DAO: UserDAO, CustomerDAO, ProductDAO, OrderDAO...
        ├── util/                         # Tiện ích:
        │   ├── DBConnection.java         # Kết nối MySQL qua JDBC
        │   ├── FormatUtil.java           # Định dạng tiền tệ VNĐ và thời gian
        │   ├── ImageUtil.java            # Tiện ích nạp ảnh thông minh & tự phục hồi
        │   └── ValidationUtil.java       # Bộ kiểm tra và bẫy lỗi nhập liệu
        └── view/                         # Giao diện Java Swing:
            ├── MainApp.java              # Lớp khởi chạy hệ thống (Main Entry)
            ├── LoginForm.java            # Màn hình đăng nhập (Panel Header Nâu, Form Trắng)
            ├── DashboardForm.java        # Khung sườn làm việc (Sidebar Nâu, Vùng làm việc Trắng)
            ├── DashboardPanel.java       # Tổng quan 4 thẻ chỉ số & đơn hàng mới
            ├── MenuPanel.java            # Quản lý thực đơn & Xem trước ảnh món ăn
            ├── CustomersPanel.java       # Quản lý khách hàng (Validation SĐT 10 số & Email)
            ├── OrdersPanel.java          # Bán hàng POS, giỏ hàng, tính tiền & in phiếu hóa đơn
            └── UsersPanel.java           # Quản lý tài khoản & phân quyền (Chỉ dành cho Admin)
```

---

HƯỚNG DẪN CÀI ĐẶT & CHẠY TRÊN APACHE NETBEANS IDE 29

### Bước 1: Khởi động Cơ sở dữ liệu (MySQL / XAMPP)
1. Mở phần mềm **XAMPP Control Panel**, nhấn **Start** cho 2 dịch vụ **Apache** và **MySQL**.
2. Mở trình duyệt web truy cập địa chỉ: `http://localhost/phpmyadmin/`.
3. Tạo mới một cơ sở dữ liệu có tên chính xác là: `cafe_db`.
4. Chọn cơ sở dữ liệu `cafe_db`, nhấn vào tab **Import** (Nhập).
5. Chọn tệp `database/cafe_db.sql` trong thư mục dự án và bấm nút **Go** (Thực hiện). Hệ thống sẽ tự động tạo đủ 6 bảng kèm dữ liệu thực đơn có liên kết ảnh mẫu.

### Bước 2: Mở dự án trong NetBeans IDE 29
1. Khởi động **Apache NetBeans IDE 29**.
2. Trên thanh menu, chọn: **File** -> **Open Project...** (phím tắt `Ctrl + Shift + O`).
3. Điều hướng tới thư mục giải nén và chọn thư mục **`CafeManagementSystem`** (thư mục có biểu tượng hộp vuông Maven màu vàng).
4. Nhấn nút **Open Project**. NetBeans sẽ tự động nhận diện và đồng bộ các thư viện Maven.

### Bước 3: Chạy ứng dụng
- Nhấn phím **F6** (hoặc nhấn nút tam giác màu xanh lá **Run Project** trên thanh công cụ).
- Cửa sổ đăng nhập sẽ hiển thị.

---
