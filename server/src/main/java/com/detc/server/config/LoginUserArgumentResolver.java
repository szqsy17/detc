package com.detc.server.config;

import com.detc.server.model.LoginUser;
import com.detc.server.util.JwtUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 参数解析器
 *
 * @author szqsy17
 */
@Log4j2
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> c = parameter.getParameterType();
        return c == LoginUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = (String) webRequest.getAttribute("token", RequestAttributes.SCOPE_REQUEST);
        return LoginUser.builder()
                .id(JwtUtils.getUserId(token))
                .username(JwtUtils.getUsername(token))
                .role(JwtUtils.getUserRole(token))
                .build();
    }

}
