-- ==========================================================
-- COFFEE SHOP MANAGEMENT SYSTEM (HE THONG QUAN LY QUAN CA FE)
-- Database: cafe_db
-- Compatibility: MySQL 5.7+ / 8.0+ / MariaDB (XAMPP / phpMyAdmin)
-- ==========================================================

DROP DATABASE IF EXISTS `cafe_db`;
CREATE DATABASE `cafe_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `cafe_db`;

-- 1. Table: users
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (  `id` INT NOT NULL AUTO_INCREMENT,  `fullname` VARCHAR(255) NOT NULL,  `email` VARCHAR(255) NOT NULL UNIQUE,  `password` VARCHAR(255) NOT NULL,  `phone` VARCHAR(20) DEFAULT NULL,  `role` VARCHAR(50) NOT NULL DEFAULT 'USER',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table: categories
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (  `id` INT NOT NULL AUTO_INCREMENT,  `name` VARCHAR(100) NOT NULL UNIQUE,  `description` TEXT DEFAULT NULL,  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table: customers
DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers` (  `id` INT NOT NULL AUTO_INCREMENT,  `name` VARCHAR(255) NOT NULL,  `email` VARCHAR(255) DEFAULT NULL,  `phone` VARCHAR(20) DEFAULT NULL,  `address` TEXT DEFAULT NULL,  `orders_count` INT NOT NULL DEFAULT 0,  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Table: products
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (  `id` INT NOT NULL AUTO_INCREMENT,  `category_id` INT DEFAULT NULL,  `name` VARCHAR(255) NOT NULL,  `description` TEXT DEFAULT NULL,  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,  `image_path` VARCHAR(255) DEFAULT NULL,  PRIMARY KEY (`id`),  KEY `fk_prod_cat` (`category_id`),  CONSTRAINT `fk_prod_cat` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Table: orders
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (  `id` INT NOT NULL AUTO_INCREMENT,  `customer_id` INT DEFAULT NULL,  `user_id` INT DEFAULT NULL,  `total` DECIMAL(10,2) NOT NULL DEFAULT 0.00,  `status` VARCHAR(50) NOT NULL DEFAULT 'Pending',  `order_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  PRIMARY KEY (`id`),  KEY `fk_order_cust` (`customer_id`),  KEY `fk_order_user` (`user_id`),  CONSTRAINT `fk_order_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Table: order_details
DROP TABLE IF EXISTS `order_details`;
CREATE TABLE `order_details` (  `id` INT NOT NULL AUTO_INCREMENT,  `order_id` INT NOT NULL,  `product_id` INT NOT NULL,  `quantity` INT NOT NULL DEFAULT 1,  `unit_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,  `line_total` DECIMAL(10,2) NOT NULL DEFAULT 0.00,  PRIMARY KEY (`id`),  KEY `fk_od_order` (`order_id`),  KEY `fk_od_prod` (`product_id`),  CONSTRAINT `fk_od_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,  CONSTRAINT `fk_od_prod` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample Seed Data
INSERT INTO `users` (`id`, `fullname`, `email`, `password`, `phone`, `role`) VALUES
(1, 'Administrator', 'admin@gmail.com', 'admin123', '0901234567', 'ADMIN'),
(2, 'Nguyễn Hà Duy Anh', 'duyanh@gmail.com', '12345', '0336106980', 'ADMIN'),
(3, 'Phạm Văn Trường Anh', 'truonganh@gmail.com', '1234', '0879789879', 'USER');

INSERT INTO `categories` (`id`, `name`, `description`) VALUES
(1, 'Cà Phê Truyền Thống', 'Các dòng cà phê pha phin truyền thống Việt Nam đậm đà'),
(2, 'Cà Phê Ý & Pha Máy', 'Espresso, Cappuccino, Latte, Americano hảo hạng'),
(3, 'Trà & Trà Trái Cây', 'Trà đào, trà vải, trà lài thanh nhiệt'),
(4, 'Bánh Ngọt & Tráng Miệng', 'Bánh Tiramisu, Croissant, Mousse chanh dây');

INSERT INTO `customers` (`id`, `name`, `email`, `phone`, `address`, `orders_count`) VALUES
(1, 'Nguyễn Hà Duy Anh', 'duyanh09112006@gmail.com', '0336106980', 'Phúc Trại, Thượng Phúc, Hà Nội', 5),
(2, 'Đào Gia Thế Vũ', 'thevu@gmail.com', '0988776655', 'Triều Đông, Thượng Phúc, Hà Nội', 3),
(3, 'Nguyễn Đức Anh', 'ducanh@gmail.com', '0911223344', '12 Phương Liệt, Thanh Xuân Hà Nội', 1),
(4, 'Nguyen Huu Bac', 'Huubac@gmail.com', '0909090909', 'Tai quan', 12);

INSERT INTO `products` (`id`, `category_id`, `name`, `description`, `price`, `image_path`) VALUES
(1, 1, 'Cà Phê Đen Đá', 'Cà phê Robusta Buôn Ma Thuột đậm đà, vị đắng dịu', 25000.00, 'images/cafe_den.png'),
(2, 1, 'Cà Phê Sữa Đá', 'Cà phê nguyên chất kết hợp sữa đặc ngọt béo thơm lừng', 29000.00, 'images/Screenshot 2026-11-18 171138.png'),
(3, 2, 'Cappuccino Ý', 'Lớp bọt sữa mịn màng vẽ latte art nghệ thuật', 45000.00, 'images/cappuccino.png'),
(4, 2, 'Latte Kem Béo', 'Chiết xuất từ hạt Arabica Cầu Đất nguyên chất hòa quyện sữa tươi', 48000.00, 'images/latte.png'),
(5, 3, 'Trà Đào Cam Sả', 'Trà thanh mát kết hợp lát đào giòn và sả thơm', 42000.00, 'images/traodaocamxa.png'),
(6, 3, 'Trà Vải Hoa Hồng', 'Hương hoa thơm ngát cùng trái vải mọng nước tươi mát', 40000.00, 'images/travai.png'),
(7, 3, 'Matcha Đá Xay', 'Bột trà xanh Nhật Bản xay cùng sữa tươi và kem whipping', 50000.00, 'images/matchadaxay.png'),
(8, 4, 'Bánh Tiramisu Ý', 'Bánh tiramisu cacao phủ bột mịn tan chảy trong miệng', 38000.00, 'images/tiramisu.png'),
(9, 4, 'Bánh Sừng Bò Croissant', 'Bánh sừng bò nướng giòn tan ngập hương bơ thơm phức', 30000.00, 'images/banhsungbo.png'),
(11, 2, 'Espresso', 'Espresso là thức uống cà phê độc đáo với hương vị mạnh mẽ, bùng nổ, đại diện cho văn hóa cà phê Ý hiện đại và đã chinh phục khẩu vị cà phê của người yêu thích cà phê trên toàn thế giới.', 45000.00, 'images/espresso.png');


INSERT INTO `orders` (`id`, `customer_id`, `user_id`, `total`, `status`, `order_date`) VALUES
(1001, 1, 2, 74000.00, 'Completed', '2026-11-18 08:30:00'),
(1002, 2, 2, 45000.00, 'Completed', '2026-11-18 09:15:00'),
(1003, 3, 2, 82000.00, 'Processing', '2026-11-18 10:00:00'),
(1004, 4, 1, 29000.00, 'Pending', '2026-11-18 10:20:00');

INSERT INTO `order_details` (`id`, `order_id`, `product_id`, `quantity`, `unit_price`, `line_total`) VALUES
(1, 1001, 2, 1, 29000.00, 29000.00),
(2, 1001, 3, 1, 45000.00, 45000.00),
(3, 1002, 3, 1, 45000.00, 45000.00),
(4, 1003, 5, 1, 42000.00, 42000.00),
(5, 1003, 6, 1, 40000.00, 40000.00),
(6, 1004, 2, 1, 29000.00, 29000.00);