package com.test.demo.entity;

import java.sql.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 */
public class Images {
    private int id;
    private String tag;
    private String url;
    private Date uploadTime;
    private String uploadUserId;
    private String batchNo;
    private String bak;
    private List<Classifications> classifications;

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(String uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getBak() {
        return bak;
    }

    public void setBak(String bak) {
        this.bak = bak;
    }

    public List<Classifications> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<Classifications> classifications) {
        this.classifications = classifications;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
