package com.test.demo.entity;

import java.util.Date;

public class Trains {
    private int id;
    private String trainNo;
    private int numShard;
    private int numValidation;
    private int numTrain;
    private int trainStepNum;
    private int status;
    private String description;
    private Date createTime;
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
