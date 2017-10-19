package com.web.image.service;

import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface IClassificationService {

    /**
     * 分页条件查询分类
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryClassifications(Map param);

    /**
     * 查询分类下图片数量
     *
     * @param id
     * @return
     */
    long queryImageCountByClassification(long id);

    /**
     * 添加分类
     *
     * @param param
     * @return
     */
    boolean addClassification(Map param);


    /**
     * 更新Classification
     *
     * @param param
     * @return
     */
    boolean updateClassification(Map<String, String> param);

    /**
     * @param id
     * @return
     */
    boolean deleteClassification(long id);

    /**
     * 查询分类是否重复
     *
     * @param id             分类id
     * @param userId         用户Id
     * @param alias          分类名
     * @param classification 分类别名
     * @return
     */
    List<Map<String, Object>> findSameClassification(String id, String userId, String alias, String classification) throws Exception;

    /**
     * 复制用户标签到分类
     *
     * @param userId
     * @param tagId
     * @return
     */
    boolean copyTagsToClass(String userId, String tagId);
}