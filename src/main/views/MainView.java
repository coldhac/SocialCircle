package main.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.data.DataManager;
import main.models.*;
import java.util.List;

// 主视图
public class MainView {
    
    private Scene scene;
    private BorderPane root;
    private VBox feedContainer;
    private DataManager dataManager;
    
    private TextField searchField;
    private ComboBox<String> sortBox;
    
    public MainView() {
        dataManager = DataManager.getInstance();
        
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f2f5;");
        
        root.setTop(createTopBar());
        root.setCenter(createCenter());
        
        scene = new Scene(root);
        loadFeed();
    }
    
    // 创建顶部工具栏
    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #4267B2;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Social Circle");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label userLabel = new Label("@" + dataManager.getCurrentUser().getUsername());
        userLabel.setStyle("-fx-text-fill: white;");
        
        searchField = new TextField();
        searchField.setPromptText("搜索...");
        searchField.setPrefWidth(200);
        
        Button searchBtn = new Button("搜索");
        searchBtn.setOnAction(e -> search());
        
        sortBox = new ComboBox<>();
        sortBox.getItems().addAll("最新", "最多赞");
        sortBox.setValue("最新");
        sortBox.setOnAction(e -> loadFeed());
        
        Button newPostBtn = new Button("发布");
        newPostBtn.setStyle("-fx-background-color: #42b72a; -fx-text-fill: white;");
        newPostBtn.setOnAction(e -> showNewPostDialog());
        
        Button myPageBtn = new Button("我的主页");
        myPageBtn.setOnAction(e -> showUserPage(dataManager.getCurrentUser()));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(title, userLabel, spacer, 
            searchField, searchBtn, sortBox, newPostBtn, myPageBtn);
        
        return topBar;
    }
    
    // 创建中心区域
    private ScrollPane createCenter() {
        feedContainer = new VBox(15);
        feedContainer.setPadding(new Insets(20));
        feedContainer.setAlignment(Pos.TOP_CENTER);
        
        ScrollPane scroll = new ScrollPane(feedContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        
        return scroll;
    }
    
    // 加载动态
    private void loadFeed() {
        feedContainer.getChildren().clear();
        
        List<Post> posts;
        if ("最多赞".equals(sortBox.getValue())) {
            posts = dataManager.sortByLikes();
        } else {
            posts = dataManager.sortByTime();
        }
        
        if (posts.isEmpty()) {
            Label empty = new Label("还没有动态");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            feedContainer.getChildren().add(empty);
            return;
        }
        
        for (Post post : posts) {
            feedContainer.getChildren().add(createPostCard(post));
        }
    }
    
    // 创建帖子卡片
    private VBox createPostCard(Post post) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        card.setMaxWidth(700);
        
        // 用户信息
        HBox userBar = new HBox(10);
        userBar.setAlignment(Pos.CENTER_LEFT);
        
        Label userName = new Label(post.getAuthor().getDisplayName());
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        userName.setOnMouseClicked(e -> showUserPage(post.getAuthor()));
        userName.setStyle("-fx-cursor: hand; -fx-font-weight: bold;");
        
        Label time = new Label(post.getFormattedTime());
        time.setTextFill(Color.GRAY);
        
        userBar.getChildren().addAll(userName, new Label("·"), time);
        
        // 删除按钮（只有作者能看到）
        if (post.getAuthor().equals(dataManager.getCurrentUser())) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button deleteBtn = new Button("删除");
            deleteBtn.setOnAction(e -> deletePost(post));
            userBar.getChildren().addAll(spacer, deleteBtn);
        }
        
        // 内容
        Label content = new Label(post.getContent());
        content.setWrapText(true);
        
        // 图片
        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(
                    "file:" + post.getImagePath(), 650, 400, true, true);
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitWidth(650);
                imgView.setPreserveRatio(true);
                card.getChildren().add(imgView);
            } catch (Exception ex) {
                Label error = new Label("图片加载失败");
                error.setTextFill(Color.RED);
                card.getChildren().add(error);
            }
        }
        
        // 互动栏
        HBox actions = new HBox(15);
        
        String currentUser = dataManager.getCurrentUser().getUsername();
        boolean liked = post.isLikedBy(currentUser);
        
        Button likeBtn = new Button((liked ? "❤️" : "🤍") + " " + post.getLikeCount());
        likeBtn.setOnAction(e -> {
            post.toggleLike(currentUser);
            loadFeed();
        });
        
        Button commentBtn = new Button("💬 " + post.getCommentCount());
        commentBtn.setOnAction(e -> showPostDetail(post));
        
        actions.getChildren().addAll(likeBtn, commentBtn);
        
        card.getChildren().addAll(userBar, content, actions);
        
        return card;
    }
    
    // 搜索
    private void search() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadFeed();
            return;
        }
        
        List<Post> results = dataManager.searchPosts(keyword);
        feedContainer.getChildren().clear();
        
        if (results.isEmpty()) {
            Label empty = new Label("没找到相关内容");
            empty.setStyle("-fx-text-fill: gray;");
            feedContainer.getChildren().add(empty);
        } else {
            for (Post post : results) {
                feedContainer.getChildren().add(createPostCard(post));
            }
        }
    }
    
    // 显示发布对话框
    private void showNewPostDialog() {
        NewPostDialog dialog = new NewPostDialog();
        dialog.showAndWait();
        loadFeed();
    }
    
    // 显示帖子详情
    private void showPostDetail(Post post) {
        PostDetailDialog dialog = new PostDetailDialog(post);
        dialog.showAndWait();
        loadFeed();
    }
    
    // 显示用户主页
    private void showUserPage(User user) {
        UserPageDialog dialog = new UserPageDialog(user);
        dialog.showAndWait();
    }
    
    // 删除帖子
    private void deletePost(Post post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认");
        alert.setHeaderText("删除这条动态?");
        
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
