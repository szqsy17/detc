package com.detc.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 全局变量
 *
 * @author szqsy17
 */
@Component
public class Variable {

    public static String webUrl;
    public static String storagePath;

    @Value("${server.webUrl}")
    public void setWebUrl(String webUrl) {
        Variable.webUrl = webUrl;
    }

    @Value("${server.storagePath}")
    public void setLogPath(String storagePath) {
        Variable.storagePath = storagePath;
    }

}
