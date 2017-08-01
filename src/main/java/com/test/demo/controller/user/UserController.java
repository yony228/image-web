package com.test.demo.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.test.demo.controller.BaseController;
import com.test.demo.service.UserService;
import edu.nudt.das.sansiro.login.interceptor.Auth;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */
@Controller
@Auth
@RequestMapping("/user")
public class UserController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, @PathVariable String pageFile, @PathVariable String page) {
        return "/" + pageFile + "/" + page;
    }

    /**
     * 用户管理查询
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("queryUserList")
    @ResponseBody
    public Object classList(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("condition", request.getParameter("condition"));
        map.put("page_index", request.getParameter("pageNumber"));
        map.put("page_size", request.getParameter("pageSize"));

        List<Map<String, Object>> list = userService.queryUserListPage(map);

        //统计总数
        map.remove("page_index");
        map.remove("page_size");
        List<Map<String, Object>> countList = userService.queryUserListPage(map);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", list);
        jsonObject.put("total", countList.size());
        return jsonObject;
    }

    /**
     * 重置密码
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("resetPwd")
    @ResponseBody
    public Object resetPwd(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap();
        if (userService.resetPwd(request.getParameter("userId"))) {
            map.put("code", 100);
        } else {
            map.put("code", 200);
            map.put("msg", "重置密码失败");
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject;
    }

    /**
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("updateStatus")
    @ResponseBody
    public Object updateStatus(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap();
        if (userService.updateStatus(request.getParameter("userId"), request.getParameter("status"))) {
            map.put("code", 100);
        } else {
            map.put("code", 200);
            map.put("msg", "重置密码失败");
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject;
    }


    /**
     * 修改密码
     *
     * @param request
     * @param param
     * @return
     */
    @RequestMapping("updatePwd")
    @ResponseBody
    public Object updatePwd(HttpServletRequest request, @RequestParam Map param) {
        String userName = MapUtils.getString(getUser(request.getSession()), "name");
        String oldPwd = param.get("oldPwd").toString();
        String newPwd = param.get("newPwd").toString();

        Map<String, Object> map = new HashMap();
        if (userService.updatePwd(userName, oldPwd, newPwd)) {
            map.put("code", 100);
        } else {
            map.put("code", 200);
            map.put("msg", "用户原密码错误");
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject;
    }
}
