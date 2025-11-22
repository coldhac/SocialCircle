package main.common;

import java.io.Serializable;

// 网络请求对象
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private RequestType type;
    private Object data; // 可以携带 User, Post, Comment 等数据
    private String extra; // 额外信息，如 username, password 等

    public Request(RequestType type, Object data) {
        this.type = type;
        this.data = data;
    }
    
    public Request(RequestType type, Object data, String extra) {
        this.type = type;
        this.data = data;
        this.extra = extra;
    }

    public RequestType getType() { return type; }
    public Object getData() { return data; }
    public String getExtra() { return extra; }
}
