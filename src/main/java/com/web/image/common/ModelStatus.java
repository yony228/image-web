package com.web.image.common;

/**
 * 模型状态
 * Created by Administrator on 2017/8/21.
 */
public enum ModelStatus {
    DEL_STATUS("删除", -1), OFF_TRAIN("下线", 1), ON_FINISH("上线", 2);

    ModelStatus(String name, int status) {
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
