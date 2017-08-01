package com.test.demo.controller.models;

import com.alibaba.fastjson.JSONObject;
import com.test.demo.controller.BaseController;
import com.test.demo.service.ModelsService;
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
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/22.
 */
@Controller
@Auth
@RequestMapping("/models")
public class ModelsManagerController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelsManagerController.class);

    @Autowired
    private ModelsService modelsService;

    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, @PathVariable String pageFile, @PathVariable String page) throws Exception {
        return "/" + pageFile + "/" + page;
    }

    @RequestMapping("queryModels")
    @ResponseBody
    public Object queryModels(HttpServletRequest request) throws Exception {
        String userId = MapUtils.getString(getUser(request.getSession()), "id");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("user_id", userId);
        List<Map<String, Object>> modelList = modelsService.queryModels(paramMap);

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("modelList", modelList);
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    @RequestMapping("queryModelList")
    @ResponseBody
    public Object queryModelList(HttpServletRequest request) throws Exception {
        String nowPage = request.getParameter("pageNumber");
        if (nowPage == null || nowPage.equals("")) {
            nowPage = "1";
        }
        String condition = request.getParameter("condition");
        String userId = MapUtils.getString(getUser(request.getSession()), "id");

        Map<String, Object> paramMap = new HashMap();
        paramMap.put("user_id", userId);
        paramMap.put("condition", condition);
        paramMap.put("page_index", nowPage);
        paramMap.put("page_size", 10);
        List<Map<String, Object>> modelList = modelsService.queryModels(paramMap);
        for (Map<String, Object> model : modelList) {
            int classCount = modelsService.queryClassCountByModelId(Integer.parseInt(model.get("id").toString()));
            model.put("classCount", classCount);
        }

        //统计总数
        paramMap.remove("page_index");
        paramMap.remove("page_size");
        List<Map<String, Object>> countList = modelsService.queryModels(paramMap);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", modelList);
        jsonObject.put("total", countList.size());
        return jsonObject;
    }

    @RequestMapping("addModels")
    @ResponseBody
    public Object addModels(HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        String des = request.getParameter("des");
        String name = request.getParameter("name");
        String userId = MapUtils.getString(getUser(request.getSession()), "id");

        Map<String, Object> map = new HashMap();
        map.put("name", name);
        map.put("des", des);
        map.put("user_id", userId);
        if (modelsService.addModels(map) != 0) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "模型已存在！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    @RequestMapping("updateModels")
    @ResponseBody
    public Object updateModels(HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        String id = request.getParameter("modelsId");
        String des = request.getParameter("des");
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String showRateTop = request.getParameter("showRateTop");
        String showRateBottom = request.getParameter("showRateBottom");
        String userId = MapUtils.getString(getUser(request.getSession()), "id");

        Map<String, Object> map = new HashMap();
        map.put("id", id);
        map.put("name", name);
        map.put("des", des);
        map.put("status", status);
        map.put("showRateTop", showRateTop);
        map.put("showRateBottom", showRateBottom);
        map.put("user_id", userId);
        if (modelsService.updateModels(map)) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "模型名称重复！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 模型上线
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("updateModelStatus")
    @ResponseBody
    public Object updateModelStatus(HttpServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        String id = request.getParameter("modelsId");
        String status = request.getParameter("status");

        if (modelsService.upModels(id, Integer.parseInt(status))) {
            returnMap.put("code", "100");
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "修改失败！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }
}
