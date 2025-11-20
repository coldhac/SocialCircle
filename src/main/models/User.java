package main.models;

import java.io.Serializable;

// 用户类
public class User implements Serializable {
    
    private String username;
    private String displayName;
    private int totalLikes;
    
    public User(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
        this.totalLikes = 0;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getTotalLikes() {
        return totalLikes;
    }
    
    public void addLike() {
        totalLikes++;
    }
    
    public void removeLike() {
        if (totalLikes > 0) {
            totalLikes--;
        }
    }
    
    @Override
    public String toString() {
        return displayName + " (@" + username + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }
    
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
