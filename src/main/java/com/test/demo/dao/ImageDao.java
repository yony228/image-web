package com.test.demo.dao;

import com.test.demo.entity.Classifications;
import com.test.demo.entity.Images;
import com.test.demo.entity.dto.ClassificationsDto;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
public interface ImageDao {
    /**
     * 查询标签id
     *
     * @param paramMap
     * @return
     */
    int queryClassId(Map<String, String> paramMap);

    /**
     * 添加图片
     *
     * @param fastDfsUrl
     * @param returnList
     * @param imgType
     * @param userId
     * @return
     */
    int insertPic(String fastDfsUrl, List<String> returnList, int imgType, int userId, String modelId) throws Exception;

    /**
     * 根据标签搜索图片
     *
     * @param classList
     * @return
     */
    List<String> queryPicByLabels(List<String> classList, int nowPage, int pageNum, String modelId);

    /**
     * @param classList
     * @return
     */
    int queryPicByLabelsCount(List<String> classList, String modelId);

    /**
     * 根据标签中文描述搜索图片
     *
     * @param description
     * @return
     */
    List<String> queryPicByLabelStr(String description, String modelId);

    /**
     * 根据标签统计图片数量
     *
     * @param classList
     * @return
     */
    List<ClassificationsDto> queryPicCountByLabels(List<String> classList, String modelId);

    /**
     * 根据标签查询标签描述
     *
     * @param classList
     * @return
     */
    String queryClassDescription(List<String> classList, String modelId);

    /**
     * 查询所有图片(分页)
     *
     * @return
     */
    List<Images> queryAllPicWithTag(int nowPage, Map<String, Object> map);

    /**
     * 查询所有图片总数
     *
     * @return
     */
    int queryAllPicWithTagCount(Map<String, Object> map);

    /**
     * 查询所有图片(分页)
     *
     * @return
     */
    List<Images> queryAllPicWithClass(int nowPage, Map<String, Object> map);

    /**
     * 查询所有图片总数
     *
     * @return
     */
    int queryAllPicWithClassCount(Map<String, Object> map);

    /**
     * 根据图片查询标签
     *
     * @param imagesId
     * @return
     */
    List<Classifications> queryTagByImagesId(int imagesId);

    /**
     * 根据图片查询标签
     *
     * @param imagesId
     * @return
     */
    List<Classifications> queryClassByImagesId(int imagesId, String modelId);

    /**
     * 修改图片标签
     *
     * @param imagesId         图片id
     * @param classDescription 图片标签
     * @return
     */
    int editImagesClass(int imagesId, String[] classDescription, int userId) throws Exception;

    /**
     * 修改图片标签
     *
     * @param imagesId         图片id
     * @param classDescription 图片标签
     * @return
     */
    int editImagesTags(int imagesId, String[] classDescription, int userId) throws Exception;

    /**
     * 添加图片标签
     *
     * @param imagesId         图片id
     * @param classDescription 图片标签
     * @return
     */
    int addImagesTags(int imagesId, Object[] classDescription, int userId) throws Exception;

    /**
     * 删除图片标签
     *
     * @param imagesId 图片id
     * @param tagId    自定义标签id
     * @return
     */
    int delImagesTagsRel(int imagesId, int tagId) throws Exception;

    /**
     * 删除图片标签
     *
     * @param imagesId 图片id
     * @param classId  标签id
     * @return
     */
    int delImagesClassRel(int imagesId, int classId) throws Exception;

    /**
     * 根据文字查询自定义标签id
     *
     * @param classDescription
     * @return
     */
    int queryTagIdByDes(String classDescription, int userId);

    /**
     * 删除图片
     *
     * @param imgId
     * @return
     */
    int delImages(String imgId);

    /**
     * 自定义标签添加图片
     *
     * @param picUrl
     * @param tagId
     * @param fileType
     * @param userId
     */
    int insertImageTag(String picUrl, String tagId, int fileType, int userId);

    /**
     * 在分类管理里面批量添加图片
     *
     * @param picUrl
     * @param classificationId
     * @param fileType
     * @param userId
     * @return
     */
    int insertImageInClassification(String picUrl, String classificationId, int fileType, int userId, String batchNo, String bak);
}
