package com.test.demo.service.impl;

import com.google.protobuf.ByteString;
import com.test.demo.common.FileWebConf;
import com.test.demo.common.ZipUtil;
import com.test.demo.dao.DImageTypesDao;
import com.test.demo.dao.ImageTypesDao;
import com.test.demo.dao.ImageDao;
import com.test.demo.dao.ModelsDao;
import com.test.demo.entity.Classifications;
import com.test.demo.entity.Images;
import com.test.demo.entity.dto.ClassificationsDto;
import com.test.demo.service.FileService;
import edu.nudt.das.image.grpc.client.prediction.ImagePredictClient;
import freemarker.ext.util.IdentityHashMap;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class FileServiceImpl implements FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileWebConf fileWebConf;

    @Autowired
    private ImageDao imageDao;

    @Autowired
    private ImageTypesDao imageTypesDao;

    @Autowired
    private DImageTypesDao dImageTypesDao;

    @Autowired
    private ModelsDao modelsDao;

    public String fileUpload(InputStream fis, String fileName) {
        String fileFolder = fileWebConf.getUploadUrl();
        String newFileName = "";
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");

            newFileName = fileName.split("\\.")[0] + "_" + sf.format(new Date()) + "." + fileName.split("\\.")[1];

            FileOutputStream fos = new FileOutputStream(new File(fileFolder + "/" + newFileName));
            byte[] temp = new byte[1024];
            int len = 0;
            while ((len = fis.read(temp)) > 0) {
                fos.write(temp, 0, len);
            }

            fis.close();
            fos.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return fileFolder + newFileName;
    }

    /**
     * @param serverStr
     * @param serverPort
     * @param fis
     * @return
     * @throws IOException
     */
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
    public int insertPic(String fastDfsUrl, List<String> returnList, String fileType, int userId) {
        try {
            int imgType = imageTypesDao.queryTypeIdByValues(fileType);
            Map<String, Object> modelsMap = modelsDao.getOnlineModels();
            return imageDao.insertPic(fastDfsUrl, returnList, imgType, userId, modelsMap.get("id").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> queryPicByLabels(List<String> classList, int nowPage, int pageNum) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return imageDao.queryPicByLabels(classList, nowPage, pageNum, modelsMap.get("id").toString());
    }

    @Override
    public int queryPicByLabelsCount(List<String> classList) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return imageDao.queryPicByLabelsCount(classList, modelsMap.get("id").toString());
    }

    @Override
    public List<String> queryPicByLabelStr(String description) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return imageDao.queryPicByLabelStr(description, modelsMap.get("id").toString());
    }

    @Override
    public List<ClassificationsDto> queryPicCountByLabels(List<String> classList) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return imageDao.queryPicCountByLabels(classList, modelsMap.get("id").toString());
    }

    @Override
    public String queryClassDescription(List<String> classList) {
        Map<String, Object> modelsMap = modelsDao.getOnlineModels();
        return imageDao.queryClassDescription(classList, modelsMap.get("id").toString());
    }

    @Override
    public Map<String, Object> queryAllPic(int nowPage, Map<String, Object> paramMap, int userId, boolean tags) {
        Map<String, Object> returnMap = new IdentityHashMap();

        paramMap.put("userId", String.valueOf(userId));
        if (tags) {
            returnMap.put("imgesList", imageDao.queryAllPicWithTag(nowPage, paramMap));
            returnMap.put("countNum", imageDao.queryAllPicWithTagCount(paramMap));
        } else {
            returnMap.put("imgesList", imageDao.queryAllPicWithClass(nowPage, paramMap));
            returnMap.put("countNum", imageDao.queryAllPicWithClassCount(paramMap));
        }
        return returnMap;
    }

    @Override
    public List<Classifications> queryTagByImagesId(int imagesId) {
        return imageDao.queryTagByImagesId(imagesId);
    }

    @Override
    public List<Classifications> queryClassByImagesId(int imagesId, String modelId) {
        return imageDao.queryClassByImagesId(imagesId, modelId);
    }

    @Override
    public int editImagesClass(int imagesId, String[] classDescription, int userId, boolean tags) throws Exception {
        if (tags) {
            return imageDao.editImagesTags(imagesId, classDescription, userId);
        } else {
            return imageDao.editImagesClass(imagesId, classDescription, userId);
        }
    }

    @Override
    public int addImagesTags(int imagesId, String[] classDescription, int userId) throws Exception {
        return imageDao.addImagesTags(imagesId, classDescription, userId);
    }

    @Override
    public int delImagesClass(int imagesId, String classDescription, int userId) throws Exception {
        int tagId = imageDao.queryTagIdByDes(classDescription, userId);
        imageDao.delImagesTagsRel(imagesId, tagId);

        //class关系表也要删
        Map<String, String> map = new HashMap<>();
        map.put("alias", classDescription);
        int classId = imageDao.queryClassId(map);
        imageDao.delImagesClassRel(imagesId, classId);

        return 0;
    }

    @Override
    public int delImages(String imgId) {
        return imageDao.delImages(imgId);
    }

    @Override
    public int insertImageTag(String picUrl, String tagId, String fileType, int userId) {
        try {
            int imgType = imageTypesDao.queryTypeIdByValues(fileType);
            return imageDao.insertImageTag(picUrl, tagId, imgType, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int insertImageInClassification(String imgUrl, String classificationId, String fileType, int userId, String batchNo, String bak) {
        try {
            int imgType = imageTypesDao.queryTypeIdByValues(fileType);
            return imageDao.insertImageInClassification(imgUrl, classificationId, imgType, userId, batchNo, bak);
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
