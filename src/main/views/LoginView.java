package main.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.controllers.AppController;
import main.data.DataManager;

public class LoginView {

    private AppController controller;
    private Scene scene;
    private VBox root;
    
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField displayNameField; // For registration
    private Label statusLabel;
    
    private boolean isRegisterMode = false; // Toggle between login and register

    public LoginView(AppController controller) {
        this.controller = controller;
        createView();
    }

    private void createView() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f0f2f5;");
        root.setPrefSize(400, 500);

        // Title
        Label title = new Label("Social Circle");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1877f2;");

        Label subtitle = new Label("Connect with friends and the world.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        // Input Fields
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        usernameField.setPrefHeight(40);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setPrefHeight(40);
        
        // Register field (hidden by default)
        displayNameField = new TextField();
        displayNameField.setPromptText("Display Name (e.g. Alice)");
        displayNameField.setMaxWidth(300);
        displayNameField.setPrefHeight(40);
        displayNameField.setManaged(false);
        displayNameField.setVisible(false);

        // Buttons
        Button actionBtn = new Button("Log In");
        actionBtn.setStyle("-fx-background-color: #1877f2; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        actionBtn.setPrefSize(300, 45);
        actionBtn.setOnAction(e -> handleAction());

        Button switchModeBtn = new Button("Create New Account");
        switchModeBtn.setStyle("-fx-background-color: #42b72a; -fx-text-fill: white; -fx-font-weight: bold;");
        switchModeBtn.setPrefSize(200, 40);
        switchModeBtn.setOnAction(e -> toggleMode(actionBtn, switchModeBtn));
        
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        // Layout assembly
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(350);
        
        formBox.getChildren().addAll(usernameField, passwordField, displayNameField, statusLabel, actionBtn, new Separator(), switchModeBtn);
        
        root.getChildren().addAll(title, subtitle, formBox);
        
        scene = new Scene(root);
    }

    private void toggleMode(Button actionBtn, Button switchModeBtn) {
        isRegisterMode = !isRegisterMode;
        
        if (isRegisterMode) {
            displayNameField.setManaged(true);
            displayNameField.setVisible(true);
            actionBtn.setText("Sign Up");
            switchModeBtn.setText("Back to Login");
            statusLabel.setText("");
        } else {
            displayNameField.setManaged(false);
            displayNameField.setVisible(false);
            actionBtn.setText("Log In");
            switchModeBtn.setText("Create New Account");
            statusLabel.setText("");
        }
    }

    private void handleAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password are required.");
            return;
        }
        
        DataManager dm = DataManager.getInstance();
        
        if (isRegisterMode) {
            // Handle Registration
            String displayName = displayNameField.getText().trim();
            if (displayName.isEmpty()) {
                statusLabel.setText("Display name is required.");
                return;
            }
            
            boolean success = dm.registerUser(username, password, displayName);
            if (success) {
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Registered successfully! Please login.");
                // Switch back to login mode logic could go here
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Username already exists.");
            }
            
        } else {
            // Handle Login
            if (dm.login(username, password)) {
                System.out.println("Logged in as: " + username);
                controller.showMainView();
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Invalid username or password.");
            }
        }
    }

    public Scene getScene() {
        return scene;
    }
}
