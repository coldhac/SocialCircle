package main.controllers;

import javafx.stage.Stage;
import main.data.DataManager;
import main.views.LoginView;
import main.views.MainView;

// Application Controller
public class AppController {
    
    private Stage stage;
    
    public AppController(Stage stage) {
        this.stage = stage;
        
        stage.setTitle("Social Circle");
        stage.setWidth(1000);
        stage.setHeight(750);
    }
    
    // Show the login screen
    public void showLoginView() {
        LoginView loginView = new LoginView(this);
        stage.setScene(loginView.getScene());
        stage.centerOnScreen();
        stage.show();
    }
    
    // Show the main feed
    public void showMainView() {
        MainView mainView = new MainView(this); // Pass controller to view
        stage.setScene(mainView.getScene());
        stage.centerOnScreen();
        stage.show();
    }
    
    // Logout logic
    public void logout() {
        DataManager.getInstance().logout();
        showLoginView();
    }
}
