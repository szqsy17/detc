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
public class OtaRequestInfo {

    private String projectName;
    private String protocolVersion;
    private String vin;
    private String version;
    private String iviVersion;
    private String clusterVersion;
    private String mcuVersion;

}
