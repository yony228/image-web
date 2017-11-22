package com.web.image.entity;

/**
 * Created by Administrator on 2017/5/22.
 */
public class Classifications {
    private int id;
    //分类名称
    private String classification;
    //分类描述
    private String description;
    //分类别名
    private String alias;
    //模型名称
    private String modelName;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassification() {
        return classification;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
