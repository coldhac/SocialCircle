package main.data;

import main.models.*;
import java.io.*;
import java.util.*;

// Data Manager - Singleton Pattern
public class DataManager {
    
    private static DataManager instance;
    
    private List<Post> allPosts;
    private Map<String, User> allUsers;
    private User currentUser; // The user currently logged in
    
    private static final String DATA_FILE = "data.ser";
    
    private DataManager() {
        allPosts = new ArrayList<>();
        allUsers = new HashMap<>();
        // Do not set currentUser here anymore, wait for login
        initUsers(); 
    }
    
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    // Initialize default users with password "123456"
    private void initUsers() {
        if (allUsers.isEmpty()) {
            registerUser("alice", "123456", "Alice");
            registerUser("bob", "123456", "Bob");
            registerUser("charlie", "123456", "Charlie");
        }
    }

    // New: Register a new user
    public boolean registerUser(String username, String password, String displayName) {
        if (allUsers.containsKey(username)) {
            return false; // Username already exists
        }
        User newUser = new User(username, displayName, password);
        allUsers.put(username, newUser);
        return true;
    }

    // New: Login logic
    public boolean login(String username, String password) {
        User user = allUsers.get(username);
        if (user != null && user.checkPassword(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    // New: Logout logic
    public void logout() {
        this.currentUser = null;
    }
    
    public void addPost(Post post) {
        allPosts.add(0, post); 
    }
    
    public void removePost(Post post) {
        allPosts.remove(post);
    }
    
    public List<Post> getAllPosts() {
        return allPosts;
    }
    
    public List<Post> getUserPosts(User user) {
        List<Post> userPosts = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getAuthor().equals(user)) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }
    
    public List<Post> searchPosts(String keyword) {
        List<Post> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        
        for (Post post : allPosts) {
            if (post.getContent().toLowerCase().contains(lower)) {
                results.add(post);
                continue;
            }
            if (post.getAuthor().getUsername().toLowerCase().contains(lower) ||
                post.getAuthor().getDisplayName().toLowerCase().contains(lower)) {
                results.add(post);
            }
        }
        return results;
    }
    
    public List<Post> sortByTime() {
        List<Post> sorted = new ArrayList<>(allPosts);
        sorted.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
        return sorted;
    }
    
    public List<Post> sortByLikes() {
        List<Post> sorted = new ArrayList<>(allPosts);
        sorted.sort((p1, p2) -> Integer.compare(p2.getLikeCount(), p1.getLikeCount()));
        return sorted;
    }
    
    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(allUsers);
            out.writeObject(allPosts);
            // No need to save currentUser state for next launch, force login
            System.out.println("Data saved successfully.");
        } catch (Exception e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No saved data found.");
            return;
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            allUsers = (Map<String, User>) in.readObject();
            allPosts = (List<Post>) in.readObject();
            // Do not load currentUser, user must login again
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load data: " + e.getMessage());
            // If load fails (e.g. class changed), ensure default users exist
            initUsers();
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public User getUser(String username) {
        return allUsers.get(username);
    }
    
    public Collection<User> getAllUsers() {
        return allUsers.values();
    }
}
