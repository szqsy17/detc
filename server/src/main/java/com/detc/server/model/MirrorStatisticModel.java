package com.detc.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author szqsy17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MirrorStatisticModel {

    private String id;
    private String projectName;
    private String vin;
    private String beforeVersion;
    private String targetVersion;
    private Integer status;
    private String time;
    private String gps;
    private String note;
    private List<ModuleStatisticModel> moduleStatisticModels;

}
