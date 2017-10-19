package com.web.image.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface IClassificationsDao {
    /**
     * 分页查询分类
     *
     * @param param
     * @param pageInfo
     * @return
     */
    List<Map<String, Object>> queryClassifications(Map<String, String> param, Map pageInfo);

    /**
     * 查询分类下图片数量
     *
     * @param id
     * @return
     */
    long queryImageCountByClassification(long id);

    /**
     * 根据id修改分类
     *
     * @param param
     * @return
     */
    int updateById(Map param);

    /**
     * 根据id修改分类所有字段
     *
     * @param param
     * @return
     */
    int updateAllById(Map param);

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    int delete(long id);

    /**
     * 添加分类
     *
     * @param param
     * @return
     */
    int save(Map param);

    /**
     * 复制
     *
     * @param oldClassId
     * @param newClassId
     * @return
     */
    int batchImageClass(String oldClassId, String newClassId);

    /**
     * 根据标签查询标签描述
     *
     * @param classList
     * @return
     */
    String queryClassDescription(List<String> classList, String modelId);

    /**
     * 添加模型分类关系
     *
     * @param classIdList
     * @param modelId
     */
    boolean saveRModelClassBatch(List<String> classIdList, int modelId);

    /**
     * 复制图片关系
     *
     * @param tagId
     * @param classId
     */
    void copyImagesRel(String tagId, String classId);
}
