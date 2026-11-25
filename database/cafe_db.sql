-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 19, 2026 at 07:10 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cafe_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `description`) VALUES
(1, 'Cà Phê Truyền Thống', 'Các dòng cà phê pha phin truyền thống Việt Nam đậm đà'),
(2, 'Cà Phê Ý & Pha Máy', 'Espresso, Cappuccino, Latte, Americano hảo hạng'),
(3, 'Trà & Trà Trái Cây', 'Trà đào, trà vải, trà lài thanh nhiệt'),
(4, 'Bánh Ngọt & Tráng Miệng', 'Bánh Tiramisu, Croissant, Mousse chanh dây');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `orders_count` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `name`, `email`, `phone`, `address`, `orders_count`) VALUES
(1, 'Nguyễn Đức Anh', 'ducanh@gmail.com', '0933112233', '12 Phương Liệt, Thanh Xuân, Hà Nội', 5),
(2, 'Trần Hải Đăng', 'Haidang@gmail.com', '0988776655', '15 Hoàn Kiếm, Tây Hồ, Hà Nội', 3),
(3, 'Pham Văn Trường Anh', 'Truonganh@gmail.com', '0911223344', '1 Ba Đình, Hà Nội', 1),
(4, 'Đào Gia Thế Vũ', 'TheVu@gmail.com', '0909090909', 'Triều Đông, Thượng Phúc, Hà Nội', 12),
(5, 'Nguyễn Hà Duy Anh', 'duyanh@gmail.com', '0336106980', 'Phúc Trại,Thượng Phúc, Hà Nội', 2);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `total` decimal(10,2) NOT NULL DEFAULT 0.00,
  `status` varchar(50) NOT NULL DEFAULT 'Pending',
  `order_date` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `customer_id`, `user_id`, `total`, `status`, `order_date`) VALUES
(1001, 1, 2, 74000.00, 'Completed', '2026-11-18 08:30:00'),
(1002, 2, 2, 45000.00, 'Completed', '2026-11-18 09:15:00'),
(1003, 3, 2, 82000.00, 'Processing', '2026-11-18 10:00:00'),
(1004, 4, 1, 29000.00, 'Pending', '2026-11-18 10:20:00'),
(1005, 5, 1, 259000.00, 'Completed', '2026-11-18 17:44:16'),
(1006, NULL, 1, 35000.00, 'Completed', '2026-11-18 17:44:53'),
(1007, 5, 1, 70000.00, 'Processing', '2026-11-18 18:03:20');

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

CREATE TABLE `order_details` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `unit_price` decimal(10,2) NOT NULL DEFAULT 0.00,
  `line_total` decimal(10,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`id`, `order_id`, `product_id`, `quantity`, `unit_price`, `line_total`) VALUES
(1, 1001, 2, 1, 29000.00, 29000.00),
(2, 1001, 3, 1, 45000.00, 45000.00),
(3, 1002, 3, 1, 45000.00, 45000.00),
(6, 1004, 2, 1, 29000.00, 29000.00),
(7, 1005, 4, 3, 48000.00, 144000.00),
(8, 1005, 3, 2, 45000.00, 90000.00),
(9, 1005, 1, 1, 25000.00, 25000.00),
(10, 1006, 5, 1, 35000.00, 35000.00),
(11, 1007, 5, 2, 35000.00, 70000.00);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT 0.00,
  `image_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `category_id`, `name`, `description`, `price`, `image_path`) VALUES
(1, 1, 'Cà Phê Đen Đá', 'Cà phê Robusta Buôn Ma Thuột đậm đà, vị đắng dịu', 25000.00, 'images/cafe_den.png'),
(2, 1, 'Cà Phê Sữa Đá', 'Cà phê nguyên chất kết hợp sữa đặc ngọt béo thơm lừng', 29000.00, 'images/Screenshot 2026-11-18 171138.png'),
(3, 2, 'Cappuccino Ý', 'Lớp bọt sữa mịn màng vẽ latte art nghệ thuật', 45000.00, 'images/cappuccino.png'),
(4, 2, 'Latte Kem Béo', 'Chiết xuất từ hạt Arabica Cầu Đất nguyên chất hòa quyện sữa tươi', 48000.00, 'images/latte.png'),
(5, 2, 'Macchiato', 'Caffè macchiato là một loại espresso với một lượng nhỏ sữa, thường được tạo bọt', 35000.00, 'images/macchiato.png');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` varchar(50) NOT NULL DEFAULT 'USER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `fullname`, `email`, `password`, `phone`, `role`) VALUES
(1, 'Administrator', 'admin@gmail.com', 'admin123', '0901234567', 'ADMIN'),
(2, 'Nguyễn Hà Duy Anh', 'duyanh@gmail.com', '1234', '0912345678', 'USER');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_order_cust` (`customer_id`),
  ADD KEY `fk_order_user` (`user_id`);

--
-- Indexes for table `order_details`
--
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_od_order` (`order_id`),
  ADD KEY `fk_od_prod` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_prod_cat` (`category_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1008;

--
-- AUTO_INCREMENT for table `order_details`
--
ALTER TABLE `order_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_order_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `order_details`
--
ALTER TABLE `order_details`
  ADD CONSTRAINT `fk_od_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_od_prod` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `fk_prod_cat` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
