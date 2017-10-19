package com.web.image.service.impl;

import com.google.protobuf.ByteString;
import com.web.image.common.FileWebConf;
import com.web.image.common.ZipUtil;
import com.web.image.dao.*;
import com.web.image.entity.dto.ClassificationsDto;
import com.web.image.service.IFileService;
import edu.nudt.das.image.grpc.client.prediction.ImagePredictClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/5/16.
 */
@Service
public class FileServiceImpl implements IFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileWebConf fileWebConf;

    @Autowired
    private IFileDao fileDao;

    @Autowired
    private IImageTypesDao imageTypesDao;

    @Autowired
    private IDImageTypesDao dImageTypesDao;

    @Autowired
    private IModelsDao modelsDao;

    @Autowired
    private IClassificationsDao classDao;

    @Autowired
    private ITagsDao tagsDao;

    @Autowired
    private IImagesDao imagesDao;

    /**
     * @param serverStr
     * @param serverPort
     * @param fis
     * @return
     * @throws IOException
     */
    @Override
    public List<String> clintDoPredict(String serverStr, String serverPort, InputStream fis) throws IOException {
        LOGGER.info("Start the predict client");
        String host = serverStr;
        short port = Short.parseShort(serverPort);
        String modelName = "inception";
        String signatureName = "predict_images";
        String inputName = "images";
        ImagePredictClient client = new ImagePredictClient(host, port);

        Map<String, Object> modelsMap = modelsDao.getOnlineModels();//当前上线模型

        List<String> returnList = new ArrayList<>();
        try {
            Map e = client.predict_image(modelName, signatureName, inputName, fis);
            Iterator var9 = e.entrySet().iterator();

            List<ByteString> classNameList = null;
            List<Float> floatList = null;
            while (var9.hasNext()) {
                Map.Entry entry = (Map.Entry) var9.next();
                LOGGER.info("client返回:" + entry.getValue());
                TensorProto t = (TensorProto) entry.getValue();
                DataType type = t.getDtype();
                if (type == DataType.DT_STRING) {
                    classNameList = t.getStringValList();
                } else if (type == DataType.DT_FLOAT) {
                    floatList = t.getFloatValList();
                }
            }

//            float showLabelRate = fileWebConf.getShowLabelRateTop();
            float showLabelRate = modelsMap.get("show_rate_top") == null ? fileWebConf.getShowLabelRateTop() : Float.parseFloat(modelsMap.get("show_rate_top").toString());
            if (classNameList != null && classNameList.size() > 0) {
                if (floatList.get(0) > showLabelRate) {
                    LOGGER.info("高于top值，只显示一个标签.");
                    System.out.println(classNameList.get(0).toStringUtf8() + ":" + floatList.get(0));
                    returnList.add(classNameList.get(0).toStringUtf8());
                } else {
//                    showLabelRate = fileWebConf.getShowLabelRateBottom();
                    showLabelRate = modelsMap.get("show_rate_bottom") == null ? fileWebConf.getShowLabelRateBottom() : Float.parseFloat(modelsMap.get("show_rate_bottom").toString());
                    LOGGER.info("低于top值，显示所有高于bottom的标签。");
                    for (int i = 0; i < classNameList.size(); i++) {
                        if (floatList.get(i) > showLabelRate) {
                            System.out.println(classNameList.get(i).toStringUtf8() + ":" + floatList.get(i));
                            returnList.add(classNameList.get(i).toStringUtf8());
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }

                client.shutdown();
            } catch (Exception var20) {
                System.out.println(var20);
            }

        }
        LOGGER.info("End of predict client");
        return returnList;
    }

    @Override
    public List<String> queryPicByClass(List<String> classList, int nowPage, int pageNum) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return fileDao.queryPicByClass(classList, nowPage, pageNum, modelsMap.get("id").toString());
    }

    /**
     * 根据标签中文描述搜索图片
     *
     * @param description
     * @return
     */
    @Override
    public List<String> queryPicByAlias(String description) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return fileDao.queryPicByAlias(description, modelsMap.get("id").toString());
    }

    /**
     * 统计每个分类的图片总数
     *
     * @param classList
     * @return
     */
    @Override
    public List<ClassificationsDto> queryPicCountByClass(List<String> classList) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return fileDao.queryPicCountByClass(classList, modelsMap.get("id").toString());
    }

    /**
     * 上传图片
     *
     * @param fastDfsUrl 图片地址
     * @param returnList 所属分类
     * @param fileType   图片类型
     * @param userId     上传人id
     * @return
     */
    @Override
    public int insertPic(String fastDfsUrl, List<String> returnList, String fileType, int userId) {
        try {
            int imgType = imageTypesDao.queryTypeIdByValues(fileType);
            Map<String, Object> modelsMap = modelsDao.getOnlineModels();
            return fileDao.insertPic(fastDfsUrl, returnList, imgType, userId, modelsMap.get("id").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String queryClassDescription(List<String> classList) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return classDao.queryClassDescription(classList, modelsMap.get("id").toString());
    }

    @Override
    public Map<String, Object> queryAllPic(int nowPage, Map<String, Object> paramMap, int userId, String trainer) {
        Map<String, Object> returnMap = new IdentityHashMap();

        paramMap.put("userId", String.valueOf(userId));
        if (trainer.equals("false")) {
            returnMap.put("imgesList", fileDao.queryAllPicWithTag(nowPage, paramMap));
            returnMap.put("countNum", fileDao.queryAllPicWithTag(0, paramMap).size());
        } else {
            returnMap.put("imgesList", fileDao.queryAllPicWithClass(nowPage, paramMap));
            returnMap.put("countNum", fileDao.queryAllPicWithClass(0, paramMap).size());
        }
        return returnMap;
    }

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
    @Override
    public int editImagesClass(int imagesId, String[] classDescription, int userId, String trainer) throws Exception {
        if (trainer.equals("false")) {
            return fileDao.editImagesTags(imagesId, classDescription, userId);
        } else {
            return fileDao.editImagesClass(imagesId, classDescription, userId);
        }
    }

    @Override
    public int addImagesTags(int imagesId, String[] classDescription, int userId) throws Exception {
        return fileDao.addImagesTags(imagesId, classDescription, userId);
    }

    /**
     * 普通用户上传图片时，删除图片标签
     *
     * @param imagesId         图片id
     * @param classDescription 图片标签
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public int delImagesClass(int imagesId, String classDescription, int userId) throws Exception {
        Integer tagId = 0;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("alias", classDescription);
        paramMap.put("user_id", String.valueOf(userId));
        List<Map<String, Object>> tagList = tagsDao.queryTags(paramMap, null);
        if (tagList.size() > 0) {
            tagId = Integer.parseInt(tagList.get(0).get("id").toString());
        }
        fileDao.delImagesTagsRel(String.valueOf(imagesId), tagId);

        //TODO class关系表是否也要删
        Map<String, String> map = new HashMap<>();
        map.put("alias", classDescription);
        List<Map<String, Object>> classList = classDao.queryClassifications(map, null);
        int classId = Integer.parseInt(classList.get(0).get("id").toString());
        fileDao.delImagesClassRel(String.valueOf(imagesId), classId);

        return 0;
    }

    @Override
    @Transactional
    public int delImages(String imgId, String trainer) {
        if (StringUtils.isEmpty(imgId)) {
            LOGGER.info("图片id不能为空");
            return -1;
        }

        if (trainer.equals("true")) {
            //如果是训练用户，删除图片和分类的对应关系
            //TODO 多个训练用户时，如果两人是复制的同一个普通用户的标签，用引用同一个imgId,删除图片时，只能删除自己的分类和图片的关系
            fileDao.delImagesClassRel(imgId, -1);
        } else {
            fileDao.delImagesTagsRel(imgId, -1);
        }

        //查询没有其它关联关系的图片，直接删除。
        List<Map<String, Object>> delList = fileDao.queryCanDelImg(imgId);
        if (delList!=null && delList.size()>0) {
            String delImgId = delList.get(0).get("img_id").toString();
            fileDao.delImages(delImgId);

            //删除服务器中的图片
            String[] delImgUrl = delList.get(0).get("img_url").toString().split(",");
            for (String url : delImgUrl) {
                url = fileWebConf.getUploadUrl() + url;
                File file = new File(url);
                LOGGER.info("删除服务器中的图片 url:=========" + url);
                file.delete();
            }
        }

        //如果图片还有其它关联关系，就修改图片所属人。(因为普通用户图片管理，没有标签的图片要求也能查询出来) TODO 暂时定将图片上传人修改为999999+user_id
        if (trainer.equals("false")) {
            imagesDao.updateUploadUserByIds("999999", imgId);
        }

        return 0;
    }

    @Override
    public int insertImageTag(String picUrl, String tagId, String fileType, int userId) {
        try {
            int imgType = imageTypesDao.queryTypeIdByValues(fileType);
            return fileDao.insertImageTag(picUrl, tagId, imgType, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int insertImageInClassification(String imgUrl, String classificationId, String fileType, int userId, String batchNo, String bak) {
        try {
            int imgType = imageTypesDao.queryTypeIdByValues(fileType);
            return fileDao.insertImageInClassification(imgUrl, classificationId, imgType, userId, batchNo, bak);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Map<String, String>> saveMultipartFile(HttpServletRequest request) throws Exception {
        List<Map<String, String>> returnList = new ArrayList<>();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        String fileFolder = fileWebConf.getUploadUrl();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        fileFolder += sf.format(new Date()) + "/";
        File folder = new File(fileFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }

        //MultipartFile file = null;
        //BufferedOutputStream stream = null;
        for (MultipartFile file : files) {
            String fileName = "";
            if (!file.isEmpty()) {
                String fileType = file.getOriginalFilename().split("\\.")[1];

                //校验文件格式
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("value", fileType);
                List<Map<String, Object>> imageTypesList = dImageTypesDao.queryImageTypes(paramMap);
                if (imageTypesList == null || imageTypesList.size() == 0) {
                    LOGGER.info("saveMultipartFile error : " + file.getOriginalFilename() + "格式不正确！");
                    continue;
                }

                fileName = fileFolder + UUID.randomUUID() + "." + fileType;
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();

                LOGGER.info("文件目录:" + fileName);
                if (fileType.equals("zip")) {
                    //解压zip包
                    returnList = getZipFile(fileName, fileFolder);
                } else {
                    //去掉文件目录
                    fileName = fileName.replace(fileWebConf.getUploadUrl(), "");

                    Map<String, String> rtn = new HashMap<>();
                    rtn.put("fileName", fileName);
                    rtn.put("fileType", fileType);
                    returnList.add(rtn);
                }
            }
        }

        LOGGER.info("fileSize=" + returnList.size());
        return returnList;
    }

    public List<Map<String, String>> getZipFile(String fileUrl, String fileFolder) {
        ZipUtil.unZip(fileUrl);
        //
        String filePath = fileUrl.substring(0, fileUrl.lastIndexOf(".")) + File.separator;
        File file = new File(fileUrl);
        String zipName = file.getName();
        List<Map<String, String>> returnList = new ArrayList<>();
        this.readFiles(filePath, zipName, fileFolder, returnList);
        return returnList;
    }

    public void readFiles(String filePath, String zipName, String fileFolder, List<Map<String, String>> returnList) {
        String fileName = "";
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                readFiles(file2.getAbsolutePath(), zipName, fileFolder, returnList);
            } else {
                //TODO 判断文件类型

                Map<String, String> rtn = new HashMap<>();
                String path = file2.getAbsolutePath();
                path = path.replace(fileFolder, "");
                rtn.put("fileName", path);
                rtn.put("fileType", file2.getName().split("\\.")[1]);
                rtn.put("bak", zipName);
                returnList.add(rtn);
            }
        }
    }
}
