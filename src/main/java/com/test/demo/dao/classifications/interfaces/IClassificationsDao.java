package com.test.demo.dao.classifications.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface IClassificationsDao {

    List<Map<String, Object>> queryClassifications(Map param, Map pageInfo);

    /**
     * 模糊搜索分类（匹配字段：classification，alias,des)
     *
     * @param map
     * @param pageInfo
     * @return
     */
    List<Map<String, Object>> fuzzyQueryClassifications(Map map, Map pageInfo);

    int queryClassificationCount(Map param);

    long queryImageCountByClassification(long id);

    int updateById(Map param);

    int delete(long id);

    int save(Map param);

    /**
     * 复制
     * @param oldClassId
     * @param newClassId
     * @return
     */
    int batchImageClass(String oldClassId, String newClassId);
}
