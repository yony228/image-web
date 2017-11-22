package com.web.image.service.impl;

import com.web.image.dao.IUserDao;
import com.web.image.service.IAccessService;
import edu.nudt.das.sansiro.core.service.AbsBaseService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
@Service
public class AccessServiceImpl extends AbsBaseService implements IAccessService {

    @Autowired
    private IUserDao userDao;

    @Override
    public Map findUserByName(String name) {
        return userDao.findUserByName(name);
    }

    @Override
    public Map getUserInfo(Map param) {
        Map<String, Object> userInfo = new HashMap<>();
        Map user = userDao.findUserByPassword(param);
        if (MapUtils.isEmpty(user)) {
            getLogger().info("用户不存在.");
            return null;
        }

        List<Map<String, Object>> userRole;
        user.put("trainer", false);
        if (MapUtils.isNotEmpty(user)) {
            userRole = userDao.findUserRoles(user);
            for (Map<String, Object> map : userRole) {
                //暂时用角色名称来判断是不是训练用户
                if (map.get("name").equals("训练用户")) {
                    user.put("trainer", true);
                    break;
                }
            }
            userInfo.put("user", user);
            userInfo.put("userRole", userRole);
        }
        getLogger().info("用户信息:" + userInfo);
        return userInfo;
    }

    @Override
    public int insertGeneralUser(Map param) {
        return userDao.insertGeneralUser(param);
    }
}