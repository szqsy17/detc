package com.detc.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author szqsy17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogModel {

    private String id;
    private String projectName;
    private Integer module;
    private String vin;
    private String logFileName;
    private String logFilePath;
    private String time;
    private String note;

}
