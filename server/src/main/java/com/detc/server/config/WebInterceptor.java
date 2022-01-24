package com.detc.server.config;

import com.detc.server.annotation.NeedRoleOne;
import com.detc.server.annotation.SkipVerify;
import com.detc.server.model.Role;
import com.detc.server.util.HttpUtils;
import com.detc.server.util.JwtUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * 拦截器
 *
 * @author szqsy17
 */
@Log4j2
public class WebInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        // 如果不是api接口
        if (!(handler instanceof HandlerMethod)) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(HttpUtils.setMessage("403", "拒绝访问"));
            return false;
        }

        // 放行
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Annotation classSkipVerify = handlerMethod.getBeanType().getAnnotation(SkipVerify.class);
        Annotation methodSkipVerify = handlerMethod.getMethodAnnotation(SkipVerify.class);
        if (classSkipVerify != null || methodSkipVerify != null) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(HttpUtils.setMessage("401", "没有token"));
            return false;
        }

        String tokenHead = "Bearer ";
        if (token.startsWith(tokenHead)) {
            token = token.substring(7);
        }

        if (JwtUtils.verifyJwt(token)) {
            // 权限管理
            Annotation classNeedRoleOne = handlerMethod.getBeanType().getAnnotation(NeedRoleOne.class);
            Annotation methodNeedRoleOne = handlerMethod.getMethodAnnotation(NeedRoleOne.class);
            if (classNeedRoleOne != null || methodNeedRoleOne != null) {
                if (JwtUtils.getUserRole(token) == Role.USER) {
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write(HttpUtils.setMessage("403", "没有权限"));
                    return false;
                }
            }

            request.setAttribute("token", token);
            return true;
        }

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(HttpUtils.setMessage("401", "无效token"));
        return false;
    }

}
