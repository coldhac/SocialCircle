package main.data;

import main.models.*;
import main.common.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

// 数据管理器 - 客户端版本 (单例模式)
public class DataManager {
    
    private static DataManager instance;
    
    private User currentUser; // 当前登录用户
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private static final String SERVER_HOST = "10.0.0.73";
    private static final int SERVER_PORT = 8888;
    
    private DataManager() {
        connectToServer();
    }
    
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    // 连接到服务器
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            // 注意：先创建输出流，再创建输入流，否则会死锁
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server.");
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }
    
    // 发送请求并等待响应的通用方法
    private Response sendRequest(Request req) {
        if (socket == null || socket.isClosed()) {
            return new Response(false, "Not connected to server", null);
        }
        try {
            out.writeObject(req);
            out.flush();
            return (Response) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Network error: " + e.getMessage(), null);
        }
    }

    // 注册用户
    public boolean registerUser(String username, String password, String displayName) {
        User newUser = new User(username, displayName, password);
        Response res = sendRequest(new Request(RequestType.REGISTER, newUser));
        return res.isSuccess();
    }

    // 登录
    public boolean login(String username, String password) {
        Response res = sendRequest(new Request(RequestType.LOGIN, username, password));
        if (res.isSuccess()) {
            this.currentUser = (User) res.getPayload();
            return true;
        }
        return false;
    }

    // 登出
    public void logout() {
        this.currentUser = null;
    }
    
    // 发布帖子
    public void addPost(Post post) {
        sendRequest(new Request(RequestType.ADD_POST, post));
    }
    
    // 删除帖子
    public void removePost(Post post) {
        sendRequest(new Request(RequestType.DELETE_POST, post));
    }
    
    // 获取所有帖子 (从服务器获取)
    @SuppressWarnings("unchecked")
    public List<Post> getAllPosts() {
        Response res = sendRequest(new Request(RequestType.GET_ALL_POSTS, null));
        if (res.isSuccess() && res.getPayload() instanceof List) {
            return (List<Post>) res.getPayload();
        }
        return new ArrayList<>();
    }
    
    // 获取特定用户的帖子
    public List<Post> getUserPosts(User user) {
        List<Post> allPosts = getAllPosts();
        List<Post> userPosts = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getAuthor().getUsername().equals(user.getUsername())) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }
    
    // 搜索帖子 (在本地过滤服务器返回的数据)
    public List<Post> searchPosts(String keyword) {
        List<Post> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        
        for (Post post : getAllPosts()) {
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
    
    // 排序逻辑 (客户端处理)
    public List<Post> sortByTime() {
        // 服务器默认就是时间排序
        return getAllPosts();
    }
    
    public List<Post> sortByLikes() {
        List<Post> posts = getAllPosts();
        posts.sort((p1, p2) -> Integer.compare(p2.getLikeCount(), p1.getLikeCount()));
        return posts;
    }
    
    // 更新帖子状态（点赞/评论）
    public void updatePost(Post post, RequestType type) {
        sendRequest(new Request(type, post));
    }
    
    // 获取当前用户
    public User getCurrentUser() {
        return currentUser;
    }
    
    // 关闭连接
    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
