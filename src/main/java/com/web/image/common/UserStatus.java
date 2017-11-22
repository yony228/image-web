package com.web.image.common;

/**
 * 用户状态
 * Created by Administrator on 2017/7/17.
 */
public enum UserStatus {
    NORMAL("正常", 0), BLOCK("停用", 1);

    UserStatus(String name, int status) {
        this.name = name;
        this.status = status;
    }

    private String name;
    private int status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
