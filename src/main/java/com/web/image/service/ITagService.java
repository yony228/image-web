package com.web.image.service;

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
    boolean updateTag(Map<String, String> param);

    /**
     * 查找用户下同名Tag
     *
     * @param id
     * @param userId
     * @param alias
     * @param tag
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> findSameTag(String id, String userId, String alias, String tag) throws Exception;

    /**
     * 删除标签
     *
     * @param id
     * @return
     */
    boolean deleteTag(long id);

    /**
     * 添加标签
     *
     * @param des
     * @param userId
     * @return
     */
    boolean insertTags(String alias, String des, String userId, String isPublic);
}
