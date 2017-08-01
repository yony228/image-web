package com.test.demo.controller.file;

import com.alibaba.fastjson.JSONObject;
import com.test.demo.common.FileWebConf;
import com.test.demo.controller.BaseController;
import com.test.demo.dao.DImageTypesDao;
import com.test.demo.entity.Images;
import com.test.demo.service.FileService;
import edu.nudt.das.sansiro.login.interceptor.Auth;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/5/15.
 */
@Controller
@Auth
@RequestMapping("/file")
public class FileController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @Autowired
    FileWebConf fileWebConf;

    @Autowired
    private DImageTypesDao dImageTypesDao;

    @RequestMapping("upload/init")
    public String uploadInit(HttpServletRequest request) throws Exception {
        return "/file/fileUpload";
    }

    /**
     * 多个文件上传
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "upload/uploadFile", method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request, ModelMap modelMap) throws Exception {
        List<Map<String, Object>> returnFileList = new ArrayList<>();

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        String fileFolder = fileWebConf.getUploadUrl();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        fileFolder += sf.format(new Date()) + "/";
        File folder = new File(fileFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }

        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); i++) {
            Map<String, Object> returnFileMap = new HashMap<>();

            String fileName = "";
            file = files.get(i);
            if (!file.isEmpty()) {
                String fileType = file.getOriginalFilename().split("\\.")[1];
                //校验文件格式
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("value", fileType);
                List<Map<String, Object>> imageTypesList = dImageTypesDao.queryImageTypes(paramMap);
                if (imageTypesList == null || imageTypesList.size() == 0) {
                    LOGGER.info("uploadFile error : " + file.getOriginalFilename() + "格式不正确！");
                    continue;
                }

                fileName = fileFolder + UUID.randomUUID() + "." + fileType;
                byte[] bytes = file.getBytes();
                stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();

                LOGGER.info("client文件目录:" + fileName);

                //获取标签
                List<String> returnList = fileService.clintDoPredict(fileWebConf.getClintServer(), fileWebConf.getClintServerPort(), file.getInputStream());
                String returnStr = fileService.queryClassDescription(returnList);

                //添加数据库
                fileName = fileName.replace(fileWebConf.getUploadUrl(), "");
                int userId = Integer.parseInt(MapUtils.getString(getUser(request.getSession()), "id"));
                int imagesId = fileService.insertPic(fileName, returnList, fileType, userId);

                returnFileMap.put("imagesId", String.valueOf(imagesId));
                returnFileMap.put("keyName", returnStr.split(" "));
                returnFileMap.put("fileUrl", fileName);
                returnFileList.add(returnFileMap);
            }
        }
        LOGGER.info("上传完成。。。");
        modelMap.put("returnFileList", returnFileList);
        modelMap.put("picFullUrl", fileWebConf.getPicFullUrl());

        if (request.getParameter("picManager") != null && !request.getParameter("picManager").equals("")) {
            return "/file/file/fileList";
        }
        return "/file/fileUploadList";
    }

    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, ModelMap modelMap, @PathVariable String pageFile, @PathVariable String page) throws Exception {
        modelMap.put("picUrl", fileWebConf.getPicFullUrl());
        modelMap.put("className", request.getParameter("className"));
        return "/" + pageFile + "/" + page;
    }

    @RequestMapping("getImageList")
    @ResponseBody
    public Object picManagerInit(HttpServletRequest request) throws Exception {
        String nowPage = request.getParameter("pageNumber");
        if (nowPage == null || nowPage.equals("")) {
            nowPage = "1";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("imageId", request.getParameter("imageId"));
        paramMap.put("className", request.getParameter("className"));
        paramMap.put("modelId", request.getParameter("modelId"));
        paramMap.put("batchNo", request.getParameter("batchNo"));

        int userId = Integer.parseInt(MapUtils.getString(getUser(request.getSession()), "id"));

        //如果就训练用户，查询class表
        Map<String, Object> imagesMap = new HashMap<>();
        List<Images> imagesList = new ArrayList<>();

        String trainer = MapUtils.getString(getUser(request.getSession()), "trainer");//是否训练用户
        if (trainer.equals("true")) {
            imagesMap = fileService.queryAllPic(Integer.parseInt(nowPage), paramMap, userId, false);
            imagesList = (List) imagesMap.get("imgesList");
            for (Images images : imagesList) {
                images.setClassifications(fileService.queryClassByImagesId(images.getId(), null));
            }
        } else {
            imagesMap = fileService.queryAllPic(Integer.parseInt(nowPage), paramMap, userId, true);
            imagesList = (List) imagesMap.get("imgesList");
            for (Images images : imagesList) {
                images.setClassifications(fileService.queryTagByImagesId(images.getId()));
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", imagesList);
        jsonObject.put("total", imagesMap.get("countNum"));
        return jsonObject;
    }

    /**
     * 删除图片
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("picManager/delImg")
    @ResponseBody
    public Object delImages(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String imageId = request.getParameter("imgId");
        fileService.delImages(imageId);

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("code", "100");
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 图片管理页，修改标签
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("picManager/editClass")
    public String editImagesClass(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String imageId = request.getParameter("imgId");
        String[] classText = request.getParameter("classText").split(",");

        int userId = Integer.parseInt(MapUtils.getString(getUser(request.getSession()), "id"));

        String trainer = MapUtils.getString(getUser(request.getSession()), "trainer");//是否训练用户
        if (trainer.equals("true")) {
            fileService.editImagesClass(Integer.parseInt(imageId), classText, userId, false);
        } else {
            fileService.editImagesClass(Integer.parseInt(imageId), classText, userId, true);
        }

        return "/file/file/fileList";
    }

    /**
     * 图片上传修改标签
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("fileUpload/editClass")
    @ResponseBody
    public Object editImagesTag(HttpServletRequest request) throws Exception {
        String imageId = request.getParameter("imgId");
        String classText = request.getParameter("classText");
        String flag = request.getParameter("flag");

        int userId = Integer.parseInt(MapUtils.getString(getUser(request.getSession()), "id"));
        if (flag.equals("add")) {
            String[] classList = classText.split(" ");
            fileService.addImagesTags(Integer.parseInt(imageId), classList, userId);
        } else if (flag.equals("del")) {
            fileService.delImagesClass(Integer.parseInt(imageId), classText, userId);
        }

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("code", "100");
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }
}
