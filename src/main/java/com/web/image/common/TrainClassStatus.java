package com.web.image.common;

/**
 * 训练状态
 * Created by Administrator on 2017/6/21.
 */
public enum TrainClassStatus {
    WAIT_TRAIN("等待训练", 0), START_TRAIN("训练中", 1), START_FINISH("训练完成", 2);

    TrainClassStatus(String name, int status) {
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
