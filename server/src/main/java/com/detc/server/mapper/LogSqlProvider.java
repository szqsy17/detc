package com.detc.server.mapper;

import com.detc.server.model.LogModel;
import com.detc.server.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author szqsy17
 */
@Log4j2
public class LogSqlProvider {

    public String addLog(LogModel logModel) {
        SQL sql = new SQL();
        sql.INSERT_INTO("log")
                .INTO_COLUMNS("module, vin, logFileName, logFilePath")
                .INTO_VALUES("#{module}, #{vin}, #{logFileName}, #{logFilePath}");
        if (logModel.getProjectName() != null) {
            sql.VALUES("projectName", "#{projectName}");
        }
        if (Utils.isTimeCorrect(logModel.getTime())) {
            sql.VALUES("time", "#{time}");
        }
        if (logModel.getNote() != null) {
            sql.VALUES("note", "#{note}");
        }
        return sql.toString();
    }

}
