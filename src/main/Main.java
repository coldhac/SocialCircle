package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.controllers.AppController;
import main.data.DataManager;
import main.models.*;


public class Main extends Application {
    
    @Override
    public void start(Stage stage) {
        System.out.println("=== Social Circle 启动 ===");
        
        
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();
        
        
        if (dataManager.getAllPosts().isEmpty()) {
            generateSampleData();
        }
        
        
        AppController controller = new AppController(stage);
        controller.showMainView();
        
        // 关闭时保存数据
        stage.setOnCloseRequest(e -> {
            dataManager.saveData();
            System.out.println("data saved");
        });
    }
    
    // 生成示例数据
    private void generateSampleData() {
        //System.out.println("生成示例数据...");
        
        DataManager dm = DataManager.getInstance();
        User alice = dm.getUser("alice");
        User bob = dm.getUser("bob");
        User charlie = dm.getUser("charlie");
        
        // Alice的帖子
        Post post1 = new Post(alice, "我是泽连斯基， 我不同意特朗普给克林顿口", "");
        dm.addPost(post1);
        post1.toggleLike("bob");
        post1.toggleLike("charlie");
        
        Post post2 = new Post(alice, "刚刚我家安娜被铁拳曹飞，我不能露出一丝笑意，因为我知道如果被发现她就会黑化成莫伊拉奖励我", "");
        dm.addPost(post2);
        
        // Bob的帖子
        Post post3 = new Post(bob, "我是普京，vote me", "");
        dm.addPost(post3);
        post3.toggleLike("alice");
        
        Comment c1 = new Comment(charlie, "我是习，我不同意跳跳虎吃小熊维尼的蜂蜜！", post3);
        post3.addComment(c1);
        
        // Charlie的帖子
        Post post4 = new Post(charlie, "刚刚西格玛开大被我一针扎下来坐了个大屁吨，我不能露出一丝笑意，因为我知道如果被发现他就会换成铁拳奖励我", "");
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
