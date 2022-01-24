package com.detc.server.mapper;

import com.detc.server.model.LogModel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author szqsy17
 */
@Repository
public interface LogMapper {

    @InsertProvider(type = LogSqlProvider.class, method = "addLog")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addLog(LogModel logModel);

    @Delete("<script> " +
            "DELETE FROM log WHERE id IN" +
            "<foreach collection='array' index='index' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int deleteLog(String[] ids);

    @Select("SELECT * FROM log ORDER BY id DESC")
    List<LogModel> getAllLog();

    @Select("<script>" +
            "SELECT * FROM log WHERE id IN" +
            "<foreach collection='array' index='index' item='id' open='(' separator=',' close= ')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<LogModel> getLog(String[] ids);

}
