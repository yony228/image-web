package com.web.image.dao;

import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface IImagesDao {
    int updateByTag(Map tmp);

    int updateByClassification(Map tmp);

    /**
     * 修改图片所属人
     *
     * @param uploadUserId
     * @param ids
     * @return
     */
    int updateUploadUserByIds(String uploadUserId, String ids);
}
