package edu.nudt.das.sansiro.login.interceptor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by yony on 17-6-13.
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {


    private static List<String> sessionAttributeNames;


    private static String redirectStr;

    public LoginInterceptor(@Value("#{'${sansiro.login.interceptor.session.attributer.names}'.split(',')}") List<String> sessionAttributeNames,
                            @Value("${sansiro.login.interceptor.redirect.string}") String redirectStr) {
        super();
        this.sessionAttributeNames = sessionAttributeNames;
        this.redirectStr = redirectStr;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Method method = handlerMethod.getMethod();
        final Class<?> clazz = method.getDeclaringClass();
        if (clazz.isAnnotationPresent(Auth.class) || method.isAnnotationPresent(Auth.class)) {
            //判断登录
            if (!isNotNull(sessionAttributeNames, request)) {
//          if(ObjectUtils.allNotNull(request.getSession().getAttribute("userInfo"))) {
                response.sendRedirect(redirectStr);
                return false;
            }
        }
        return true;
    }

    private boolean isNotNull(List<String> list, HttpServletRequest request) {
        for (String s : list) {
            if (!ObjectUtils.allNotNull(request.getSession().getAttribute(s))) {
                return false;
            }
        }
        return true;
    }
}
