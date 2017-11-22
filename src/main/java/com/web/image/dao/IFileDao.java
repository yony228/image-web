package com.web.image.dao;

import com.web.image.entity.Classifications;
import com.web.image.entity.Images;
import com.web.image.entity.dto.ClassificationsDto;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
public interface IFileDao {
    /**
     * 添加图片
     *
     * @param fastDfsUrl 图片地址
     * @param returnList 图片分类
     * @param imgType    图片类型
     * @param userId
     * @param modelId
     * @return
     * @throws Exception
     */
    int insertPic(String fastDfsUrl, List<String> returnList, int imgType, int userId, String modelId) throws Exception;

    /**
     * 根据分类拼音搜索图片
     *
     * @param classList
     * @return
     */
    List<String> queryPicByClass(List<String> classList, int nowPage, int pageNum, String modelId);

    /**
     * 根据标签中文描述搜索图片(模糊查询)
     *
     * @param description
     * @return
     */
    List<String> queryPicByAlias(String description, String modelId);

    /**
     * 根据标签统计图片数量
     *
     * @param classList
     * @return
     */
    List<ClassificationsDto> queryPicCountByClass(List<String> classList, String modelId);

    /**
     * 查询所有图片(分页)
     *
     * @return
     */
    List<Images> queryAllPicWithTag(int nowPage, Map<String, Object> map);

    /**
     * 查询所有图片(分页)
     *
     * @return
     */
    List<Images> queryAllPicWithClass(int nowPage, Map<String, Object> map);

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
    int delImagesTagsRel(String imagesId, int tagId);

    /**
     * 删除图片标签
     *
     * @param imagesId 图片id
     * @param classId  标签id
     * @return
     */
    int delImagesClassRel(String imagesId, int classId);

    /**
     * 查询没有关联关系，可以删除的图片
     * @param imagesId
     * @return
     */
    List<Map<String, Object>> queryCanDelImg(String imagesId);

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
