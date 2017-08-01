package com.test.demo.dao.common.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface IUserDao {
    Map findUserByName(String name);

    Map findUserByPassword(Map param);

    List<Map<String, Object>> findUserRoles(Map param);

    /**
     * 添加普通用户
     *
     * @param param
     * @return
     */
    int insertGeneralUser(Map param);

    /**
     * 查询用户信息
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryUserListPage(Map<String, Object> param, Map pageInfo);

    /**
     * 修改用户
     *
     * @param param
     * @return
     */
    int updateById(Map param);
}
