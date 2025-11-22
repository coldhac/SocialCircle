package main.common;

import java.io.Serializable;

// 请求类型枚举
public enum RequestType implements Serializable {
    LOGIN,          // 登录
    REGISTER,       // 注册
    GET_ALL_POSTS,  // 获取所有帖子
    ADD_POST,       // 发布帖子
    DELETE_POST,    // 删除帖子
    TOGGLE_LIKE,    // 点赞
    ADD_COMMENT,    // 添加评论
    REMOVE_COMMENT, // 删除评论
    LOGOUT          // 登出
}
