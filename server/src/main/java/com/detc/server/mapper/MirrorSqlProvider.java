package com.detc.server.mapper;

import com.detc.server.model.MirrorModel;
import com.detc.server.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author szqsy17
 */
@Log4j2
public class MirrorSqlProvider {

    private SQL sql;

    public String addMirror(MirrorModel mirrorModel) {
        sql = new SQL();

        sql.INSERT_INTO("mirror")
                .INTO_COLUMNS("mirrorVersion, iviVersion, clusterVersion, mcuVersion, mirrorFilePath, md5")
                .INTO_VALUES(" #{mirrorVersion}, #{iviVersion}, #{clusterVersion}, #{mcuVersion}, #{mirrorFilePath}, #{md5}");
        if (mirrorModel.getProjectName() != null) {
            sql.VALUES("projectName", "#{projectName}");
        }
        if (Utils.isTimeCorrect(mirrorModel.getTime())) {
            sql.VALUES("time", "#{time}");
        }
        if (mirrorModel.getDescription() != null) {
            sql.VALUES("description", "#{description}");
        }
        if (mirrorModel.getNote() != null) {
            sql.VALUES("note", "#{note}");
        }

        return sql.toString();
    }

    public String updateOneMirror(MirrorModel mirrorModel) {
        sql = new SQL();

        sql.UPDATE("mirror");
        if (!Utils.isStrBlank(mirrorModel.getMirrorVersion())) {
            mirrorModel.setMirrorFilePath("/mirror/" + mirrorModel.getMirrorVersion() + ".zip");
            sql.SET("mirrorVersion = #{mirrorVersion}, mirrorFilePath = #{mirrorFilePath}");
        }
        if (!Utils.isStrBlank(mirrorModel.getIviVersion())) {
            sql.SET("iviVersion = #{iviVersion}");
        }
        if (!Utils.isStrBlank(mirrorModel.getClusterVersion())) {
            sql.SET("clusterVersion = #{clusterVersion}");
        }
        if (!Utils.isStrBlank(mirrorModel.getMcuVersion())) {
            sql.SET("mcuVersion = #{mcuVersion}");
        }
        if (mirrorModel.getDescription() != null) {
            sql.SET("description = #{description}");
        }
        if (Utils.isTimeCorrect(mirrorModel.getTime())) {
            sql.SET("time = #{time}");
        }
        sql.WHERE("id = #{id}");

        return sql.toString();
    }

}
