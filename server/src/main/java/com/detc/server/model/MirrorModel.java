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
public class MirrorModel {

    private String id;
    private String projectName;
    private String mirrorVersion;
    private String iviVersion;
    private String clusterVersion;
    private String mcuVersion;
    private String mirrorFilePath;
    private String description;
    private String md5;
    private String time;
    private String note;

}
