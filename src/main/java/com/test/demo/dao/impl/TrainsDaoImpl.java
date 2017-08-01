package com.test.demo.dao.impl;

import com.test.demo.dao.TrainsDao;
import com.test.demo.entity.Trains;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
@Repository
public class TrainsDaoImpl implements TrainsDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainsDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int saveTrains(Map<String, Object> trainsMap) {
        Iterator i = trainsMap.keySet().iterator();
        StringBuffer placeholder_names = new StringBuffer();
        StringBuffer placeholder_values = new StringBuffer();
        while (i.hasNext()) {
            switch ((String) i.next()) {
                case "trainNo":
                    placeholder_names.append("train_no, ");
                    placeholder_values.append(":trainNo, ");
                    break;
                case "numShard":
                    placeholder_names.append("num_shard, ");
                    placeholder_values.append(":numShard, ");
                    break;
                case "numValidation":
                    placeholder_names.append("num_validation, ");
                    placeholder_values.append(":numValidation, ");
                    break;
                case "numTrain":
                    placeholder_names.append("num_train, ");
                    placeholder_values.append(":numTrain, ");
                    break;
                case "trainStepNum":
                    placeholder_names.append("train_step_num, ");
                    placeholder_values.append(":trainStepNum, ");
                    break;
                case "status":
                    placeholder_names.append("status, ");
                    placeholder_values.append(":status, ");
                    break;
                case "description":
                    placeholder_names.append("description, ");
                    placeholder_values.append(":description, ");
                    break;
                case "createUserId":
                    placeholder_names.append("create_user_id, ");
                    placeholder_values.append(":createUserId, ");
                    break;
            }
        }
        StringBuffer sql = new StringBuffer("INSERT INTO trains(");
        sql.append(placeholder_names + "create_time").append(") VALUES (").append(placeholder_values + "now()").append(")");
        return jdbcTemplate.update(sql.toString(), trainsMap);
    }

    @Override
    public Map<String, Object> queryTrainsByModelId(String modelId) {
        StringBuffer sqlBuffer = new StringBuffer("SELECT * FROM trains WHERE 1=1 ");
        if (StringUtils.isNotBlank(modelId)) {
            sqlBuffer.append("AND model_id = :modelId");
        }
        LOGGER.info("queryTrainsByModelId sql = " + sqlBuffer.toString());
        Map param = new HashMap();
        param.put("modelId", modelId);
        return jdbcTemplate.queryForMap(sqlBuffer.toString(), param);
    }

    @Override
    public int updateTrainsByTrainNo(Map<String, Object> trainsMap, String trainNo) {
        if (StringUtils.isBlank(trainNo)) {
            return 0;
        }
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE trains SET ");
        if (StringUtils.isNotBlank(MapUtils.getString(trainsMap, "model_id"))) {
            sqlBuffer.append(" model_id = :model_id, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(trainsMap, "status"))) {
            sqlBuffer.append(" status = :status, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(trainsMap, "model_url"))) {
            sqlBuffer.append(" model_url = :model_url, ");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 2);
        sqlBuffer.append(" WHERE train_no = :trainNo");
        trainsMap.put("trainNo", trainNo);
        return jdbcTemplate.update(sqlBuffer.toString(), trainsMap);
    }
}
