package com.detc.server.mapper;

import com.detc.server.model.MirrorStatisticModel;
import com.detc.server.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author szqsy17
 */
@Log4j2
public class StatisticSqlProvider {

    public String addOneMirrorStatistic(MirrorStatisticModel mirrorStatisticModel) {
        SQL sql = new SQL();

        sql.INSERT_INTO("mirror_statistic");
        sql.INTO_COLUMNS("vin, beforeVersion, targetVersion, status");
        sql.INTO_VALUES("#{vin}, #{beforeVersion}, #{targetVersion}, #{status}");
        if (mirrorStatisticModel.getProjectName() != null) {
            sql.VALUES("projectName", "#{projectName}");
        }
        if (mirrorStatisticModel.getGps() != null) {
            sql.VALUES("gps", "#{gps}");
        }
        if (mirrorStatisticModel.getNote() != null) {
            sql.VALUES("note", "#{note}");
        }
        if (Utils.isTimeCorrect(mirrorStatisticModel.getTime())) {
            sql.VALUES("time", "#{time}");
        }

        return sql.toString();
    }

}
