package com.web.image.dao.impl;

import com.web.image.dao.TrainClassDao;
import com.web.image.entity.TrainClass;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
@Repository
public class TrainClassDaoImpl implements TrainClassDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainClassDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int saveTrainClassBatch(List<TrainClass> trainClassList) {
        //添加关系表
        String relSql = "INSERT INTO train_class(train_no,class_id,status,create_time,create_user_id) VALUES (?,?,?,now(),?)";
        jdbcTemplate.batchUpdate(relSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, trainClassList.get(i).getTrainNo());
                ps.setInt(2, trainClassList.get(i).getClassId());
                ps.setInt(3, trainClassList.get(i).getStatus());
                ps.setInt(4, trainClassList.get(i).getCreateUserId());
            }

            @Override
            public int getBatchSize() {
                return trainClassList.size();
            }
        });

        return 0;
    }

    @Override
    public List<Map<String, Object>> queryTrainClass(Map<String, Object> param, Map pageInfo) {
        StringBuffer sqlBuffer = new StringBuffer("SELECT tc.train_no,max(t.status) status,max(t.train_step_num) train_step_num,DATE_FORMAT(max(tc.create_time),'%Y-%m-%d %H:%i:%s') create_time,group_concat(c.alias) alias,m.des modelDes " +
                "FROM train_class tc,classifications c,trains t left join models m on t.model_id=m.id " +
                "WHERE tc.class_id=c.id and tc.train_no=t.train_no ");
        if (StringUtils.isNotBlank((String) param.get("id"))) {
            sqlBuffer.append("AND tc.id = :id ");
        }
        if (StringUtils.isNotBlank((String) param.get("train_no"))) {
            sqlBuffer.append("AND tc.train_no LIKE :train_no ");
            param.put("train_no", "%" + param.get("train_no") + "%");
        }
        if (StringUtils.isNotBlank((String) param.get("class_id"))) {
            sqlBuffer.append("AND tc.class_id = :class_id ");
        }
        if (StringUtils.isNotBlank((String) param.get("status"))) {
            sqlBuffer.append("AND t.status = :status ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "condition"))) {
            sqlBuffer.append(" and (tc.train_no LIKE :condition ");
            sqlBuffer.append(" OR m.des LIKE :condition) ");

            param.put("condition", "%" + MapUtils.getString(param, "condition") + "%");
        }
        sqlBuffer.append(" group BY tc.train_no ORDER BY tc.create_time desc");
        if (null != pageInfo) {
            sqlBuffer.append(" LIMIT " + MapUtils.getString(pageInfo, "offset", "0") + "," + MapUtils.getString(pageInfo, "limit", "10"));
        }
        LOGGER.info("queryTrainClass sql = " + sqlBuffer.toString());
        return namedParameterJdbcTemplate.queryForList(sqlBuffer.toString(), param);
    }

    @Override
    public List<Map<String, Object>> queryClassByTrainNo(String trainNo) {
        StringBuffer sqlBuffer = new StringBuffer("SELECT c.* FROM train_class tc,classifications c " +
                "WHERE tc.class_id=c.id ");
        if (StringUtils.isNotBlank(trainNo)) {
            sqlBuffer.append("AND tc.train_no = :train_no");
        }
        LOGGER.info("queryClassByTrainNo sql = " + sqlBuffer.toString());
        Map param = new HashMap();
        param.put("train_no", trainNo);
        return namedParameterJdbcTemplate.queryForList(sqlBuffer.toString(), param);
    }
}
