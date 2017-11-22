package com.web.image.entity.dto;

import com.web.image.entity.Classifications;

/**
 * Created by Administrator on 2017/5/27.
 */
public class ClassificationsDto extends Classifications {
    private int countImages;//标签图片总数统计

    public int getCountImages() {
        return countImages;
    }

    public void setCountImages(int countImages) {
        this.countImages = countImages;
    }
}
