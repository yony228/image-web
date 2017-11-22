package com.web.image.service;

import com.web.image.entity.Classifications;
import com.web.image.entity.dto.ClassificationsDto;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface IFileService {
    /**
     * 读取文件标签
     *
     * @param serverStr
     * @param fis
     * @return
     * @throws IOException
     */
    List<String> clintDoPredict(String serverStr, String serverPort, InputStream fis) throws IOException;

    /**
     * 根据分类拼音搜索图片
     *
     * @param classList
     * @return
     */
    List<String> queryPicByClass(List<String> classList, int nowPage, int pageNum);

    /**
     * 根据标签中文描述搜索图片(模糊查询)
     *
     * @param description
     * @return
     */
    List<String> queryPicByAlias(String description);

    /**
     * 统计每个分类的图片总数
     *
     * @param classList
     * @return
     */
    List<ClassificationsDto> queryPicCountByClass(List<String> classList);

    /**
     * 上传图片
     *
     * @param fastDfsUrl 图片地址
     * @param returnList 所属分类
     * @param fileType   图片类型
     * @param userId     上传人id
     * @return
     */
    int insertPic(String fastDfsUrl, List<String> returnList, String fileType, int userId);

    /**
     * 根据标签查询标签描述
     *
     * @param classList
     * @return
     */
    String queryClassDescription(List<String> classList);

    /**
     * 查询所有图片(分页)
     *
     * @param nowPage
     * @param paramMap
     * @param userId
     * @param trainer  是否训练用户
     * @return
     */
    Map<String, Object> queryAllPic(int nowPage, Map<String, Object> paramMap, int userId, String trainer);

    /**
     * 修改图片标签
     *
     * @param imagesId         图片id
     * @param classDescription 图片标签
     * @param userId
     * @param trainer
     * @return
     * @throws Exception
     */
    int editImagesClass(int imagesId, String[] classDescription, int userId, String trainer) throws Exception;

    /**
     * 添加图片标签
     *
     * @param imagesId         图片id
     * @param classDescription 图片标签
     * @return
     */
    int addImagesTags(int imagesId, String[] classDescription, int userId) throws Exception;

    /**
     * 删除图片标签
     *
     * @param imagesId         图片id
     * @param classDescription 图片标签
     * @return
     */
    int delImagesClass(int imagesId, String classDescription, int userId) throws Exception;

    /**
     * 删除图片
     *
     * @param imgId
     * @param trainer
     * @return
     */
    int delImages(String imgId, String trainer);

    /**
     * 自定义标签添加图片
     *
     * @param picUrl
     * @param tagId
     * @param fileType
     * @param userId
     */
    int insertImageTag(String picUrl, String tagId, String fileType, int userId);

    /**
     * 从分类添加图片
     *
     * @param imgUrl
     * @param classificationId
     * @param imgType
     * @param userId
     * @param batchNo
     * @param bak
     * @return
     */
    int insertImageInClassification(String imgUrl, String classificationId, String imgType, int userId, String batchNo, String bak);

    List<Map<String, String>> saveMultipartFile(HttpServletRequest request) throws Exception;
}
