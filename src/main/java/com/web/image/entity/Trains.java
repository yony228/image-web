package com.web.image.entity;

import java.util.Date;

/**
 * 训练实体类
 *
 * @author zll
 *         2017
 */
public class Trains {
    //ID
    private int id;

    //训练号
    private String trainNo;

    //训练集分片数量
    private int numShard;

    //验证集数量
    private int numValidation;

    //训练图片数量
    private int numTrain;

    //训练步数
    private int trainStepNum;

    //训练状态（-1.训练失败； 0.等待训练；1.训练中；2.训练完成；3.已产生模型）
    private int status;

    //备注
    private String description;

    //创建时间
    private Date createTime;

    //创建人
    private int createUserId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public int getNumShard() {
        return numShard;
    }

    public void setNumShard(int numShard) {
        this.numShard = numShard;
    }

    public int getNumValidation() {
        return numValidation;
    }

    public void setNumValidation(int numValidation) {
        this.numValidation = numValidation;
    }

    public int getNumTrain() {
        return numTrain;
    }

    public void setNumTrain(int numTrain) {
        this.numTrain = numTrain;
    }

    public int getTrainStepNum() {
        return trainStepNum;
    }

    public void setTrainStepNum(int trainStepNum) {
        this.trainStepNum = trainStepNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }
}
