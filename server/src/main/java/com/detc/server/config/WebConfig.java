package com.detc.server.config;

import com.detc.server.annotation.SkipVerify;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 配置
 *
 * @author szqsy17
 */
@Log4j2
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求，通过判断是否有@SkipVerify注解判断是否跳过校验，放行网站图标
        registry.addInterceptor(new WebInterceptor())
                .addPathPatterns("/**");
    }

    /**
     * 添加token解析器
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver());
    }

    /**
     * 静态资源映射
     */
    @Override
    @SkipVerify
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射图标文件，addResourceLocations中的文件路径末尾一定要有"/"
        // registry.addResourceHandler("/**")
        //        .addResourceLocations("file:" + Variable.webImagePath + "/");
    }

}
