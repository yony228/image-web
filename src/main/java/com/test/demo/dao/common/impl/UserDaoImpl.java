package com.test.demo.dao.common.impl;

import com.test.demo.dao.common.interfaces.IUserDao;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * Created by yony on 17-6-5.
 */
@Repository
public class UserDaoImpl implements IUserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map findUserByName(String name) {
        LOGGER.info("findUserByName name=" + name);
        Map<String, Object> map = new HashMap<>();
        map.put("USER_NAME", name);
        String sql = "SELECT * FROM users  t WHERE t.name = :USER_NAME";
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, map);
            return list.size() > 0 ? list.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map findUserByPassword(Map param) {
        LOGGER.info("findUserByPassword params=" + param);
        Map<String, Object> map = new HashMap<>();
        map.put("USER_NAME", param.get("username"));
        map.put("PASSWORD", param.get("password"));
        String sql = "SELECT * FROM users  t WHERE t.name = :USER_NAME AND t.password = :PASSWORD";
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, map);
            return list.size() > 0 ? list.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }

//            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, new MapSqlParameterSource()
//                    .addValue("USER_NAME", param.get("username"))
//                    .addValue("PASSWORD", param.get("password")));
        return null;
    }

    @Override
    public List<Map<String, Object>> findUserRoles(Map param) {
        LOGGER.info("findUserRoles params=" + param);
        String sql = "SELECT * FROM r_users_roles t1, roles t2  WHERE t1.role_id = t2.id AND t1.user_id = :USER_ID";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource()
                .addValue("USER_ID", param.get("ID")));
    }

    @Override
    @Transactional
    public int insertGeneralUser(Map param) {
        Iterator i = param.keySet().iterator();
        StringBuffer placeholder_names = new StringBuffer();
        StringBuffer placeholder_values = new StringBuffer();
        while (i.hasNext()) {
            switch ((String) i.next()) {
                case "username":
                    placeholder_names.append("name, ");
                    placeholder_values.append(":username, ");
                    break;
                case "password":
                    placeholder_names.append("password, ");
                    placeholder_values.append(":password, ");
                    break;
            }
        }
        StringBuffer sql = new StringBuffer("INSERT INTO users(");
        sql.append(placeholder_names.subSequence(0, placeholder_names.length() - 2)).append(") VALUES (").append(placeholder_values.substring(0, placeholder_values.length() - 2)).append(")");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(param);
        jdbcTemplate.update(sql.toString(), parameterSource, keyHolder, new String[]{"id"});
        int userId = keyHolder.getKey().intValue();


        Map<String, Object> relMap = new HashMap<>();
        relMap.put("userId", userId);
        String sqlRel = "INSERT INTO r_users_roles(user_id,role_id) values(:userId,'1')";
        jdbcTemplate.update(sqlRel.toString(), relMap);

        return userId;
    }

    @Override
    public List<Map<String, Object>> queryUserListPage(Map<String, Object> param, Map pageInfo) {
        StringBuffer sqlBuffer = new StringBuffer("SELECT u.id,u.name,u.`desc`,u.status,r.name role_name from users u,r_users_roles rur,roles r\n" +
                "where u.id=rur.user_id and r.id=rur.role_id ");
        if (StringUtils.isNotBlank(MapUtils.getString(param, "condition"))) {
            sqlBuffer.append(" and (u.name LIKE :condition ");
            sqlBuffer.append("OR u.`desc` LIKE :condition ) ");

            param.put("condition", "%" + MapUtils.getString(param, "condition") + "%");
        }
        sqlBuffer.append(" ORDER BY u.id desc");
        if (null != pageInfo) {
            sqlBuffer.append(" LIMIT " + MapUtils.getString(pageInfo, "offset", "0") + "," + MapUtils.getString(pageInfo, "limit", "10"));
        }
        LOGGER.info("queryUserListPage sql = " + sqlBuffer.toString());
        return jdbcTemplate.queryForList(sqlBuffer.toString(), param);
    }

    //更新
    @Override
    public int updateById(Map param) {
        int reValue = 0;
        if (!param.containsKey("userId") || StringUtils.isBlank(MapUtils.getString(param, "userId"))) {
            return reValue;
        }
        StringBuffer sqlBuffer = new StringBuffer(" UPDATE users SET ");
        if (StringUtils.isNotBlank(MapUtils.getString(param, "name"))) {
            sqlBuffer.append(" name = :name, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "desc"))) {
            sqlBuffer.append(" desc = :desc, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "password"))) {
            sqlBuffer.append(" password = :password, ");
        }
        if (StringUtils.isNotBlank(MapUtils.getString(param, "status"))) {
            sqlBuffer.append(" status = :status, ");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 2);
        sqlBuffer.append(" WHERE id = :userId");
        return jdbcTemplate.update(sqlBuffer.toString(), param);
    }
}
