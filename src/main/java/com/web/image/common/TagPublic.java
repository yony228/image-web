package com.web.image.common;

/**
 * 模型状态
 * Created by Administrator on 2017/8/21.
 */
public enum TagPublic {
    PUBLIC("公开", 1), PRIVATE("不公开", 2);

    TagPublic(String name, int status) {
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
