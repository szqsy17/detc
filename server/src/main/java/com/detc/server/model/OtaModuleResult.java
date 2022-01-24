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
public class OtaModuleResult {

    private Integer moduleId;
    private String originVersion;
    private String targetVersion;
    private Integer result;

}
