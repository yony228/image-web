package com.test.demo.service.classificationandtag.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface ITagService {

    /**
     * 分页条件查询标签
     *
     * @param params tags表相关字段
     * @return
     */
    List<Map<String, Object>> queryTags(Map params);

    /**
     * 模糊查询标签
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> fuzzyQueryTags(Map params);

    /**
     * 查询tag下图片数量
     *
     * @param tagId
     * @return
     */
    long queryImageCountByTag(long tagId);

    /**
     * 修改tag
     *
     * @param param
     * @return
     */
    boolean updateTag(Map param);

    /**
     * 查找用户下同名Tag
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> findSameTagPerUser(Map param);

    /**
     * 检测用户下是否有同名Tag
     *
     * @param param
     * @return
     */
    boolean checkSameTagPerUser(Map param);

    boolean deleteTag(long id);

    boolean addTag(Map param);

    /**
     * 添加标签
     *
     * @param des
     * @param userId
     * @return
     */
    boolean insertTags(String alias, String des, String userId);
}
