package com.test.demo.dao.classifications.impl;

import com.test.demo.dao.classifications.interfaces.IClassificationsDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
@Repository
public class ClassificationsDaoImpl implements IClassificationsDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationsDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    //分页查询分类
    @Override
    public List<Map<String, Object>> queryClassifications(Map param, Map pageInfo) {
        StringBuffer sqlBuffer = new StringBuffer(" SELECT * FROM classifications WHERE 1=1 ");
        if (StringUtils.isNotBlank((String) param.get("id"))) {
            sqlBuffer.append("AND id = :id ");
        }
        if (StringUtils.isNotBlank((String) param.get("classification"))) {
            sqlBuffer.append("AND classification = :classification ");
        }
        if (StringUtils.isNotBlank((String) param.get("alias"))) {
            sqlBuffer.append("AND alias = :alias ");
        }
        if (StringUtils.isNotBlank((String) param.get("des"))) {
            sqlBuffer.append("AND des = :des ");
        }
        if (StringUtils.isNotBlank((String) param.get("model_id"))) {
            sqlBuffer.append("AND model_id = :model_id ");
        }
        if (StringUtils.isNotBlank((String) param.get("not_id"))) {
            sqlBuffer.append(" AND id <> :not_id");
        }
        sqlBuffer.append(" ORDER BY id desc");
        if (null != pageInfo) {
            sqlBuffer.append(" LIMIT " + MapUtils.getString(pageInfo, "offset", "0") + "," + MapUtils.getString(pageInfo, "limit", "10"));
        }
        LOGGER.info("queryClassifications sql = " + sqlBuffer.toString());
        return jdbcTemplate.queryForList(sqlBuffer.toString(), param);
    }

    @Override
    public List<Map<String, Object>> fuzzyQueryClassifications(Map param, Map pageInfo) {
        StringBuffer sqlBuffer = new StringBuffer("SELECT c.*,m.des modelDes FROM classifications c left join models m on c.model_id=m.id where 1=1 ");
        if (StringUtils.isNotBlank((String) param.get("model_id"))) {
            sqlBuffer.append("AND c.model_id = :model_id ");
        }
        if (StringUtils.isNotBlank((String) param.get("classification"))) {
            sqlBuffer.append("AND c.classification LIKE :classification ");
            param.put("classification", "%" + MapUtils.getString(param, "classification") + "%");
        }
        if (StringUtils.isNotBlank((String) param.get("alias"))) {
            sqlBuffer.append("AND c.alias LIKE :alias ");
            param.put("alias", "%" + MapUtils.getString(param, "alias") + "%");
        }
        if (StringUtils.isNotBlank((String) param.get("des"))) {
            sqlBuffer.append("AND c.des LIKE :des ");
            param.put("des", "%" + MapUtils.getString(param, "des") + "%");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "condition"))) {
            sqlBuffer.append(" and (c.classification LIKE :condition ");
            sqlBuffer.append("OR c.alias LIKE :condition ");
            sqlBuffer.append("OR c.des LIKE :condition) ");

            param.put("condition", "%" + MapUtils.getString(param, "condition") + "%");
        }
        if (StringUtils.isNotBlank((String) param.get("notClassId"))) {
            sqlBuffer.append("AND c.id not in (" + param.get("notClassId") + ")");
        }
        sqlBuffer.append(" ORDER BY c.id desc");
        if (null != pageInfo) {
            sqlBuffer.append(" LIMIT " + MapUtils.getString(pageInfo, "offset", "0") + "," + MapUtils.getString(pageInfo, "limit", "10"));
        }
        LOGGER.info("fuzzyQueryClassifications sql = " + sqlBuffer.toString());
        return jdbcTemplate.queryForList(sqlBuffer.toString(), param);
    }

    @Override
    public int queryClassificationCount(Map param) {
        StringBuffer sqlBuffer = new StringBuffer(" SELECT count(*) FROM classifications WHERE 1=1 ");
        if (StringUtils.isNotBlank((String) param.get("id"))) {
            sqlBuffer.append("AND id = :id ");
        }
        if (StringUtils.isNotBlank((String) param.get("classification"))) {
            sqlBuffer.append("AND classification = :classification ");
        }
        if (StringUtils.isNotBlank((String) param.get("alias"))) {
            sqlBuffer.append("AND alias = :alias ");
        }
        if (StringUtils.isNotBlank((String) param.get("des"))) {
            sqlBuffer.append("AND des = :des ");
        }
        if (StringUtils.isNotBlank((String) param.get("model_id"))) {
            sqlBuffer.append("AND model_id = :model_id ");
        }
        if (StringUtils.isNotBlank((String) param.get("not_id"))) {
            sqlBuffer.append(" AND id <> :not_id");
        }

        LOGGER.info("queryClassificationCount sql = " + sqlBuffer.toString());
        return jdbcTemplate.queryForObject(sqlBuffer.toString(), param, int.class);
    }

    //todo: 查询分类下图片数量
    @Override
    public long queryImageCountByClassification(long id) {
        long count = 0;
        Map tmp = new HashMap();
        StringBuffer sqlBuffer = new StringBuffer(" SELECT count(*) FROM r_images_classifications WHERE classification_id = :id ");
        tmp.put("id", id);
        count = jdbcTemplate.queryForObject(sqlBuffer.toString(), tmp, Integer.class);
        return count;
    }

    //更新
    @Override
    public int updateById(Map param) {
        int reValue = 0;
        if (!param.containsKey("id") || StringUtils.isBlank(MapUtils.getString(param, "id"))) {
            return reValue;
        }
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE classifications SET ");
        if (StringUtils.isNotBlank(MapUtils.getString(param, "classification"))) {
            sqlBuffer.append(" classification = :classification, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "alias"))) {
            sqlBuffer.append(" alias = :alias, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "des"))) {
            sqlBuffer.append(" des = :des, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "model_id"))) {
            sqlBuffer.append(" model_id = :model_id, ");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 2);
        sqlBuffer.append(" WHERE id = :id");
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }

    @Override
    public int delete(long id) {
        StringBuffer sql = new StringBuffer("DELETE FROM classifications WHERE id = :id");
        Map param = new HashMap();
        param.put("id", id);
        return jdbcTemplate.update(sql.toString(), param);
    }

    @Override
    public int save(Map param) {
        Iterator i = param.keySet().iterator();
        StringBuffer placeholder_names = new StringBuffer();
        StringBuffer placeholder_values = new StringBuffer();
        while (i.hasNext()) {
            switch ((String) i.next()) {
                case "classification":
                    placeholder_names.append("classification, ");
                    placeholder_values.append(":classification, ");
                    break;
                case "alias":
                    placeholder_names.append("alias, ");
                    placeholder_values.append(":alias, ");
                    break;
                case "des":
                    placeholder_names.append("des, ");
                    placeholder_values.append(":des, ");
                    break;
                case "model_id":
                    placeholder_names.append("model_id, ");
                    placeholder_values.append(":model_id, ");
                    break;
            }
        }
        StringBuffer sql = new StringBuffer("INSERT INTO classifications(");
        sql.append(placeholder_names.subSequence(0, placeholder_names.length() - 2)).append(") VALUES (").append(placeholder_values.substring(0, placeholder_values.length() - 2)).append(")");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(param);
        jdbcTemplate.update(sql.toString(), parameterSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public int batchImageClass(String oldClassId, String newClassId) {
        LOGGER.info("batchImageClass params oldClassId=" + oldClassId + ",newClassId=" + newClassId);
        String sql = "INSERT into r_images_classifications(img_id,classification_id)\n" +
                "SELECT img_id,:newClassId from r_images_classifications where classification_id=:oldClassId";

        Map param = new HashMap();
        param.put("oldClassId", oldClassId);
        param.put("newClassId", newClassId);
        return jdbcTemplate.update(sql.toString(), param);
    }
}