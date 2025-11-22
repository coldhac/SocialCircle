package main.models;

import java.io.Serializable;
import java.util.*;

// 帖子类 - 继承SocialEntity，实现Likeable接口
public class Post extends SocialEntity implements Likeable, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String imagePath; // 本地路径（仅用于显示文件名）
    private byte[] imageBytes; // 新增：图片的二进制数据，用于网络传输
    private Set<String> likedUsers; // 存储点赞用户名的集合
    private List<Comment> comments; // 评论列表
    
    public Post(User author, String content, String imagePath) {
        super(author, content);
        this.imagePath = imagePath;
        this.likedUsers = new HashSet<>();
        this.comments = new ArrayList<>();
    }
    
    @Override
    public String getType() {
        return "Post";
    }
    
    // 切换点赞状态
    @Override
    public boolean toggleLike(String username) {
        if (likedUsers.contains(username)) {
            likedUsers.remove(username);
            return false;
        } else {
            likedUsers.add(username);
            return true;
        }
    }
    
    @Override
    public int getLikeCount() {
        return likedUsers.size();
    }
    
    @Override
    public boolean isLikedBy(String username) {
        return likedUsers.contains(username);
    }
    
    // 添加评论
    public void addComment(Comment comment) {
        comments.add(comment);
    }
    
    // 删除评论
    public boolean removeComment(Comment comment) {
        return comments.remove(comment);
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public int getCommentCount() {
        return comments.size();
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    // 获取图片数据
    public byte[] getImageBytes() {
        return imageBytes;
    }

    // 设置图片数据
    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
    
    public Set<String> getLikedUsers() {
        return likedUsers;
    }
}
