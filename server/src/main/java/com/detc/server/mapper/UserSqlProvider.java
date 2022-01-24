package com.detc.server.mapper;

import com.detc.server.model.UserModel;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author szqsy17
 */
@Log4j2
public class UserSqlProvider {

    public String updateOneUser(UserModel user) {
        SQL sql = new SQL();
        sql.UPDATE("user");
        if (user.getUsername() != null && !"".equals(user.getUsername().trim())) {
            sql.SET("username = #{username}");
        }
        if (user.getPassword() != null && !"".equals(user.getPassword().trim())) {
            sql.SET("password = #{password}");
        }
        if (user.getRole() != null) {
            sql.SET("role = #{role}");
        }
        if (user.getDepartment() != null) {
            sql.SET("department = #{department}");
        }
        sql.WHERE("id = #{id}");
        return sql.toString();
    }

}
