package com.web.image.service.impl;

import com.web.image.common.Md5Util;
import com.web.image.common.PageUtil;
import com.web.image.dao.IUserDao;
import com.web.image.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2017/5/16.
 */
@Service
public class UserServiceImpl implements IUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private IUserDao userDao;

    @Override
    public List<Map<String, Object>> queryUserListPage(Map<String, Object> param) {
        Map pageInfo = PageUtil.getPage(param);
        return userDao.queryUserListPage(param, pageInfo);
    }

    @Override
    public boolean resetPwd(String userId) {
        String newPwd = "123456";
        try {
            newPwd = Md5Util.EncoderByMd5(newPwd);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("password", newPwd);

            if (userDao.updateById(map) > 0) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return false;
    }

    @Override
    public boolean updateStatus(String userId, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("status", status);

        if (userDao.updateById(map) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePwd(String userName, String oldPwd, String newPwd) {
        try {
            Map param = new HashMap();
            param.put("username", userName);
            param.put("password", Md5Util.EncoderByMd5(oldPwd));
            Map user = userDao.findUserByPassword(param);
            if (user == null) {
                return false;
            }
            int userId = Integer.parseInt(user.get("id").toString());

            newPwd = Md5Util.EncoderByMd5(newPwd);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("password", newPwd);
            userDao.updateById(map);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }
}
