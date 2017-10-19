package com.web.image.dao.impl;

import com.web.image.dao.IImageTypesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/5/27.
 */
@Service
public class ImageTypesDaoImpl implements IImageTypesDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTypesDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int queryTypeIdByValues(String values) {
        LOGGER.info("queryTypeIdByValues params=" + values);
        //查询对应标签id
        Integer typeId = jdbcTemplate.query("SELECT id from d_image_types WHERE value='" + values + "'", new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while (resultSet.next()) {
                    return resultSet.getInt("id");
                }
                return 0;
            }
        });
        return typeId;
    }
}
