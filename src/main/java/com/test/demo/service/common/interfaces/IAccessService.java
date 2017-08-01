package com.test.demo.service.common.interfaces;

import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface IAccessService {
    Map findUserByName(String name);

    Map getUserInfo(Map param);

    int insertGeneralUser(Map param);
}
