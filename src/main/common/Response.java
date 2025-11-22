package main.common;

import java.io.Serializable;

// 网络响应对象
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String message;
    private Object payload; // 返回的数据，如帖子列表

    public Response(boolean success, String message, Object payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getPayload() { return payload; }
}
