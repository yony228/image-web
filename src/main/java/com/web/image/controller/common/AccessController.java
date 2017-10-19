package com.web.image.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.web.image.common.Md5Util;
import com.web.image.common.UserStatus;
import com.web.image.controller.BaseController;
import com.web.image.service.IAccessService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录
 *
 * @author zll
 *         2017-6-19
 */
@Controller
@RequestMapping
public class AccessController extends BaseController {

    @Autowired
    IAccessService accessService;

    @RequestMapping("/")
    public String home(HttpServletRequest request, ModelMap modelMap) {
        return "fileSearch/init";
    }

    /**
     * 进入登录页面
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/common/login";
    }

    /**
     * 进入注册页面
     * @return
     * @throws Exception
     */
    @RequestMapping("/register")
    public String register() throws Exception {
        return "/common/register";
    }

    /**
     * 用户登录
     * @param request
     * @param param
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping("/access/login")
    @ResponseBody
    public Object loginTest(HttpServletRequest request, @RequestParam Map param) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap();

        Map userInfo = null;
        //查询数据库获得用户角色和用户权限
        if (StringUtils.isNoneEmpty((String) param.get("username")) && StringUtils.isNoneEmpty((String) param.get("password"))) {
            param.put("password", Md5Util.EncoderByMd5((String) param.get("password")));
            userInfo = accessService.getUserInfo(param);
            if (MapUtils.isNotEmpty(userInfo)) {
                Map userMap = (Map) userInfo.get("user");
                if (MapUtils.getString(userMap, "status").equals(String.valueOf(UserStatus.BLOCK.getStatus()))) {
                    map.put("code", 200);
                    map.put("msg", "用户已停用，请联系管理员。");
                } else {
                    map.put("code", 100);
                    request.getSession().setAttribute("userInfo", userInfo);
                }
            } else {
                map.put("code", 200);
                map.put("msg", "用户名或密码错误");
            }
        }

        JSONObject jsonObject = new JSONObject(map);
        return jsonObject;
    }

    /**
     * 登出
     *
     * @param request
     * @param param
     * @return
     */
    @RequestMapping("/common/access/logout")
    public String logout(HttpServletRequest request, @RequestParam Map param) {
        request.getSession().invalidate();
        //return "file/fileSearchInit";
        return "/login";
    }

    /**
     * 注册用户
     * @param request
     * @param param
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping("/access/registerUser")
    @ResponseBody
    public Object registerUser(HttpServletRequest request, @RequestParam Map param) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap();
        if (accessService.findUserByName((String) param.get("username")) != null) {
            map.put("code", 200);
            map.put("msg", "用户名已存在");
        } else {
            param.put("password", Md5Util.EncoderByMd5((String) param.get("password")));

            //注册用户
            accessService.insertGeneralUser(param);
            map.put("code", 100);
            map.put("msg", "注册成功");
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject;
    }
}