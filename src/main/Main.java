package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.controllers.AppController;
import main.data.DataManager;
import main.models.*;

// 程序入口
public class Main extends Application {
    
    @Override
    public void start(Stage stage) {
        System.out.println("=== Social Circle 启动 ===");
        
        // 加载数据
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();
        
        // 如果没有数据，生成一些示例数据
        if (dataManager.getAllPosts().isEmpty()) {
            generateSampleData();
        }
        
        // 显示主界面
        AppController controller = new AppController(stage);
        controller.showMainView();
        
        // 关闭时保存数据
        stage.setOnCloseRequest(e -> {
            dataManager.saveData();
            System.out.println("数据已保存");
        });
    }
    
    // 生成示例数据
    private void generateSampleData() {
        System.out.println("生成示例数据...");
        
        DataManager dm = DataManager.getInstance();
        User alice = dm.getUser("alice");
        User bob = dm.getUser("bob");
        User charlie = dm.getUser("charlie");
        
        // Alice的帖子
        Post post1 = new Post(alice, "今天天气真好！☀️", "");
        dm.addPost(post1);
        post1.toggleLike("bob");
        post1.toggleLike("charlie");
        
        Post post2 = new Post(alice, "刚学会了一道新菜 🍳", "");
        dm.addPost(post2);
        
        // Bob的帖子
        Post post3 = new Post(bob, "周末去爬山了 🏔️", "");
        dm.addPost(post3);
        post3.toggleLike("alice");
        
        Comment c1 = new Comment(charlie, "看起来很棒！", post3);
        post3.addComment(c1);
        
        // Charlie的帖子
        Post post4 = new Post(charlie, "读了一本好书 📚", "");
        dm.addPost(post4);
        post4.toggleLike("alice");
        post4.toggleLike("bob");
        
        System.out.println("示例数据生成完成");
    }
    
    @Override
    public void stop() {
        DataManager.getInstance().saveData();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
