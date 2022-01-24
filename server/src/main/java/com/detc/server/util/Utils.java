package com.detc.server.util;

import lombok.extern.log4j.Log4j2;

/**
 * @author szqsy17
 */
@Log4j2
public class Utils {

    /**
     * 日期格式：yyyy-MM-dd HH:mm:ss
     */
    private final static String TIME_REGEX = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

    /**
     * 字符串为空
     */
    public static boolean isStrBlank(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 多个字符串全为空
     */
    public static boolean isStrAllBlank(String... str) {
        return loopBlank(true, "all", str);
    }

    /**
     * 多个字符串存在空
     */
    public static boolean isStrExistBlank(String... str) {
        return loopBlank(false, "exist", str);
    }

    /**
     * 判断多个字符串为空的循环体
     */
    private static boolean loopBlank(boolean defaultValue, String type, String... str) {
        boolean isBlank, res = defaultValue;
        for (String s : str) {
            isBlank = s == null || "".equals(s.trim());

            if (!isBlank && "all".equals(type)) {
                res = false;
                break;
            } else if (isBlank && "exist".equals(type)) {
                res = true;
                break;
            }
        }
        return res;
    }

    /**
     * 判断时间是否满足格式：yyyy-MM-dd HH:mm:ss
     */
    public static boolean isTimeCorrect(String time) {
        return time != null && time.matches(TIME_REGEX);
    }

}
