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
public class UserModel {

    private String id;
    private String username;
    private String password;
    private Integer role;
    private String department;
    private String time;

}
