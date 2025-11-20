package main.models;

import java.io.Serializable;

// 评论类 - 继承SocialEntity，只支持一级评论
public class Comment extends SocialEntity implements Serializable {
    
    private Post parentPost;
    
    public Comment(User author, String content, Post parentPost) {
        super(author, content);
        this.parentPost = parentPost;
    }
    
    @Override
    public String getType() {
        return "Comment";
    }
    
    public Post getParentPost() {
        return parentPost;
    }
}
