package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.controllers.AppController;
import main.data.DataManager;
import main.models.*;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) {
        System.out.println("=== Social Circle Starting ===");
        
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();
        
        // Only generate sample data if no posts exist (and users were just initialized)
        if (dataManager.getAllPosts().isEmpty()) {
            generateSampleData();
        }
        
        AppController controller = new AppController(stage);
        // Changed: Start with Login View instead of Main View
        controller.showLoginView();
        
        // Save data on close
        stage.setOnCloseRequest(e -> {
            dataManager.saveData();
            System.out.println("Application closing, data saved.");
        });
    }
    
    // Generate Sample Data
    private void generateSampleData() {
        System.out.println("Generating sample data...");
        
        DataManager dm = DataManager.getInstance();
        // Note: Users are created in DataManager.initUsers() if empty
        User alice = dm.getUser("alice");
        User bob = dm.getUser("bob");
        User charlie = dm.getUser("charlie");
        
        if (alice == null || bob == null || charlie == null) return;

        // Alice's Posts
        Post post1 = new Post(alice, "Hello world! This is my first post on Social Circle.", "");
        dm.addPost(post1);
        post1.toggleLike("bob");
        post1.toggleLike("charlie");
        
        Post post2 = new Post(alice, "Learning JavaFX is fun but challenging!", "");
        dm.addPost(post2);
        
        // Bob's Post
        Post post3 = new Post(bob, "The weather is amazing today.", "");
        dm.addPost(post3);
        post3.toggleLike("alice");
        
        Comment c1 = new Comment(charlie, "Totally agree!", post3);
        post3.addComment(c1);
        
        // Charlie's Post
        Post post4 = new Post(charlie, "Anyone up for coffee?", "");
        dm.addPost(post4);
        post4.toggleLike("alice");
        post4.toggleLike("bob");
        
        System.out.println("Sample data generated.");
    }
    
    @Override
    public void stop() {
        DataManager.getInstance().saveData();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
