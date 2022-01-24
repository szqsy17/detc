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
public class ModuleStatisticModel {

    private String id;
    private String mirrorStatisticId;
    private Integer module;
    private String beforeVersion;
    private String targetVersion;
    private Integer status;

}
