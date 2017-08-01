package com.test.demo.service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface UserService {
    /**
     * 查询
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryUserListPage(Map<String, Object> map);

    /**
     * @param userId
     * @return
     */
    boolean resetPwd(String userId);

    /**
     * 修改用户状态
     *
     * @param userId
     * @param status
     * @return
     */
    boolean updateStatus(String userId, String status);

    /**
     *
     * @param userName
     * @param oldPwd
     * @param newPwd
     * @return
     */
    boolean updatePwd(String userName, String oldPwd, String newPwd);
}
