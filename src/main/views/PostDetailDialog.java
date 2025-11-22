package main.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.data.DataManager;
import main.models.*;
import main.utils.ValidationUtil;
import main.common.RequestType; // 导入请求类型

// Post Detail Dialog
public class PostDetailDialog extends Dialog<Void> {
    
    private Post post;
    private DataManager dataManager;
    private VBox commentsBox;
    private Button likeBtn;
    
    public PostDetailDialog(Post post) {
        this.post = post;
        this.dataManager = DataManager.getInstance();
        
        setTitle("Details");
        setResizable(true);
        getDialogPane().setPrefSize(650, 600);
        
        VBox content = createContent();
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        
        getDialogPane().setContent(scroll);
        ButtonType closeBtn = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeBtn);
    }
    
    private VBox createContent() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        
        // Post Card
        VBox postCard = createPostCard();
        
        // Comments Title
        Label commentTitle = new Label("Comments (" + post.getCommentCount() + ")");
        commentTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        // Comment Input
        VBox commentInput = createCommentInput();
        
        // Comments List
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
        
        // User Info
        HBox userBar = new HBox(10);
        Label userName = new Label(post.getAuthor().getDisplayName());
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label time = new Label(post.getFormattedTime());
        time.setTextFill(Color.GRAY);
        userBar.getChildren().addAll(userName, time);
        
        // Content
        Label content = new Label(post.getContent());
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 14px;");
        
        // Image (Updated logic for byte array)
        if (post.getImageBytes() != null && post.getImageBytes().length > 0) {
            try {
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(post.getImageBytes());
                javafx.scene.image.Image img = new javafx.scene.image.Image(bis, 600, 450, true, true);
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitWidth(600);
                imgView.setPreserveRatio(true);
                card.getChildren().add(imgView);
            } catch (Exception e) {
                card.getChildren().add(new Label("Error loading image"));
            }
        } else if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            // Fallback for old local files
            Label pathLabel = new Label("[Image: " + post.getImagePath() + "]");
            pathLabel.setTextFill(Color.GRAY);
            card.getChildren().add(pathLabel);
        }
        
        // Actions Bar
        HBox actions = new HBox(15);
        
        User currentUser = dataManager.getCurrentUser();
        boolean liked = currentUser != null && post.isLikedBy(currentUser.getUsername());
        
        likeBtn = new Button((liked ? "❤️ Liked" : "🤍 Like") + " (" + post.getLikeCount() + ")");
        likeBtn.setOnAction(e -> toggleLike());
        
        Label commentLabel = new Label("💬 " + post.getCommentCount() + " comments");
        
        actions.getChildren().addAll(likeBtn, commentLabel);
        
        card.getChildren().addAll(userBar, content, actions);
        
        return card;
    }
    
    private VBox createCommentInput() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write your comment...");
        commentArea.setPrefRowCount(2);
        commentArea.setWrapText(true);
        
        Button submitBtn = new Button("Publish");
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
            Label empty = new Label("No comments yet");
            empty.setStyle("-fx-text-fill: gray;");
            commentsBox.getChildren().add(empty);
            return;
        }
        
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
        
        // Delete button (Only for author)
        if (dataManager.getCurrentUser() != null && 
            comment.getAuthor().getUsername().equals(dataManager.getCurrentUser().getUsername())) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button deleteBtn = new Button("Delete");
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
        
        likeBtn.setText((liked ? "❤️ Liked" : "🤍 Like") + " (" + post.getLikeCount() + ")");
        
        // [关键修改] 同步到服务器
        dataManager.updatePost(post, RequestType.TOGGLE_LIKE);
    }
    
    private boolean addComment(String text) {
        String error = ValidationUtil.validateContent(text);
        if (error != null) {
            showError(error);
            return false;
        }
        
        Comment comment = new Comment(dataManager.getCurrentUser(), text, post);
        post.addComment(comment);
        
        // [关键修改] 同步到服务器
        dataManager.updatePost(post, RequestType.ADD_COMMENT);
        
        loadComments();
        return true;
    }
    
    private void deleteComment(Comment comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Delete this comment?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                post.removeComment(comment);
                
                // [关键修改] 同步到服务器
                // 注意：为了确保服务器处理删除逻辑，我们可以重用 ADD_COMMENT 类型
                // 因为服务器逻辑只是简单的 updatePost (覆盖整个 Post 对象)
                dataManager.updatePost(post, RequestType.ADD_COMMENT);
                
                loadComments();
            }
        });
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}