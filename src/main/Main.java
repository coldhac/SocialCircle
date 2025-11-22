package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.controllers.AppController;
import main.data.DataManager;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) {
        System.out.println("=== Social Circle Client Starting ===");
        
        // 初始化数据管理器（这将建立与服务器的连接）
        // Data Manager initialization (this establishes connection to server)
        DataManager dataManager = DataManager.getInstance();
        
        // 初始化应用控制器
        // Initialize App Controller
        AppController controller = new AppController(stage);
        
        // 显示登录界面
        // Show Login View
        controller.showLoginView();
        
        // 设置关闭请求处理
        // Handle window close request
        stage.setOnCloseRequest(e -> {
            System.out.println("Application closing...");
            // 关闭网络连接
            // Close network connection
            dataManager.close();
        });
    }
    
    @Override
    public void stop() {
        // 确保应用停止时关闭连接
        // Ensure connection is closed when app stops
        DataManager.getInstance().close();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}