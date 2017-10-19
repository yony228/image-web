package com.web.image.dao.impl;

import com.web.image.common.BaseUtil;
import com.web.image.dao.IClassificationsDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @Autowired
    private JdbcTemplate jdbcTemplate1;

    /**
     * 分页查询分类
     *
     * @param param
     * @param pageInfo
     * @return
     */
    @Override
    public List<Map<String, Object>> queryClassifications(Map<String, String> param, Map pageInfo) {
        StringBuffer sqlBuffer = new StringBuffer(" SELECT c.* FROM classifications c left join r_model_class rmc on c.id=rmc.class_id where 1=1 ");
        if (StringUtils.isNotBlank(param.get("id"))) {
            sqlBuffer.append("AND c.id = :id ");
        }
        if (StringUtils.isNotBlank(param.get("classification"))) {
            sqlBuffer.append("AND c.classification = :classification ");
        }
        if (StringUtils.isNotBlank(param.get("alias"))) {
            sqlBuffer.append("AND c.alias = :alias ");
        }
        if (StringUtils.isNotBlank(param.get("des"))) {
            sqlBuffer.append("AND c.des = :des ");
        }
        if (StringUtils.isNotBlank(param.get("create_user_id"))) {
            sqlBuffer.append("AND c.create_user_id = :create_user_id ");
        }

        if (StringUtils.isNotBlank(param.get("likeClassification"))) {
            sqlBuffer.append("AND c.classification LIKE :likeClassification ");
            param.put("likeClassification", "%" + MapUtils.getString(param, "likeClassification") + "%");
        }
        if (StringUtils.isNotBlank(param.get("likeAlias"))) {
            sqlBuffer.append("AND c.alias LIKE :likeAlias ");
            param.put("likeAlias", "%" + MapUtils.getString(param, "likeAlias") + "%");
        }
        if (StringUtils.isNotBlank(param.get("likeDes"))) {
            sqlBuffer.append("AND c.des LIKE :likeDes ");
            param.put("likeDes", "%" + MapUtils.getString(param, "likeDes") + "%");
        }

        if (StringUtils.isNotBlank(param.get("not_id"))) {
            sqlBuffer.append(" AND c.id <> :not_id");
        }
        if (StringUtils.isNotBlank(param.get("notClassId"))) {
            sqlBuffer.append("AND c.id not in (" + param.get("notClassId") + ")");
        }
        if (StringUtils.isNotBlank(param.get("model_id"))) {
            sqlBuffer.append("AND rmc.model_id = :model_id");
        }
        sqlBuffer.append(" group by c.id ORDER BY c.id desc");
        if (null != pageInfo) {
            sqlBuffer.append(" LIMIT " + MapUtils.getString(pageInfo, "offset", "0") + "," + MapUtils.getString(pageInfo, "limit", "10"));
        }
        LOGGER.info("queryClassifications sql = " + sqlBuffer.toString());
        return jdbcTemplate.queryForList(sqlBuffer.toString(), param);
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
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 2);
        sqlBuffer.append(" WHERE id = :id");
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }

    @Override
    public int updateAllById(Map param) {
        int reValue = 0;
        if (!param.containsKey("id") || StringUtils.isBlank(MapUtils.getString(param, "id"))) {
            return reValue;
        }
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE classifications SET ");
        if (param.containsKey("classification")) {
            sqlBuffer.append(" classification = :classification, ");
        }
        if (param.containsKey("alias")) {
            sqlBuffer.append(" alias = :alias, ");
        }
        if (param.containsKey("des")) {
            sqlBuffer.append(" des = :des, ");
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
                case "create_user_id":
                    placeholder_names.append("create_user_id, ");
                    placeholder_values.append(":create_user_id, ");
                    break;
                case "create_time":
                    placeholder_names.append("create_time, ");
                    placeholder_values.append(":create_time, ");
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

    @Override
    public String queryClassDescription(List<String> classList, String modelId) {
        if (classList == null || classList.size() == 0) {
            return "无";
        }

        String sqlParams = BaseUtil.getSqlParams(classList);
        LOGGER.info("queryClassDescription sqlParams:" + sqlParams);

        StringBuffer sb = new StringBuffer("SELECT GROUP_CONCAT(c.alias SEPARATOR ' ') alias from classifications c,r_model_class rmc where c.id=rmc.class_id");
        if (StringUtils.isNotBlank(modelId)) {
            sb.append(" and rmc.model_id=" + modelId);
        }
        if (StringUtils.isNotBlank(sqlParams)) {
            sb.append(" AND c.classification IN (" + sqlParams + ")");
        }
        String returnStr = jdbcTemplate.query(sb.toString(), new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while (resultSet.next()) {
                    return resultSet.getString("alias");
                }
                return "无";
            }
        });
        return returnStr;
    }

    @Override
    public boolean saveRModelClassBatch(List<String> classIdList, int modelId) {
        //添加关系表
        String relSql = "INSERT INTO r_model_class(model_id,class_id) VALUES (?,?)";
        jdbcTemplate1.batchUpdate(relSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, modelId);
                ps.setString(2, classIdList.get(i));
            }

            @Override
            public int getBatchSize() {
                return classIdList.size();
            }
        });
        return true;
    }

    /**
     * 复制图片关系(从标签复制到分类)
     *
     * @param tagId
     * @param classId
     */
    @Override
    public void copyImagesRel(String tagId, String classId) {
        //查询对应图片id
        StringBuffer sqlBuffer = new StringBuffer("select img_id from r_images_tags where tag_id=" + tagId);
        sqlBuffer.append(" and img_id not in(");
        sqlBuffer.append(" select img_id from r_images_classifications where classification_id=" + classId);
        sqlBuffer.append(" )");
        LOGGER.info("insertPic sqlParams:" + sqlBuffer.toString());
        List<Integer> imgList = jdbcTemplate.query(sqlBuffer.toString(), new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt("img_id");
            }
        });

        //添加关系表
        String relSql = "INSERT INTO r_images_classifications(img_id,classification_id) VALUES (?,?)";
        jdbcTemplate1.batchUpdate(relSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, imgList.get(i).intValue());
                ps.setString(2, classId);
            }

            @Override
            public int getBatchSize() {
                return imgList.size();
            }
        });
    }
}