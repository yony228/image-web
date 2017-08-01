package com.test.demo.entity;

/**
 * Created by Admin on 2017/6/8.
 */
public class Tags {
    //主键唯一标识符
    private int id;
    //所属用户id
    private int userId;
    //标签名称
    private String tag;
    //标签描述
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
