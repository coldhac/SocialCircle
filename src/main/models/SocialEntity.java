package main.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 抽象类 - 帖子和评论的基类
public abstract class SocialEntity implements Serializable {
    
    protected String id;
    protected User author;
    protected LocalDateTime timestamp;
    protected String content;
    
    public SocialEntity(User author, String content) {
        this.id = "ID_" + System.currentTimeMillis();
        this.author = author;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
    
    // 抽象方法，子类要实现
    public abstract String getType();
    
    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return timestamp.format(formatter);
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
