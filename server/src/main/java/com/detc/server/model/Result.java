package com.detc.server.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author szqsy17
 */
@Data
@Builder
public class Result<T> {

    private Code status;
    private String msg;
    private T data;

}
