package main.data;

import main.models.*;
import java.io.*;
import java.util.*;

// 数据管理器 - 单例模式
public class DataManager {
    
    private static DataManager instance;
    
    private List<Post> allPosts;
    private Map<String, User> allUsers;
    private User currentUser;
    
    private static final String DATA_FILE = "data.ser";
    
    private DataManager() {
        allPosts = new ArrayList<>();
        allUsers = new HashMap<>();
        initUsers();
    }
    
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    // 初始化默认用户
    private void initUsers() {
        User alice = new User("alice", "Alice");
        User bob = new User("bob", "Bob");
        User charlie = new User("charlie", "Charlie");
        
        allUsers.put("alice", alice);
        allUsers.put("bob", bob);
        allUsers.put("charlie", charlie);
        
        currentUser = alice;
    }
    
    // 添加帖子
    public void addPost(Post post) {
        allPosts.add(0, post);  // 添加到开头
    }
    
    // 删除帖子
    public void removePost(Post post) {
        allPosts.remove(post);
    }
    
    // 获取所有帖子
    public List<Post> getAllPosts() {
        return allPosts;
    }
    
    // 获取用户的帖子
    public List<Post> getUserPosts(User user) {
        List<Post> userPosts = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getAuthor().equals(user)) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }
    
    // 简单搜索 - 遍历所有帖子
    public List<Post> searchPosts(String keyword) {
        List<Post> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        
        for (Post post : allPosts) {
            // 搜索内容
            if (post.getContent().toLowerCase().contains(lower)) {
                results.add(post);
                continue;
            }
            // 搜索用户名
            if (post.getAuthor().getUsername().toLowerCase().contains(lower) ||
                post.getAuthor().getDisplayName().toLowerCase().contains(lower)) {
                results.add(post);
            }
        }
        return results;
    }
    
    // 按时间排序
    public List<Post> sortByTime() {
        List<Post> sorted = new ArrayList<>(allPosts);
        sorted.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
        return sorted;
    }
    
    // 按点赞数排序
    public List<Post> sortByLikes() {
        List<Post> sorted = new ArrayList<>(allPosts);
        sorted.sort((p1, p2) -> Integer.compare(p2.getLikeCount(), p1.getLikeCount()));
        return sorted;
    }
    
    // 保存数据
    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(allUsers);
            out.writeObject(allPosts);
            out.writeObject(currentUser.getUsername());
            System.out.println("数据已保存");
        } catch (Exception e) {
            System.err.println("保存失败: " + e.getMessage());
        }
    }
    
    // 加载数据
    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("没有保存的数据");
            return;
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            allUsers = (Map<String, User>) in.readObject();
            allPosts = (List<Post>) in.readObject();
            String username = (String) in.readObject();
            currentUser = allUsers.get(username);
            System.out.println("数据已加载");
        } catch (Exception e) {
            System.err.println("加载失败: " + e.getMessage());
        }
    }
    
    // Getters
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
