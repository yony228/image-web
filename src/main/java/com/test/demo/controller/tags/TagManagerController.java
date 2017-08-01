package com.test.demo.controller.tags;

import com.alibaba.fastjson.JSONObject;
import com.test.demo.common.FileWebConf;
import com.test.demo.controller.BaseController;
import com.test.demo.controller.classifications.ClassManagerController;
import com.test.demo.service.FileService;
import com.test.demo.service.classificationandtag.interfaces.ITagService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 2017/6/12.
 */
@Controller
@Auth
@RequestMapping("/tag")
public class TagManagerController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassManagerController.class);

    @Autowired
    private ITagService tagService;

    @Autowired
    FileWebConf fileWebConf;

    @Autowired
    private FileService fileService;

    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, @PathVariable String pageFile, @PathVariable String page) throws Exception {
        return "/" + pageFile + "/" + page;
    }

    @RequestMapping("getTagList")
    @ResponseBody
    public Object fuzzy(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String nowPage = request.getParameter("pageNumber");
        if (nowPage == null || nowPage.equals("")) {
            nowPage = "1";
        }
        String condition = request.getParameter("condition");


        Map<String, Object> map = new HashMap();
        map.put("user_id", MapUtils.getString(getUser(request.getSession()), "id"));
        map.put("condition", condition);
        map.put("page_index", nowPage);
        map.put("page_size", 10);

        List<Map<String, Object>> list = tagService.fuzzyQueryTags(map);
        for (Map<String, Object> item : list) {
            item.put("count", tagService.queryImageCountByTag(MapUtils.getLong(item, "id")));
        }

        //统计总数
        map.remove("page_index");
        map.remove("page_size");
        List<Map<String, Object>> countList = tagService.fuzzyQueryTags(map);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", list);
        jsonObject.put("total", countList.size());
        return jsonObject;
    }

    @RequestMapping("addTag")
    @ResponseBody
    public Object addTag(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        String alias = request.getParameter("alias");
        String des = request.getParameter("des");
        String userId = MapUtils.getString(getUser(request.getSession()), "id");

        if (tagService.insertTags(alias, des, userId)) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "标签已存在！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    @RequestMapping("updateTag")
    @ResponseBody
    public Object updateTag(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Map<String, Object> map = new HashMap();
        map.put("id", request.getParameter("tagId"));
        map.put("alias", request.getParameter("alias"));
        map.put("des", request.getParameter("des"));
        map.put("user_id", MapUtils.getString(getUser(request.getSession()), "id"));
        tagService.updateTag(map);

        returnMap.put("code", "100");
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    @RequestMapping("delTag")
    @ResponseBody
    public Object delTag(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        if (tagService.deleteTag(Long.parseLong(request.getParameter("tagId")))) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "标签被引用，不允许删除！");
        }

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 多个文件上传
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("uploadFile")
    public String uploadFile(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String tagId = request.getParameter("tagId");
        int userId = Integer.parseInt(MapUtils.getString(getUser(request.getSession()), "id"));
        List<Map<String, String>> fileList = fileService.saveMultipartFile(request);
        for (Map<String, String> fileMap : fileList) {
            fileService.insertImageTag(fileMap.get("fileName"), tagId, fileMap.get("fileType"), userId);
        }

        return "/tag/tags/tagList";
    }

    @RequestMapping("checkSameTag")
    @ResponseBody
    public Object checkSameTag(HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Map param = new HashMap();
        param.put("alias", request.getParameter("alias"));
        param.put("user_id", MapUtils.getString(getUser(request.getSession()), "id"));
        param.put("not_id", request.getParameter("tagId"));
        if (tagService.checkSameTagPerUser(param)) {
            returnMap.put("code", "200");
            returnMap.put("msg", "分类重复！");
        } else {
            returnMap.put("code", "100");
        }

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }
}
