package com.cafemanagement.util;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Tiện ích kiểm soát và bẫy lỗi nhập liệu (Validation)
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_10_DIGITS = Pattern.compile("^[0-9]{10}$");

    /**
     * Bắt lỗi bỏ trống dữ liệu
     */
    public static boolean requireNonEmpty(String value, String fieldName, Component parent) {
        if (value == null || value.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Trường [" + fieldName + "] không được để trống!",
                    "Cảnh Báo Nhập Liệu",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Bẫy lỗi định dạng Email (Phải có ký tự @ và tên miền hợp lệ)
     */
    public static boolean validateEmail(String email, Component parent) {
        if (!requireNonEmpty(email, "Email", parent)) {
            return false;
        }
        String cleanEmail = email.trim();
        if (!cleanEmail.contains("@") || !EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            JOptionPane.showMessageDialog(parent,
                    "Email [" + cleanEmail + "] không đúng định dạng!\n\n" +
                    "Yêu cầu:\n" +
                    "- Bắt buộc phải có ký tự '@'\n" +
                    "- Phải có tên miền hợp lệ (Ví dụ: tenban@gmail.com, nhanvien@cafe.com)",
                    "Lỗi Định Dạng Email",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Bẫy lỗi định dạng Số điện thoại (Bắt buộc phải đủ đúng 10 số)
     */
    public static boolean validatePhone(String phone, Component parent) {
        if (!requireNonEmpty(phone, "Số điện thoại", parent)) {
            return false;
        }
        String cleanPhone = phone.trim().replaceAll("\\s+", "");
        if (!PHONE_10_DIGITS.matcher(cleanPhone).matches()) {
            JOptionPane.showMessageDialog(parent,
                    "Số điện thoại [" + cleanPhone + "] không hợp lệ!\n\n" +
                    "Yêu cầu:\n" +
                    "- Bắt buộc phải đủ đúng 10 chữ số\n" +
                    "- Chỉ chứa các chữ số từ 0 đến 9\n" +
                    "- Độ dài hiện tại: " + cleanPhone.length() + " ký tự.",
                    "Lỗi Định Dạng Số Điện Thoại",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Bẫy lỗi giá tiền (Bắt buộc phải là số dương lớn hơn 0)
     */
    public static Double parsePositiveDouble(String value, String fieldName, Component parent) {
        if (!requireNonEmpty(value, fieldName, parent)) {
            return null;
        }
        try {
            double num = Double.parseDouble(value.trim());
            if (num <= 0) {
                JOptionPane.showMessageDialog(parent,
                        "Trường [" + fieldName + "] phải là số dương lớn hơn 0!\n" +
                        "Giá trị bạn vừa nhập: " + num,
                        "Lỗi Giá Trị Số Âm / Bằng 0",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return num;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent,
                    "Trường [" + fieldName + "] phải là định dạng số hợp lệ (không chứa ký tự chữ cái)!",
                    "Lỗi Định Dạng Số",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    /**
     * Bẫy lỗi số lượng (Phải là số nguyên dương > 0)
     */
    public static Integer parsePositiveInt(String value, String fieldName, Component parent) {
        if (!requireNonEmpty(value, fieldName, parent)) {
            return null;
        }
        try {
            int num = Integer.parseInt(value.trim());
            if (num <= 0) {
                JOptionPane.showMessageDialog(parent,
                        "Trường [" + fieldName + "] phải là số nguyên dương lớn hơn 0!\n" +
                        "Giá trị bạn vừa nhập: " + num,
                        "Lỗi Số Lượng",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return num;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent,
                    "Trường [" + fieldName + "] phải là số nguyên hợp lệ!",
                    "Lỗi Số Nguyên",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
}
