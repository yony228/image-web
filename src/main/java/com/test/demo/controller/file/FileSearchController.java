package com.test.demo.controller.file;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.test.demo.common.FileWebConf;
import com.test.demo.controller.BaseController;
import com.test.demo.entity.dto.ClassificationsDto;
import com.test.demo.service.FileService;
import edu.nudt.das.image.grpc.client.prediction.ImagePredictClient;
import edu.nudt.das.sansiro.login.interceptor.Auth;
import freemarker.ext.util.IdentityHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;

/**
 * email: yony228@163.com
 * Created by Administrator on 2017/5/17.
 */
@Controller
@RequestMapping("/fileSearch")
public class FileSearchController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSearchController.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private FileWebConf fileWebConf;

    @RequestMapping("init")
    public String init(HttpServletRequest request, ModelMap modelMap) throws Exception {
        modelMap.put("returnFiles", null);
        return "file/fileSearchInit";
    }

    /**
     * 根据文字搜索
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("key/searchList")
    public String searchListByKeyName(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String keyName = request.getParameter("keyName");

        //根据标签搜索
        List<String> returnFile = fileService.queryPicByLabelStr(keyName);
        LOGGER.info("搜索完成." + keyName);

        modelMap.put("returnFiles", returnFile);
        modelMap.put("fileUrl", fileWebConf.getPicFullUrl());
        modelMap.put("keyName", keyName);
        return "file/fileSearchList";
    }

    /**
     * 切换标签
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("queryPicByKeyName")
    @ResponseBody
    public Object queryPicByKeyName(HttpServletRequest request) throws Exception {
        String keyName = request.getParameter("keyName");

        //根据标签搜索
        List<String> returnList = new ArrayList<>();
        returnList.add(keyName);
        int nowPage = 1;
        int pageSize = 30;
        List<String> returnFile = fileService.queryPicByLabels(returnList, nowPage, pageSize);
        LOGGER.info("搜索完成." + keyName);

        Map<String, Object> returnMap = new IdentityHashMap();
        returnMap.put("data", returnFile);

        int countNum = fileService.queryPicByLabelsCount(returnList);
        int maxPage = countNum / pageSize;
        if (countNum % pageSize > 0) {
            maxPage += 1;
        }
        returnMap.put("maxPage", maxPage);

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 根据图片搜索
     *
     * @param request
     * @param modelMap
     * @param multiReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "pic/searchList", method = RequestMethod.POST)
    public String searchListByPic(HttpServletRequest request, ModelMap modelMap, MultipartHttpServletRequest multiReq) throws Exception {
        String fileName = multiReq.getFile("file").getOriginalFilename();
        LOGGER.info("上传文件名：" + fileName);
        InputStream fis = multiReq.getFile("file").getInputStream();

//        String filePicUrl = fileService.fileUpload(fis, fileName);
//        modelMap.put("filePicUrl", filePicUrl);
//        LOGGER.info("图片上传完成。路径：" + filePicUrl);

        // 获取图片标签
        List<String> returnList = fileService.clintDoPredict(fileWebConf.getClintServer(), fileWebConf.getClintServerPort(), fis);

        //根据标签搜索
        int nowPage = 1;
        int pageSize = 30;
        List<String> returnFile = fileService.queryPicByLabels(returnList, nowPage, pageSize);

        //统计标签数据
        List<ClassificationsDto> keyNameList = fileService.queryPicCountByLabels(returnList);
        modelMap.put("keyNameList", keyNameList);

        modelMap.put("returnFiles", returnFile);
        modelMap.put("fileUrl", fileWebConf.getPicFullUrl());

        //翻页数据
        String returnStr = "";
        for (int i = 0; i < returnList.size(); i++) {
            returnStr += returnList.get(i) + ",";
        }
        modelMap.put("returnStr", returnStr);
        modelMap.put("nowPage", nowPage);
        int countNum = fileService.queryPicByLabelsCount(returnList);
        int maxPage = countNum / pageSize;
        if (countNum % pageSize > 0) {
            maxPage += 1;
        }
        modelMap.put("maxPage", maxPage);

        return "file/fileSearchList";
    }

    /**
     * 翻页
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("queryPicPage")
    @ResponseBody
    public Object queryPicPage(HttpServletRequest request) throws Exception {
        String returnStr = request.getParameter("returnStr");
        System.out.println(returnStr);
        String nowPage = request.getParameter("nowPage");

        String[] returnArr = returnStr.split(",");
        List<String> returnList = Arrays.asList(returnArr);

        //根据标签搜索
        List<String> returnFile = fileService.queryPicByLabels(returnList, Integer.parseInt(nowPage), 20);

        Map<String, Object> returnMap = new IdentityHashMap();
        returnMap.put("data", returnFile);
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }
}
