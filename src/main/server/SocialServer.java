package main.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import main.models.*;
import main.common.*;

// 社交网络服务器主类
public class SocialServer {
    
    private static final int PORT = 8888;
    // 线程安全的集合用于存储数据
    private static Map<String, User> users = new ConcurrentHashMap<>();
    private static List<Post> posts = Collections.synchronizedList(new ArrayList<>());
    private static final String DATA_FILE = "server_data.ser";

    public static void main(String[] args) {
        loadData(); // 启动时加载数据
        System.out.println("Server started on port " + PORT);
        
        // 如果没有用户，初始化默认用户
        if (users.isEmpty()) {
            initDefaultUsers();
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                // 为每个客户端启动一个新线程
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 客户端处理线程
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    Object obj = in.readObject();
                    if (obj instanceof Request) {
                        Request req = (Request) obj;
                        Response res = handleRequest(req);
                        out.writeObject(res);
                        out.flush();
                        
                        // 每次写操作后保存数据
                        if (req.getType() != RequestType.GET_ALL_POSTS && req.getType() != RequestType.LOGIN) {
                            saveData(); 
                        }
                    }
                }
            } catch (EOFException e) {
                System.out.println("Client disconnected");
            } catch (Exception e) {
                System.err.println("Error handling client: " + e.getMessage());
            }
        }

        // 处理具体的请求逻辑
        private Response handleRequest(Request req) {
            switch (req.getType()) {
                case LOGIN:
                    String uName = (String) req.getData();
                    String pwd = req.getExtra();
                    User user = users.get(uName);
                    if (user != null && user.checkPassword(pwd)) {
                        return new Response(true, "Login success", user);
                    }
                    return new Response(false, "Invalid username or password", null);

                case REGISTER:
                    User newUser = (User) req.getData();
                    if (users.containsKey(newUser.getUsername())) {
                        return new Response(false, "Username already exists", null);
                    }
                    users.put(newUser.getUsername(), newUser);
                    return new Response(true, "Registration successful", null);

                case GET_ALL_POSTS:
                    // 按时间倒序返回
                    List<Post> sortedPosts = new ArrayList<>(posts);
                    sortedPosts.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
                    return new Response(true, "Fetched posts", sortedPosts);

                case ADD_POST:
                    Post p = (Post) req.getData();
                    posts.add(0, p);
                    return new Response(true, "Post added", null);
                
                case TOGGLE_LIKE:
                    // 数据更新逻辑需遍历找到对应帖子
                    // 这里简化处理：客户端发来的是更新后的Post，我们替换服务器端的
                    Post updatedPost = (Post) req.getData();
                    updatePost(updatedPost);
                    return new Response(true, "Like updated", null);

                case ADD_COMMENT:
                    Post postWithComment = (Post) req.getData();
                    updatePost(postWithComment);
                    return new Response(true, "Comment added", null);
                    
                case DELETE_POST:
                    Post postToDelete = (Post) req.getData();
                    // 需要根据ID删除，这里简单使用equals
                    posts.removeIf(existing -> existing.getId().equals(postToDelete.getId()));
                    return new Response(true, "Post deleted", null);

                default:
                    return new Response(false, "Unknown request", null);
            }
        }
        
        private void updatePost(Post newVersion) {
            for (int i = 0; i < posts.size(); i++) {
                if (posts.get(i).getId().equals(newVersion.getId())) {
                    posts.set(i, newVersion);
                    break;
                }
            }
        }
    }

    // 数据持久化方法
    private static void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(users);
            out.writeObject(posts);
        } catch (Exception e) {
            System.err.println("Server save failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadData() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            users = (Map<String, User>) in.readObject();
            posts = (List<Post>) in.readObject();
            System.out.println("Data loaded. Posts: " + posts.size());
        } catch (Exception e) {
            System.err.println("Server load failed: " + e.getMessage());
        }
    }

    private static void initDefaultUsers() {
        users.put("alice", new User("alice", "Alice", "123456"));
        users.put("bob", new User("bob", "Bob", "123456"));
        users.put("charlie", new User("charlie", "Charlie", "123456"));
    }
}
