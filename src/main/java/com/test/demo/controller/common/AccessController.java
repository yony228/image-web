package com.test.demo.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.test.demo.common.Md5Util;
import com.test.demo.common.UserStatus;
import com.test.demo.controller.BaseController;
import com.test.demo.service.common.interfaces.IAccessService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yony on 17-4-19.
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

    @RequestMapping("/login")
    public String login(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/common/login";
    }

    @RequestMapping("/register")
    public String register() throws Exception {
        return "/common/register";
    }


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