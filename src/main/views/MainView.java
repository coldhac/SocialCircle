package main.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.controllers.AppController;
import main.data.DataManager;
import main.models.*;
import java.util.List;

// Main View
public class MainView {
    
    private Scene scene;
    private BorderPane root;
    private VBox feedContainer;
    private DataManager dataManager;
    private AppController controller; // Reference to controller
    
    private TextField searchField;
    private ComboBox<String> sortBox;
    
    public MainView(AppController controller) {
        this.controller = controller;
        this.dataManager = DataManager.getInstance();
        
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f2f5;");
        
        root.setTop(createTopBar());
        root.setCenter(createCenter());
        
        scene = new Scene(root);
        loadFeed();
    }
    
    // Create Top Bar
    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10, 15, 10, 15));
        topBar.setStyle("-fx-background-color: #4267B2;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("SocialCircle");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label userLabel = new Label("User: " + dataManager.getCurrentUser().getUsername());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(200);
        
        Button searchBtn = new Button("🔍");
        searchBtn.setOnAction(e -> search());
        
        sortBox = new ComboBox<>();
        sortBox.getItems().addAll("Newest", "Most Liked");
        sortBox.setValue("Newest");
        sortBox.setOnAction(e -> loadFeed());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button newPostBtn = new Button("Post");
        newPostBtn.setStyle("-fx-background-color: #42b72a; -fx-text-fill: white; -fx-font-weight: bold;");
        newPostBtn.setOnAction(e -> showNewPostDialog());
        
        Button myPageBtn = new Button("My Profile");
        myPageBtn.setOnAction(e -> showUserPage(dataManager.getCurrentUser()));
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> controller.logout());
        
        topBar.getChildren().addAll(title, searchField, searchBtn, sortBox, spacer, userLabel, newPostBtn, myPageBtn, logoutBtn);
        
        return topBar;
    }
    
    // Create Center Area
    private ScrollPane createCenter() {
        feedContainer = new VBox(15);
        feedContainer.setPadding(new Insets(20));
        feedContainer.setAlignment(Pos.TOP_CENTER);
        
        ScrollPane scroll = new ScrollPane(feedContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #f0f2f5;");
        
        return scroll;
    }
    
    // Load Feed
    private void loadFeed() {
        feedContainer.getChildren().clear();
        
        List<Post> posts;
        if ("Most Liked".equals(sortBox.getValue())) {
            posts = dataManager.sortByLikes();
        } else {
            posts = dataManager.sortByTime();
        }
        
        if (posts.isEmpty()) {
            Label empty = new Label("No posts available yet.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            feedContainer.getChildren().add(empty);
            return;
        }
        
        for (Post post : posts) {
            feedContainer.getChildren().add(createPostCard(post));
        }
    }
    
    // Create Post Card
    private VBox createPostCard(Post post) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setMaxWidth(600);
        
        // User Info
        HBox userBar = new HBox(10);
        userBar.setAlignment(Pos.CENTER_LEFT);
        
        Label userName = new Label(post.getAuthor().getDisplayName());
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        userName.setOnMouseClicked(e -> showUserPage(post.getAuthor()));
        
        Label time = new Label(post.getFormattedTime());
        time.setTextFill(Color.GRAY);
        time.setStyle("-fx-font-size: 12px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        userBar.getChildren().addAll(userName, new Label("·"), time, spacer);
        
        // Delete Button (Only for author)
        if (post.getAuthor().equals(dataManager.getCurrentUser())) {
            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 11px;");
            deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #ffebee; -fx-text-fill: red; -fx-font-size: 11px;"));
            deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 11px;"));
            deleteBtn.setOnAction(e -> deletePost(post));
            userBar.getChildren().add(deleteBtn);
        }
        
        // Content
        Label content = new Label(post.getContent());
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 14px;");
        
        // Image
        if (post.getImageBytes() != null && post.getImageBytes().length > 0) {
            try {
                // 从字节数组加载图片
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(post.getImageBytes());
                javafx.scene.image.Image img = new javafx.scene.image.Image(bis, 550, 400, true, true);
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitWidth(550);
                imgView.setPreserveRatio(true);
                card.getChildren().add(imgView);
            } catch (Exception ex) {
                Label error = new Label("Failed to load image");
                error.setTextFill(Color.RED);
                card.getChildren().add(error);
            }
        } else if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(
                    "file:" + post.getImagePath(), 550, 400, true, true);
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitWidth(550);
                imgView.setPreserveRatio(true);
                card.getChildren().add(imgView);
            } catch (Exception ex) {
                Label error = new Label("Failed to load image");
                error.setTextFill(Color.RED);
                card.getChildren().add(error);
            }
        }
        
        // Actions Bar
        HBox actions = new HBox(20);
        actions.setPadding(new Insets(10, 0, 0, 0));
        
        String currentUser = dataManager.getCurrentUser().getUsername();
        boolean liked = post.isLikedBy(currentUser);
        
        Button likeBtn = new Button((liked ? "❤️ Liked" : "🤍 Like") + " (" + post.getLikeCount() + ")");
        likeBtn.setStyle("-fx-background-color: transparent;");
        likeBtn.setOnAction(e -> {
            // 更新点赞逻辑
            post.toggleLike(currentUser);
            DataManager.getInstance().updatePost(post, main.common.RequestType.TOGGLE_LIKE); // 发送到服务器
            loadFeed();
        });
        
        Button commentBtn = new Button("💬 Comments (" + post.getCommentCount() + ")");
        commentBtn.setStyle("-fx-background-color: transparent;");
        commentBtn.setOnAction(e -> showPostDetail(post));
        
        actions.getChildren().addAll(likeBtn, commentBtn);
        
        card.getChildren().addAll(userBar, content, new Separator(), actions);
        
        return card;
    }
    
    private void search() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadFeed();
            return;
        }
        
        List<Post> results = dataManager.searchPosts(keyword);
        feedContainer.getChildren().clear();
        
        if (results.isEmpty()) {
            Label empty = new Label("No matching posts found.");
            empty.setStyle("-fx-text-fill: gray;");
            feedContainer.getChildren().add(empty);
        } else {
            for (Post post : results) {
                feedContainer.getChildren().add(createPostCard(post));
            }
        }
    }
    
    private void showNewPostDialog() {
        NewPostDialog dialog = new NewPostDialog();
        dialog.showAndWait();
        loadFeed();
    }
    
    private void showPostDetail(Post post) {
        PostDetailDialog dialog = new PostDetailDialog(post);
        dialog.showAndWait();
        loadFeed();
    }
    
    private void showUserPage(User user) {
        UserPageDialog dialog = new UserPageDialog(user);
        dialog.showAndWait();
    }
    
    private void deletePost(Post post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete this post?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataManager.removePost(post);
                loadFeed();
            }
        });
    }
    
    public Scene getScene() {
        return scene;
    }
}
