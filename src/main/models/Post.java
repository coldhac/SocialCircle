package main.models;

import java.io.Serializable;
import java.util.*;

// 帖子类 - 继承SocialEntity，实现Likeable接口
public class Post extends SocialEntity implements Likeable, Serializable {
    
    private String imagePath;
    private Set<String> likedUsers;  // 使用Set存储点赞用户，避免重复
    private List<Comment> comments;   // 使用List存储评论
    
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
    
    // 实现Likeable接口的方法
    @Override
    public boolean toggleLike(String username) {
        if (likedUsers.contains(username)) {
            likedUsers.remove(username);
            author.removeLike();
            return false;
        } else {
            likedUsers.add(username);
            author.addLike();
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
    
    // 评论相关方法
    public void addComment(Comment comment) {
        comments.add(comment);
    }
    
    public boolean removeComment(Comment comment) {
        return comments.remove(comment);
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public int getCommentCount() {
        return comments.size();
    }
    
    // Getters
    public String getImagePath() {
        return imagePath;
    }
    
    public Set<String> getLikedUsers() {
        return likedUsers;
    }
}
