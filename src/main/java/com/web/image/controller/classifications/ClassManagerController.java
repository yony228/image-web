package com.web.image.controller.classifications;

import com.alibaba.fastjson.JSONObject;
import com.web.image.common.FileWebConf;
import com.web.image.controller.BaseController;
import com.web.image.service.IFileService;
import com.web.image.service.IClassificationService;
import edu.nudt.das.sansiro.login.interceptor.Auth;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 训练用户分类管理
 *
 * @author zll
 *         2017/6/6
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
    private IFileService fileService;

    /**
     * 进入页面
     *
     * @param request
     * @param pageFile
     * @param page
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, @PathVariable String pageFile, @PathVariable String page, ModelMap modelMap) throws Exception {
        modelMap.put("modelId", request.getParameter("modelId"));
        return "/" + pageFile + "/" + page;
    }

    /**
     * 查询分类列表
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("classList")
    @ResponseBody
    public Object classList(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("model_id", request.getParameter("modelId"));
        map.put("likeClassification", request.getParameter("classification"));
        map.put("likeDes", request.getParameter("des"));
        map.put("likeAlias", request.getParameter("alias"));
        map.put("page_index", request.getParameter("pageNumber"));
        map.put("page_size", request.getParameter("pageSize"));
        //
        map.put("notClassId", request.getParameter("notClassId"));
        map.put("create_user_id", MapUtils.getString(getUser(request.getSession()), "id"));

        List<Map<String, Object>> list = classificationService.queryClassifications(map);
        for (Map<String, Object> item : list) {
            item.put("count", classificationService.queryImageCountByClassification(MapUtils.getLong(item, "id")));
        }

        //统计总数
        map.remove("page_index");
        map.remove("page_size");
        List<Map<String, Object>> countList = classificationService.queryClassifications(map);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", list);
        jsonObject.put("total", countList.size());
        return jsonObject;
    }

    /**
     * 添加分类
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("addClassification")
    @ResponseBody
    public Object addClassification(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        String des = request.getParameter("des");
        String classification = request.getParameter("classification");
        String alias = request.getParameter("alias");

        Map<String, Object> map = new HashMap();
        map.put("classification", classification);
        map.put("des", des);
        map.put("alias", alias);
        map.put("create_user_id", MapUtils.getString(getUser(request.getSession()), "id"));
        if (classificationService.addClassification(map)) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "分类已存在！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 修改分类
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("updateClassification")
    @ResponseBody
    public Object updateClassification(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Map<String, String> map = new HashMap();
        map.put("id", request.getParameter("classificationId"));
        map.put("alias", request.getParameter("alias"));
        map.put("des", request.getParameter("des"));
        map.put("create_user_id", MapUtils.getString(getUser(request.getSession()), "id"));
        if (classificationService.updateClassification(map))
            returnMap.put("code", "100");
        else {
            returnMap.put("code", "200");
            returnMap.put("msg", "更新失败！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 删除分类
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
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

    /**
     * 检查分类名是否重复
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("checkSameClass")
    @ResponseBody
    public Object checkSameClass(HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        String userId = MapUtils.getString(getUser(request.getSession()), "id");
        List<Map<String, Object>> sameClass = classificationService.findSameClassification(request.getParameter("classificationId"), userId, request.getParameter("alias"), null);
        if (sameClass != null && sameClass.size() > 0) {
            returnMap.put("code", "200");
            returnMap.put("msg", "分类重复！");
        } else {
            returnMap.put("code", "100");
        }

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 复制用户标签到分类
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("copyTags")
    @ResponseBody
    public Object copyTags(HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        String userId = MapUtils.getString(getUser(request.getSession()), "id");
        String tagId = request.getParameter("tagId");

        //TODO
        classificationService.copyTagsToClass(userId, tagId);

        returnMap.put("code", "100");
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }
}