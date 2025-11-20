package main.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.data.DataManager;
import main.models.*;
import main.utils.ValidationUtil;

// 帖子详情对话框
public class PostDetailDialog extends Dialog<Void> {
    
    private Post post;
    private DataManager dataManager;
    private VBox commentsBox;
    private Button likeBtn;
    
    public PostDetailDialog(Post post) {
        this.post = post;
        this.dataManager = DataManager.getInstance();
        
        setTitle("详情");
        setResizable(true);
        getDialogPane().setPrefSize(650, 600);
        
        VBox content = createContent();
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        
        getDialogPane().setContent(scroll);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }
    
    private VBox createContent() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        
        // 帖子卡片
        VBox postCard = createPostCard();
        
        // 评论标题
        Label commentTitle = new Label("评论 (" + post.getCommentCount() + ")");
        commentTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        // 评论输入
        VBox commentInput = createCommentInput();
        
        // 评论列表
        commentsBox = new VBox(10);
        loadComments();
        
        box.getChildren().addAll(postCard, new Separator(), 
            commentTitle, commentInput, commentsBox);
        
        return box;
    }
    
    private VBox createPostCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        // 用户信息
        HBox userBar = new HBox(10);
        Label userName = new Label(post.getAuthor().getDisplayName());
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label time = new Label(post.getFormattedTime());
        time.setTextFill(Color.GRAY);
        userBar.getChildren().addAll(userName, time);
        
        // 内容
        Label content = new Label(post.getContent());
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 14px;");
        
        // 图片
        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(
                    "file:" + post.getImagePath(), 600, 450, true, true);
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitWidth(600);
                imgView.setPreserveRatio(true);
                card.getChildren().add(imgView);
            } catch (Exception e) {
                Label error = new Label("图片加载失败");
                error.setTextFill(Color.RED);
                card.getChildren().add(error);
            }
        }
        
        // 互动栏
        HBox actions = new HBox(15);
        
        String currentUser = dataManager.getCurrentUser().getUsername();
        boolean liked = post.isLikedBy(currentUser);
        
        likeBtn = new Button((liked ? "❤️ 已赞" : "🤍 点赞") + " (" + post.getLikeCount() + ")");
        likeBtn.setOnAction(e -> toggleLike());
        
        Label commentLabel = new Label("💬 " + post.getCommentCount() + " 条评论");
        
        actions.getChildren().addAll(likeBtn, commentLabel);
        
        card.getChildren().addAll(userBar, content, actions);
        
        return card;
    }
    
    private VBox createCommentInput() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("写下你的评论...");
        commentArea.setPrefRowCount(2);
        commentArea.setWrapText(true);
        
        Button submitBtn = new Button("发表");
        submitBtn.setStyle("-fx-background-color: #1877f2; -fx-text-fill: white;");
        submitBtn.setOnAction(e -> {
            String text = commentArea.getText().trim();
            if (addComment(text)) {
                commentArea.clear();
            }
        });
        
        box.getChildren().addAll(commentArea, submitBtn);
        
        return box;
    }
    
    private void loadComments() {
        commentsBox.getChildren().clear();
        
        if (post.getComments().isEmpty()) {
            Label empty = new Label("还没有评论");
            empty.setStyle("-fx-text-fill: gray;");
            commentsBox.getChildren().add(empty);
            return;
        }
        
        // 使用Iterator遍历评论
        java.util.Iterator<Comment> iterator = post.getComments().iterator();
        while (iterator.hasNext()) {
            Comment comment = iterator.next();
            commentsBox.getChildren().add(createCommentCard(comment));
        }
    }
    
    private VBox createCommentCard(Comment comment) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        HBox userBar = new HBox(10);
        userBar.setAlignment(Pos.CENTER_LEFT);
        
        Label userName = new Label(comment.getAuthor().getDisplayName());
        userName.setStyle("-fx-font-weight: bold;");
        
        Label time = new Label(comment.getFormattedTime());
        time.setTextFill(Color.GRAY);
        time.setStyle("-fx-font-size: 11px;");
        
        userBar.getChildren().addAll(userName, time);
        
        // 删除按钮（只有作者能看到）
        if (comment.getAuthor().equals(dataManager.getCurrentUser())) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button deleteBtn = new Button("删除");
            deleteBtn.setStyle("-fx-font-size: 11px;");
            deleteBtn.setOnAction(e -> deleteComment(comment));
            userBar.getChildren().addAll(spacer, deleteBtn);
        }
        
        Label content = new Label(comment.getContent());
        content.setWrapText(true);
        
        card.getChildren().addAll(userBar, content);
        
        return card;
    }
    
    private void toggleLike() {
        String currentUser = dataManager.getCurrentUser().getUsername();
        boolean liked = post.toggleLike(currentUser);
        
        likeBtn.setText((liked ? "❤️ 已赞" : "🤍 点赞") + " (" + post.getLikeCount() + ")");
    }
    
    private boolean addComment(String text) {
        String error = ValidationUtil.validateContent(text);
        if (error != null) {
            showError(error);
            return false;
        }
        
        Comment comment = new Comment(dataManager.getCurrentUser(), text, post);
        post.addComment(comment);
        
        loadComments();
        return true;
    }
    
    private void deleteComment(Comment comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认");
        alert.setHeaderText("删除这条评论?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                post.removeComment(comment);
                loadComments();
            }
        });
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
