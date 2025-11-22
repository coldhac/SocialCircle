package main.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.data.DataManager;
import main.models.*;
import java.util.List;

// 用户主页对话框
public class UserPageDialog extends Dialog<Void> {
    
    private User user;
    private DataManager dataManager;
    
    public UserPageDialog(User user) {
        this.user = user;
        this.dataManager = DataManager.getInstance();
        
        setTitle("Profile Page");
        setResizable(true);
        getDialogPane().setPrefSize(700, 600);
        
        VBox content = createContent();
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        
        getDialogPane().setContent(scroll);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }
    
    private VBox createContent() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        
        // 用户信息卡片
        VBox userCard = createUserCard();
        
        // 统计信息
        HBox stats = createStats();
        
        // 帖子标题
        Label title = new Label(user.getDisplayName() + " posts");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        // 帖子网格
        GridPane grid = createPostGrid();
        
        box.getChildren().addAll(userCard, stats, title, grid);
        
        return box;
    }
    
    private VBox createUserCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        card.setAlignment(Pos.CENTER);
        
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 50px;");
        
        Label name = new Label(user.getDisplayName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        
        Label username = new Label("@" + user.getUsername());
        username.setTextFill(Color.GRAY);
        
        card.getChildren().addAll(avatar, name, username);
        
        return card;
    }
    
    private HBox createStats() {
        HBox box = new HBox(30);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        box.setAlignment(Pos.CENTER);
        
        List<Post> userPosts = dataManager.getUserPosts(user);
        
        VBox postsBox = createStatBox("posts", String.valueOf(userPosts.size()));
        VBox likesBox = createStatBox("likes", String.valueOf(user.getTotalLikes()));
        
        box.getChildren().addAll(postsBox, new Separator(javafx.geometry.Orientation.VERTICAL), likesBox);
        
        return box;
    }
    
    private VBox createStatBox(String label, String value) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1877f2;");
        
        Label nameLabel = new Label(label);
        nameLabel.setTextFill(Color.GRAY);
        
        box.getChildren().addAll(valueLabel, nameLabel);
        
        return box;
    }
    
    private GridPane createPostGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        List<Post> userPosts = dataManager.getUserPosts(user);
        
        if (userPosts.isEmpty()) {
            Label empty = new Label("U haven't post anything yet");
            empty.setTextFill(Color.GRAY);
            grid.add(empty, 0, 0);
            return grid;
        }
        
        int col = 0;
        int row = 0;
        
        for (Post post : userPosts) {
            VBox thumbnail = createThumbnail(post);
            grid.add(thumbnail, col, row);
            
            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
        
        return grid;
    }
    
    private VBox createThumbnail(Post post) {
        VBox box = new VBox(5);
        box.setPrefSize(200, 200);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-cursor: hand;");
        box.setAlignment(Pos.TOP_LEFT);
        
        // 图片或占位符
        StackPane imgPane = new StackPane();
        imgPane.setPrefSize(180, 120);
        imgPane.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 8;");
        
        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(
                    "file:" + post.getImagePath(), 180, 120, true, true);
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitWidth(180);
                imgView.setPreserveRatio(true);
                imgPane.getChildren().add(imgView);
            } catch (Exception e) {
                Label icon = new Label("📷");
                icon.setStyle("-fx-font-size: 30px;");
                imgPane.getChildren().add(icon);
            }
        } else {
            Label icon = new Label("📝");
            icon.setStyle("-fx-font-size: 30px;");
            imgPane.getChildren().add(icon);
        }
        
        // 内容预览
        Label content = new Label(post.getContent());
        content.setMaxWidth(180);
        content.setMaxHeight(40);
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 11px;");
        
        // 统计
        HBox stats = new HBox(10);
        Label likes = new Label("❤️ " + post.getLikeCount());
        likes.setStyle("-fx-font-size: 10px;");
        Label comments = new Label("💬 " + post.getCommentCount());
        comments.setStyle("-fx-font-size: 10px;");
        stats.getChildren().addAll(likes, comments);
        
        box.getChildren().addAll(imgPane, content, stats);
        
        // 点击查看详情
        box.setOnMouseClicked(e -> {
            PostDetailDialog dialog = new PostDetailDialog(post);
            dialog.showAndWait();
        });
        
        return box;
    }
}
