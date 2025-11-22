package main.models;

import java.io.Serializable;

// User class represents a registered user
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L; // Recommended for Serializable
    private String username;
    private String displayName;
    private String password; // New field for password
    private int totalLikes;
    
    public User(String username, String displayName, String password) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;
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
    
    // Verify password
    public boolean checkPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
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
