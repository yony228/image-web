package com.web.image.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * 读取配置文件
 * Created by Administrator on 2017/5/20.
 */
@PropertySource(value = "classpath:/file_web.properties")
@Service
public class FileWebConf {
    @Value("${uploadUrl}")
    private String uploadUrl;

    @Value("${clintServer}")
    private String clintServer;

    @Value("${clintServerPort}")
    private String clintServerPort;

    @Value("${showLabelRateTop}")
    private float showLabelRateTop;

    @Value("${showLabelRateBottom}")
    private float showLabelRateBottom;

    @Value("${picFullUrl}")
    private String picFullUrl;

    @Value("${trainHost}")
    private String trainHost;

    @Value("${trainPort}")
    private String trainPort;

    @Value("${trainLogUrl}")
    private String trainLogUrl;

    @Value("${trainModelsUrl}")
    private String trainModelsUrl;

    @Value("${modelOnlineUrl}")
    private String modelOnlineUrl;

    public String getTrainModelsUrl() {
        return trainModelsUrl;
    }

    public String getModelOnlineUrl() {
        return modelOnlineUrl;
    }

    public String getTrainLogUrl() {
        return trainLogUrl;
    }

    public String getTrainHost() {
        return trainHost;
    }

    public String getTrainPort() {
        return trainPort;
    }

    public String getPicFullUrl() {
        return picFullUrl;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getClintServer() {

        return clintServer;
    }

    public float getShowLabelRateBottom() {
        return showLabelRateBottom;
    }

    public float getShowLabelRateTop() {
        return showLabelRateTop;
    }

    public String getClintServerPort() {
        return clintServerPort;
    }
}
