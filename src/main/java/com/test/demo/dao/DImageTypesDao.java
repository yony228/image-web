package com.test.demo.dao;

import com.test.demo.entity.Classifications;
import com.test.demo.entity.Images;
import com.test.demo.entity.dto.ClassificationsDto;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
public interface DImageTypesDao {
    /**
     * 查询图片类型
     *
     * @param paramMap
     * @return
     */
    List<Map<String, Object>> queryImageTypes(Map<String, String> paramMap);
}
