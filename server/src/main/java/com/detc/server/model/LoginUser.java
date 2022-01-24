package com.detc.server.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author szqsy17
 */
@Data
@Builder
public class LoginUser {

    private String id;
    private String username;
    private Role role;

}
