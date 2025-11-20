package main.models;

// 接口 - 可以被点赞的东西
public interface Likeable {
    
    // 切换点赞状态
    boolean toggleLike(String username);
    
    // 获取点赞数
    int getLikeCount();
    
    // 检查是否被某人点赞
    boolean isLikedBy(String username);
}
