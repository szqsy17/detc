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
public class UploadFileInfo {

    private String projectName;
    private String protocolVersion;
    private String vin;
    private Integer type;
    private String time;
    private String fileName;
    private String note;

}
