package main.controllers;

import javafx.stage.Stage;
import main.views.MainView;
import main.data.DataManager;

// 应用控制器
public class AppController {
    
    private Stage stage;
    private DataManager dataManager;
    
    public AppController(Stage stage) {
        this.stage = stage;
        this.dataManager = DataManager.getInstance();
        
        stage.setTitle("Social Circle");
        stage.setWidth(1000);
        stage.setHeight(700);
    }
    
    public void showMainView() {
        MainView mainView = new MainView();
        stage.setScene(mainView.getScene());
        stage.show();
    }
}
