package com.detc.server.mapper;

import com.detc.server.model.MirrorModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author szqsy17
 */
@Repository
public interface MirrorMapper {

    @InsertProvider(type = MirrorSqlProvider.class, method = "addMirror")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addOneMirror(MirrorModel mirrorModel);

    @Delete("DELETE FROM mirror WHERE id = #{id}")
    int deleteOneMirror(String id);

    @UpdateProvider(type = MirrorSqlProvider.class, method = "updateOneMirror")
    int updateOneMirror(MirrorModel mirrorModel);

    @Select("SELECT * FROM mirror ORDER BY id DESC")
    List<MirrorModel> getAllMirror();

    @Select("SELECT * FROM mirror ORDER BY id DESC")
    List<MirrorModel> getMirror();

    @Select("SELECT * FROM mirror WHERE id = #{id}")
    MirrorModel getOneMirror(String id);

    @Select("SELECT * FROM mirror WHERE mirrorVersion = #{mirrorVersion}")
    MirrorModel getOneMirrorByVersion(String mirrorVersion);

    @Select("SELECT * FROM mirror ORDER BY id DESC LIMIT 1")
    MirrorModel getLastOneMirror();

    @Select("SELECT * FROM mirror WHERE id > #{targetId} ORDER BY id DESC")
    List<MirrorModel> getMirrorAfterTargetVersion(String targetId);

}
