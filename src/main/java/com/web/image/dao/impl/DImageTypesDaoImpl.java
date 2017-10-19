package com.web.image.dao.impl;

import com.web.image.dao.IDImageTypesDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
@Repository
public class DImageTypesDaoImpl implements IDImageTypesDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DImageTypesDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> queryImageTypes(Map<String, String> paramMap) {
        StringBuffer sqlBuffer = new StringBuffer(" SELECT * FROM d_image_types WHERE 1=1 ");
        if (StringUtils.isNotBlank(paramMap.get("id"))) {
            sqlBuffer.append("AND id = :id ");
        }
        if (StringUtils.isNotBlank(paramMap.get("value"))) {
            sqlBuffer.append("AND value = :value ");
        }
        if (StringUtils.isNotBlank(paramMap.get("des"))) {
            sqlBuffer.append("AND des = :des ");
        }
        sqlBuffer.append(" ORDER BY id desc");
        LOGGER.info("queryImageTypes sql = " + sqlBuffer.toString());
        return jdbcTemplate.queryForList(sqlBuffer.toString(), paramMap);
    }
}
