package com.web.image.dao.impl;

import com.web.image.dao.IImagesDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
@Repository
public class ImagesDaoImpl implements IImagesDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagsDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int updateByTag(Map param) {
        if (!param.containsKey("tag_id") || StringUtils.isBlank(MapUtils.getString(param, "new_tag_id"))) {
            return 0;
        }
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE r_images_tags SET ");
        sqlBuffer.append(" tag_id = :new_tag_id ");
        sqlBuffer.append(" WHERE tag_id = :tag_id");
        LOGGER.info("updateByTag sql =" + sqlBuffer.toString());
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }

    //更新Classification
    @Override
    public int updateByClassification(Map param) {
        if (!param.containsKey("classification_id") || StringUtils.isBlank(MapUtils.getString(param, "new_classification_id"))) {
            return 0;
        }
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE r_images_classifications SET ");
        sqlBuffer.append(" classification_id = :new_classification_id ");
        sqlBuffer.append(" WHERE classification_id = :classification_id");
        LOGGER.info("updateByClassification sql =" + sqlBuffer.toString());
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }

    @Override
    public int updateUploadUserByIds(String uploadUserId, String ids) {
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE images SET ");
        sqlBuffer.append(" upload_user_id = concat(:upload_user_id,upload_user_id) ");
        sqlBuffer.append(" WHERE id in (" + ids + ") ");
        LOGGER.info("updateUploadUserByIds sql =" + sqlBuffer.toString());

        Map<String, Object> param = new HashMap();
        param.put("upload_user_id", uploadUserId);
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }
}
