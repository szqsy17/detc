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
public class OtaResult {

    private String projectName;
    private String protocolVersion;
    private String vin;
    private String originVersion;
    private String targetVersion;
    private String time;
    private Integer status;
    private String gps;
    private String note;
    private List<OtaModuleResult> result;

}
