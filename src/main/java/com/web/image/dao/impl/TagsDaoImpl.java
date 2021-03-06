package com.web.image.dao.impl;

import com.web.image.dao.ITagsDao;
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
public class TagsDaoImpl implements ITagsDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagsDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    //分页查询tag
    @Override
    public List<Map<String, Object>> queryTags(Map<String, String> param, Map pageInfo) {
        StringBuffer sqlBuffer = new StringBuffer(" SELECT t.*,u.name userName FROM tags t,users u WHERE t.user_id=u.id ");
        if (StringUtils.isNotBlank(param.get("id"))) {
            sqlBuffer.append(" AND t.id = :id ");
        }
        if (StringUtils.isNotBlank(param.get("tag"))) {
            sqlBuffer.append(" AND t.tag = :tag ");
        }
        if (StringUtils.isNotBlank(param.get("alias"))) {
            sqlBuffer.append(" AND t.alias = :alias ");
        }
        if (StringUtils.isNotBlank(param.get("des"))) {
            sqlBuffer.append(" AND t.des = :des ");
        }
        if (StringUtils.isNotBlank(param.get("is_public"))) {
            sqlBuffer.append(" AND t.is_public = :is_public");
        }

        if (StringUtils.isNotBlank(param.get("likeDes"))) {
            sqlBuffer.append(" AND t.des like :des ");
            param.put("des", "%" + MapUtils.getString(param, "des") + "%");
        }
        if (StringUtils.isNotBlank(param.get("likeAlias"))) {
            sqlBuffer.append(" AND t.alias like :likeAlias ");
            param.put("likeAlias", "%" + MapUtils.getString(param, "likeAlias") + "%");
        }


        if (StringUtils.isNotBlank(param.get("user_id"))) {
            sqlBuffer.append(" AND t.user_id = :user_id ");
        }
        if (StringUtils.isNotBlank(param.get("not_id"))) {
            sqlBuffer.append(" AND t.id <> :not_id");
        }
        sqlBuffer.append(" ORDER BY t.id desc");
        if (null != pageInfo) {
            sqlBuffer.append(" LIMIT " + MapUtils.getString(pageInfo, "offset", "0") + "," + MapUtils.getString(pageInfo, "limit", "10"));
        }
        LOGGER.info("queryTags sql = " + sqlBuffer.toString());
        return jdbcTemplate.queryForList(sqlBuffer.toString(), param);
    }

    /**
     * 模糊查询
     *
     * @param
     * @param pageInfo
     * @return
     */
    public List<Map<String, Object>> fuzzyQueryTags(String condition, String user_id, Map pageInfo) {
        StringBuffer sqlBuffer = new StringBuffer(" SELECT * FROM tags WHERE user_id = :user_id ");
        if (StringUtils.isNotBlank(condition)) {
            sqlBuffer.append(" AND (tag LIKE :condition OR alias LIKE :condition OR des LIKE :condition)");
        }
        if (null != pageInfo) {
            sqlBuffer.append(" order by id desc LIMIT " + MapUtils.getString(pageInfo, "offset", "0") + "," + MapUtils.getString(pageInfo, "limit", "10"));
        }
        LOGGER.info("fuzzyQueryTags sql = " + sqlBuffer.toString());
        Map param = new HashMap();
        param.put("condition", "%" + condition + "%");
        param.put("user_id", user_id);
        return jdbcTemplate.queryForList(sqlBuffer.toString(), param);
    }

    //todo: 查询tag下图片数量
    @Override
    public long queryImageCountByTag(long id) {
        long count = 0l;
        Map tmp = new HashMap();
        StringBuffer sqlBuffer = new StringBuffer(" SELECT count(*) FROM r_images_tags WHERE tag_id = :id ");
        tmp.put("id", id);
        count = jdbcTemplate.queryForObject(sqlBuffer.toString(), tmp, Integer.class);
        return count;
    }

    //更新tag
    @Override
    public int updateById(Map<String, String> param) {
        int reValue = 0;
        if (!param.containsKey("id") || StringUtils.isBlank(param.get("id"))) {
            return reValue;
        }
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE tags SET ");
        if (StringUtils.isNotBlank(param.get("tag"))) {
            sqlBuffer.append(" tag = :tag, ");
        }
        if (StringUtils.isNotBlank(param.get("alias"))) {
            sqlBuffer.append(" alias = :alias, ");
        }
        //if (StringUtils.isNotBlank((String) param.get("des"))) {
        if (param.containsKey("des")) {
            sqlBuffer.append(" des = :des, ");
        }
        if (StringUtils.isNotBlank(param.get("is_public"))) {
            sqlBuffer.append(" is_public = :is_public, ");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 2);
        sqlBuffer.append(" WHERE id = :id");
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }

    //删除分类
    @Override
    public int delete(long id) {
        StringBuffer sql = new StringBuffer("DELETE FROM tags WHERE id = :id");
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
                case "tag":
                    placeholder_names.append("tag, ");
                    placeholder_values.append(":tag, ");
                    break;
                case "alias":
                    placeholder_names.append("alias, ");
                    placeholder_values.append(":alias, ");
                    break;
                case "des":
                    placeholder_names.append("des, ");
                    placeholder_values.append(":des, ");
                    break;
                case "user_id":
                    placeholder_names.append("user_id, ");
                    placeholder_values.append(":user_id, ");
                    break;
                case "is_public":
                    placeholder_names.append("is_public, ");
                    placeholder_values.append(":is_public, ");
                    break;
            }
        }
        StringBuffer sql = new StringBuffer("INSERT INTO tags(");
        sql.append(placeholder_names.subSequence(0, placeholder_names.length() - 2)).append(") VALUES (").append(placeholder_values.substring(0, placeholder_values.length() - 2)).append(")");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(param);
        jdbcTemplate.update(sql.toString(), parameterSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

}
