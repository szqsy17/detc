package com.detc.server.util;

import com.detc.server.model.Code;
import com.detc.server.model.Result;

/**
 * @author szqsy17
 */
public class ResultUtils {

    public static <T> Result<T> success() {
        return Result.<T>builder().status(Code.SUCCESS).build();
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder().status(Code.SUCCESS).data(data).build();
    }

    public static <T> Result<T> failed(String msg) {
        return Result.<T>builder().status(Code.FAILED).msg(msg).build();
    }

}
