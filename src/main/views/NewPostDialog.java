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

// 发布新动态对话框
public class NewPostDialog extends Dialog<Post> {
    
    private TextArea contentArea;
    private TextField imageField;
    private File imageFile;
    
    public NewPostDialog() {
        setTitle("发布动态");
        setHeaderText("分享新鲜事");
        
        ButtonType postBtn = new ButtonType("发布", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(postBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        contentArea = new TextArea();
        contentArea.setPromptText("说点什么...");
        contentArea.setPrefRowCount(5);
        contentArea.setWrapText(true);
        
        imageField = new TextField();
        imageField.setPromptText("未选择图片");
        imageField.setEditable(false);
        
        Button chooseBtn = new Button("选择图片");
        chooseBtn.setOnAction(e -> chooseImage());
        
        grid.add(new Label("内容:"), 0, 0);
        grid.add(contentArea, 1, 0);
        grid.add(new Label("图片:"), 0, 1);
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
        fileChooser.setTitle("选择图片");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("图片", "*.png", "*.jpg", "*.gif")
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
        
        // 验证图片
        String imagePath = "";
        if (imageFile != null) {
            error = ValidationUtil.validateImage(imageFile.getAbsolutePath());
            if (error != null) {
                showError(error);
                return null;
            }
            imagePath = imageFile.getAbsolutePath();
        }
        
        // 创建帖子
        Post post = new Post(DataManager.getInstance().getCurrentUser(), content, imagePath);
        DataManager.getInstance().addPost(post);
        
        return post;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
