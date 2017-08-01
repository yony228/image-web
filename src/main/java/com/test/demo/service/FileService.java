package com.test.demo.service;

import com.test.demo.entity.Classifications;
import com.test.demo.entity.Images;
import com.test.demo.entity.dto.ClassificationsDto;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface FileService {
    /**
     * 文件上传
     *
     * @param fis
     * @param fileName
     * @return
     */
     String fileUpload(InputStream fis, String fileName);


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
     * 添加图片
     *
     * @param fastDfsUrl 图片地址
     * @param returnList 所属分类
     * @param fileType   图片类型
     * @param userId     上传人id
     * @return
     */
    int insertPic(String fastDfsUrl, List<String> returnList, String fileType, int userId);

    /**
     * 根据标签搜索图片
     *
     * @param classList
     * @return
     */
    List<String> queryPicByLabels(List<String> classList, int nowPage, int pageNum);


    /**
     * @param classList
     * @return
     */
    int queryPicByLabelsCount(List<String> classList);

    /**
     * 根据标签中文描述搜索图片
     *
     * @param description
     * @return
     */
    List<String> queryPicByLabelStr(String description);

    /**
     * 根据标签统计图片数量
     *
     * @param classList
     * @return
     */
    List<ClassificationsDto> queryPicCountByLabels(List<String> classList);

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
     * @return
     */
    Map<String, Object> queryAllPic(int nowPage, Map<String, Object> paramMap, int userId, boolean tags);

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
    int editImagesClass(int imagesId, String[] classDescription, int userId, boolean tags) throws Exception;

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
