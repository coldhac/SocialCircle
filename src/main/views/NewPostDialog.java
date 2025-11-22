package main.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.data.DataManager;
import main.models.Post;
import main.utils.ValidationUtil;
import java.io.File;
import java.nio.file.Files;

// 发布新动态对话框
public class NewPostDialog extends Dialog<Post> {
    
    private TextArea contentArea;
    private TextField imageField;
    private File imageFile;
    
    public NewPostDialog() {
        setTitle("New Post");
        setHeaderText("Share your life!");
        
        ButtonType postBtn = new ButtonType("Post", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(postBtn, cancelBtn);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        contentArea = new TextArea();
        contentArea.setPromptText("Say something...");
        contentArea.setPrefRowCount(5);
        contentArea.setWrapText(true);
        
        imageField = new TextField();
        imageField.setPromptText("No image selected");
        imageField.setEditable(false);
        
        Button chooseBtn = new Button("Select Image");
        chooseBtn.setOnAction(e -> chooseImage());
        
        grid.add(new Label("Content:"), 0, 0);
        grid.add(contentArea, 1, 0);
        grid.add(new Label("Image:"), 0, 1);
        grid.add(imageField, 1, 1);
        grid.add(chooseBtn, 2, 1);
        
        getDialogPane().setContent(grid);
        
        setResultConverter(dialogButton -> {
            if (dialogButton == postBtn) {
                return createPost();
            }
            return null;
        });
    }
    
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
        );
        
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        imageFile = fileChooser.showOpenDialog(stage);
        
        if (imageFile != null) {
            imageField.setText(imageFile.getName());
        }
    }
    
    private Post createPost() {
        String content = contentArea.getText().trim();
        
        // 验证内容
        String error = ValidationUtil.validateContent(content);
        if (error != null) {
            showError(error);
            return null;
        }
        
        String imagePath = "";
        byte[] imageBytes = null;
        
        // 处理图片
        if (imageFile != null) {
            error = ValidationUtil.validateImage(imageFile.getAbsolutePath());
            if (error != null) {
                showError(error);
                return null;
            }
            imagePath = imageFile.getName(); // 只保存文件名用于显示
            try {
                // 读取文件为字节数组
                imageBytes = Files.readAllBytes(imageFile.toPath());
            } catch (Exception e) {
                showError("Failed to read image file");
                return null;
            }
        }
        
        // 创建帖子
        Post post = new Post(DataManager.getInstance().getCurrentUser(), content, imagePath);
        if (imageBytes != null) {
            post.setImageBytes(imageBytes);
        }
        
        DataManager.getInstance().addPost(post);
        
        return post;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
