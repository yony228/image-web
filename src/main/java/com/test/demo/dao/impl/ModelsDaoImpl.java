package com.test.demo.dao.impl;

import com.test.demo.dao.ModelsDao;
import com.test.demo.dao.TrainClassDao;
import com.test.demo.entity.TrainClass;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
@Repository
public class ModelsDaoImpl implements ModelsDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelsDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> queryModels(Map<String, Object> paramMap, Map pageInfo) {
        StringBuffer sqlBuffer = new StringBuffer(" SELECT * FROM models WHERE 1=1 ");
        if (StringUtils.isNotBlank((String) paramMap.get("id"))) {
            sqlBuffer.append("AND id = :id ");
        }
        if (StringUtils.isNotBlank((String) paramMap.get("name"))) {
            sqlBuffer.append("AND name = :name ");
        }
        if (StringUtils.isNotBlank((String) paramMap.get("des"))) {
            sqlBuffer.append("AND des = :des ");
        }
        if (StringUtils.isNotBlank((String) paramMap.get("user_id"))) {
            sqlBuffer.append("AND user_id = :user_id ");
        }
        if (StringUtils.isNotBlank((String) paramMap.get("not_id"))) {
            sqlBuffer.append(" AND id <> :not_id");
        }
        if (StringUtils.isNotBlank((String) paramMap.get("condition"))) {
            sqlBuffer.append(" AND (name LIKE :condition ");
            sqlBuffer.append(" OR des LIKE :condition ) ");
            paramMap.put("condition", "%" + paramMap.get("condition") + "%");
        }
        sqlBuffer.append(" ORDER BY id desc");
        if (null != pageInfo) {
            sqlBuffer.append(" LIMIT " + MapUtils.getString(pageInfo, "offset", "0") + "," + MapUtils.getString(pageInfo, "limit", "10"));
        }
        LOGGER.info("queryModels sql = " + sqlBuffer.toString());
        return jdbcTemplate.queryForList(sqlBuffer.toString(), paramMap);
    }

    @Override
    public int save(Map<String, Object> param) {
        Iterator i = param.keySet().iterator();
        StringBuffer placeholder_names = new StringBuffer();
        StringBuffer placeholder_values = new StringBuffer();
        while (i.hasNext()) {
            switch ((String) i.next()) {
                case "name":
                    placeholder_names.append("name, ");
                    placeholder_values.append(":name, ");
                    break;
                case "des":
                    placeholder_names.append("des, ");
                    placeholder_values.append(":des, ");
                    break;
                case "user_id":
                    placeholder_names.append("user_id, ");
                    placeholder_values.append(":user_id, ");
                    break;
            }
        }
        StringBuffer sql = new StringBuffer("INSERT INTO models(");
        sql.append(placeholder_names + "create_time").append(") VALUES (").append(placeholder_values + "now()").append(")");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(param);
        jdbcTemplate.update(sql.toString(), parameterSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public int update(Map<String, Object> param) {
        int reValue = 0;
        if (!param.containsKey("id") || StringUtils.isBlank(MapUtils.getString(param, "id"))) {
            return reValue;
        }
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE models SET ");
        if (StringUtils.isNotBlank(MapUtils.getString(param, "name"))) {
            sqlBuffer.append(" name = :name, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "des"))) {
            sqlBuffer.append(" des = :des, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "status"))) {
            sqlBuffer.append(" status = :status, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "showRateTop"))) {
            sqlBuffer.append(" show_rate_top = :showRateTop, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "showRateBottom"))) {
            sqlBuffer.append(" show_rate_bottom = :showRateBottom, ");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 2);
        sqlBuffer.append(" WHERE id = :id");
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }

    @Override
    public int queryClassCountByModelId(int modelsId) {
        int count = 0;
        StringBuffer sqlBuffer = new StringBuffer(" SELECT count(*) FROM classifications WHERE model_id = :modelsId");
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("modelsId", modelsId);
        count = jdbcTemplate.queryForObject(sqlBuffer.toString(), tmp, Integer.class);
        return count;
    }

    //TODO
    @Override
    public int updateStatus(String modelId) {
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE models SET status=1 where id<>:modelId and status=2");

        Map<String, Object> param = new HashMap<>();
        param.put("modelId", modelId);
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }

    @Override
    public Map<String, Object> getOnlineModels() {
        StringBuffer sqlBuffer = new StringBuffer(" SELECT * FROM models WHERE status = 2 ");
        return jdbcTemplate.queryForMap(sqlBuffer.toString(), new HashMap<>());
    }
}
