package main.utils;

import java.io.File;

// 简单的验证工具类
public class ValidationUtil {
    
    // 验证文本
    public static String validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "内容不能为空";
        }
        if (content.length() > 500) {
            return "内容太长了";
        }
        return null;  // null表示验证通过
    }
    
    // 验证图片
    public static String validateImage(String path) {
        if (path == null || path.isEmpty()) {
            return null;  // 图片是可选的
        }
        
        File file = new File(path);
        if (!file.exists()) {
            return "图片不存在";
        }
        
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".jpg") && !name.endsWith(".png") && !name.endsWith(".gif")) {
            return "只支持JPG/PNG/GIF格式";
        }
        
        return null;
    }
}
