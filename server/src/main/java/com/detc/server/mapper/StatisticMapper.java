package com.detc.server.mapper;

import com.detc.server.model.MirrorStatisticModel;
import com.detc.server.model.ModuleStatisticModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author szqsy17
 */
@Repository
public interface StatisticMapper {

    @InsertProvider(type = StatisticSqlProvider.class, method = "addOneMirrorStatistic")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addOneMirrorStatistic(MirrorStatisticModel mirrorStatisticModel);

    @Insert("<script>" +
            "INSERT INTO module_statistic (mirrorStatisticId, module, beforeVersion, targetVersion, status) VALUES " +
            "<foreach collection='list' item='item' index='index' separator=','>" +
            "(#{item.mirrorStatisticId}, #{item.module}, #{item.beforeVersion}, #{item.targetVersion}, #{item.status})" +
            "</foreach>" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addModuleStatistic(List<ModuleStatisticModel> moduleStatisticModels);

    @Select("SELECT * FROM module_statistic WHERE mirrorStatisticId = #{mirrorStatisticId}")
    List<ModuleStatisticModel> getModuleStatistic(String mirrorStatisticId);

    @Select("SELECT * FROM mirror_statistic ORDER BY id DESC")
    @Result(property = "moduleStatisticModels", column = "id", many = @Many(select = "com.detc.server.mapper.StatisticMapper.getModuleStatistic"))
    List<MirrorStatisticModel> getAllMirrorStatistic();

}
