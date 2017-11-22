package com.web.image.controller.tags;

import com.alibaba.fastjson.JSONObject;
import com.web.image.common.FileWebConf;
import com.web.image.common.TagPublic;
import com.web.image.controller.BaseController;
import com.web.image.controller.classifications.ClassManagerController;
import com.web.image.service.IFileService;
import com.web.image.service.ITagService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通用户标签管理
 *
 * @author zll
 *         2017/6/12
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
    private IFileService fileService;

    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, @PathVariable String pageFile, @PathVariable String page) throws Exception {
        return "/" + pageFile + "/" + page;
    }

    /**
     * 查询标签列表
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
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

    /**
     * 添加标签
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("addTag")
    @ResponseBody
    public Object addTag(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        String alias = request.getParameter("alias");
        String des = request.getParameter("des");
        String isPublic = request.getParameter("isPublic");
        String userId = MapUtils.getString(getUser(request.getSession()), "id");

        if (tagService.insertTags(alias, des, userId, isPublic)) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "标签已存在！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 修改标签
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("updateTag")
    @ResponseBody
    public Object updateTag(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Map<String, String> map = new HashMap();
        map.put("id", request.getParameter("tagId"));
        map.put("alias", request.getParameter("alias"));
        map.put("des", request.getParameter("des"));
        map.put("is_public", request.getParameter("isPublic"));
        map.put("user_id", MapUtils.getString(getUser(request.getSession()), "id"));
        tagService.updateTag(map);

        returnMap.put("code", "100");
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 删除标签
     *
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
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

    /**
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("checkSameTag")
    @ResponseBody
    public Object checkSameTag(HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        String userId = MapUtils.getString(getUser(request.getSession()), "id");
        List<Map<String, Object>> sameClass = tagService.findSameTag(request.getParameter("tagId"), userId, request.getParameter("alias"), null);
        if (sameClass != null && sameClass.size() > 0) {
            returnMap.put("code", "200");
            returnMap.put("msg", "标签重复！");
        } else {
            returnMap.put("code", "100");
        }

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 查询所有公开标签
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("queryPublicTagList")
    @ResponseBody
    public Object queryPublicTagList(HttpServletRequest request) throws Exception {
        String nowPage = request.getParameter("pageNumber");
        if (nowPage == null || nowPage.equals("")) {
            nowPage = "1";
        }
        String alias = request.getParameter("alias");

        Map<String, Object> map = new HashMap();
        map.put("is_public", String.valueOf(TagPublic.PUBLIC.getStatus()));
        map.put("likeAlias", alias);
        map.put("page_index", nowPage);
        map.put("page_size", 10);

        List<Map<String, Object>> list = tagService.queryTags(map);
        for (Map<String, Object> item : list) {
            item.put("count", tagService.queryImageCountByTag(MapUtils.getLong(item, "id")));
        }

        //统计总数
        map.remove("page_index");
        map.remove("page_size");
        List<Map<String, Object>> countList = tagService.queryTags(map);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", list);
        jsonObject.put("total", countList.size());
        return jsonObject;
    }
}
