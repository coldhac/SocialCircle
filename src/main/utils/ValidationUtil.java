package main.utils;

import java.io.File;

// 验证工具类
public class ValidationUtil {
    
    // 验证内容文本
    public static String validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Content cannot be empty";
        }
        if (content.length() > 500) {
            return "Content is too long (max 500 chars)";
        }
        return null;  // null 表示验证通过
    }
    
    // 验证图片文件
    public static String validateImage(String path) {
        if (path == null || path.isEmpty()) {
            return null;  // 图片是可选的
        }
        
        File file = new File(path);
        if (!file.exists()) {
            return "Image file does not exist";
        }
        
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".jpg") && !name.endsWith(".png") && !name.endsWith(".gif")) {
            return "Only JPG/PNG/GIF formats are supported";
        }
        
        return null;
    }
}