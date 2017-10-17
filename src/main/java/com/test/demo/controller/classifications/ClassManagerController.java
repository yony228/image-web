package com.test.demo.controller.classifications;

import com.alibaba.fastjson.JSONObject;
import com.test.demo.common.FileWebConf;
import com.test.demo.controller.BaseController;
import com.test.demo.service.FileService;
import com.test.demo.service.classificationandtag.interfaces.IClassificationService;
import com.test.demo.service.classificationandtag.interfaces.ITagService;
import edu.nudt.das.sansiro.login.interceptor.Auth;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
 * Created by Administrator on 2017/6/6.
 */
@Controller
@Auth
@RequestMapping("/classification")
public class ClassManagerController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassManagerController.class);

    @Autowired
    private IClassificationService classificationService;

    @Autowired
    FileWebConf fileWebConf;

    @Autowired
    private FileService fileService;

    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, @PathVariable String pageFile, @PathVariable String page, ModelMap modelMap) throws Exception {
        modelMap.put("modelId", request.getParameter("modelId"));
        return "/" + pageFile + "/" + page;
    }

    @RequestMapping("classList")
    @ResponseBody
    public Object classList(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("model_id", request.getParameter("modelId"));
        map.put("classification", request.getParameter("classification"));
        map.put("des", request.getParameter("des"));
        map.put("alias", request.getParameter("alias"));
        map.put("page_index", request.getParameter("pageNumber"));
        map.put("page_size", request.getParameter("pageSize"));
        //
        map.put("notClassId", request.getParameter("notClassId"));

        List<Map<String, Object>> list = classificationService.fuzzyQueryClassification(map);
        for (Map<String, Object> item : list) {
            item.put("count", classificationService.queryImageCountByClassification(MapUtils.getLong(item, "id")));
        }

        //统计总数
        map.remove("page_index");
        map.remove("page_size");
        List<Map<String, Object>> countList = classificationService.fuzzyQueryClassification(map);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", list);
        jsonObject.put("total", countList.size());
        return jsonObject;
    }

    @RequestMapping("addClassification")
    @ResponseBody
    public Object addClassification(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        String des = request.getParameter("des");
        String classification = request.getParameter("classification");
        String alias = request.getParameter("alias");
        String modelId = request.getParameter("modelId");

        Map<String, Object> map = new HashMap();
        map.put("classification", classification);
        map.put("des", des);
        map.put("alias", alias);
        map.put("model_id", modelId);
        if (classificationService.addClassification(map)) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "分类已存在！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    @RequestMapping("updateClassification")
    @ResponseBody
    public Object updateClassification(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Map<String, Object> map = new HashMap();
        map.put("id", request.getParameter("classificationId"));
        map.put("classification", request.getParameter("classification"));
        map.put("alias", request.getParameter("alias"));
        map.put("des", request.getParameter("des"));
        map.put("model_id", request.getParameter("modelId"));
        if (classificationService.updateClassification(map))
            returnMap.put("code", "100");
        else {
            returnMap.put("code", "200");
            returnMap.put("msg", "更新失败！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    @RequestMapping("delClassification")
    @ResponseBody
    public Object delClassification(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        if (classificationService.deleteClassification(Long.parseLong(request.getParameter("classificationId")))) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "分类被引用，不允许删除！");
        }

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 多个文件上传
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("uploadFile")
    public String uploadFile(HttpServletRequest request) throws Exception {
        String classificationId = request.getParameter("classificationId");
        int userId = Integer.parseInt(MapUtils.getString(getUser(request.getSession()), "id"));
        List<Map<String, String>> fileList = fileService.saveMultipartFile(request);

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        int x = (int) ((Math.random() * (9999 - 1000 + 1)) + 1000);
        String batchNo = "M" + sf.format(new Date()) + String.valueOf(x);
        for (Map<String, String> fileMap : fileList) {
            fileService.insertImageInClassification(fileMap.get("fileName"), classificationId, fileMap.get("fileType"), userId, batchNo, fileMap.get("bak"));
        }
        return "/classification/classifications/classList";
    }

    @RequestMapping("checkSameClass")
    @ResponseBody
    public Object checkSameClass(HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Map param = new HashMap();
        param.put("alias", request.getParameter("alias"));
        param.put("model_id", request.getParameter("modelId"));
        param.put("not_id", request.getParameter("classificationId"));
        if (classificationService.checkSameClassificationOnModel(param)) {
            returnMap.put("code", "200");
            returnMap.put("msg", "分类重复！");
        } else {
            returnMap.put("code", "100");
        }

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }
}