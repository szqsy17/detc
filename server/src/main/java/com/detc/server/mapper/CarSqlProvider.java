package com.detc.server.mapper;

import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author szqsy17
 */
@Log4j2
public class CarSqlProvider {

    public String toggleCarUpgrade(String id, Integer before) {
        SQL sql = new SQL();

        sql.UPDATE("car");
        sql.SET(before == 1 ? "allowUpgrade = 0" : "allowUpgrade = 1");
        sql.WHERE("id = " + id);

        return sql.toString();
    }

}
