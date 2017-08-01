package com.test.demo.service.impl;

import com.test.demo.common.FileWebConf;
import com.test.demo.common.Md5Util;
import com.test.demo.common.PageUtil;
import com.test.demo.common.TrainClassStatus;
import com.test.demo.dao.TrainClassDao;
import com.test.demo.dao.TrainsDao;
import com.test.demo.dao.classifications.interfaces.IClassificationsDao;
import com.test.demo.dao.common.interfaces.IUserDao;
import com.test.demo.entity.TrainClass;
import com.test.demo.service.ModelsService;
import com.test.demo.service.TrainClassService;
import com.test.demo.service.UserService;
import edu.nudt.das.image.grpc.client.train.TrainClient;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/5/16.
 */
@Service
public class UserServiceImpl implements UserService {
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
