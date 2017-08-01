package com.test.demo.dao.impl;

import com.test.demo.dao.DImageTypesDao;
import com.test.demo.dao.TrainClassDao;
import com.test.demo.entity.TrainClass;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
@Repository
public class DImageTypesDaoImpl implements DImageTypesDao {
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
