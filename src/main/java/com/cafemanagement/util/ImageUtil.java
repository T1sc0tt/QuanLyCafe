package com.cafemanagement.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/**
 * Tiện ích quản lý và tải hình ảnh món ăn, biểu tượng nút bấm
 */
public class ImageUtil {

    private static final String DEFAULT_IMAGE_PATH = "images/";

    /**
     * Tải ImageIcon từ thư mục images/ hoặc tài nguyên dự án (Classpath)
     */
    public static ImageIcon getImage(String filename, int width, int height) {
        if (filename == null || filename.trim().isEmpty()) {
            return createPlaceholderIcon(width, height, "☕");
        }

        try {
            // 1. Thử tìm từ thư mục cục bộ (File System)
            File file = new File(filename);
            if (!file.exists()) {
                file = new File(DEFAULT_IMAGE_PATH + filename);
            }
            if (file.exists()) {
                ImageIcon original = new ImageIcon(file.getAbsolutePath());
                return scale(original, width, height);
            }

            // 2. Thử tìm từ Classpath (Resources)
            String resPath = filename.startsWith("/") ? filename : ("/images/" + filename);
            URL url = ImageUtil.class.getResource(resPath);
            if (url != null) {
                ImageIcon original = new ImageIcon(url);
                return scale(original, width, height);
            }
        } catch (Exception ignored) {
        }

        // 3. Dự phòng hình ảnh nếu không tìm thấy tệp
        return createPlaceholderIcon(width, height, "☕");
    }

    /**
     * Tải icon cho các nút bấm (Sidebar, Action buttons)
     */
    public static ImageIcon getButtonIcon(String iconName, int size) {
        return getImage(iconName, size, size);
    }

    private static ImageIcon scale(ImageIcon original, int w, int h) {
        if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
            return createPlaceholderIcon(w, h, "☕");
        }
        Image scaled = original.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Tạo biểu tượng tạm thời đẹp mắt khi chưa có tệp ảnh
     */
    public static ImageIcon createPlaceholderIcon(int w, int h, String symbol) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Nền nâu cà phê ấm áp
        g2.setColor(new Color(74, 44, 17));
        g2.fillRoundRect(0, 0, w, h, 8, 8);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, Math.max(12, w / 2)));
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(symbol)) / 2;
        int y = ((h - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(symbol, x, y);

        g2.dispose();
        return new ImageIcon(img);
    }
}
