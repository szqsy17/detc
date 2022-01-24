package com.detc.server.service;

import com.detc.server.config.Variable;
import com.detc.server.mapper.CarMapper;
import com.detc.server.mapper.MirrorMapper;
import com.detc.server.model.MirrorModel;
import com.detc.server.model.Result;
import com.detc.server.util.ResultUtils;
import com.detc.server.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author szqsy17
 */
@Log4j2
@Service
public class MirrorService {

    private final MirrorMapper mirrorMapper;
    private final CarMapper carMapper;

    public MirrorService(MirrorMapper mirrorMapper, CarMapper carMapper) {
        this.mirrorMapper = mirrorMapper;
        this.carMapper = carMapper;
    }

    /**
     * 获得所有镜像
     */
    public Result<?> getAllMirror() {
        return ResultUtils.success(mirrorMapper.getAllMirror());
    }

    /**
     * 检查镜像是否已存在
     */
    public Result<?> isMirrorNotExist(String fileName) {
        String fullPath = Variable.storagePath + "/mirror/" + fileName + ".zip";
        File file = new File(fullPath);
        return !file.exists()
                ? ResultUtils.success()
                : ResultUtils.failed("该版本已存在");
    }

    /**
     * 上传镜像
     */
    public Result<?> uploadMirrorFile(String projectName, String mirrorVersion, String iviVersion, String clusterVersion, String mcuVersion, String description, String time, MultipartFile uploadFile) {
        String fullPath = Variable.storagePath + "/mirror/" + mirrorVersion + ".zip";
        File file = new File(fullPath);
        if (file.exists()) {
            return ResultUtils.failed("版本已存在");
        }
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return ResultUtils.failed("文件夹创建失败");
        }

        MirrorModel mirrorModel = MirrorModel.builder()
                .projectName(projectName)
                .mirrorVersion(mirrorVersion)
                .iviVersion(iviVersion)
                .clusterVersion(clusterVersion)
                .mcuVersion(mcuVersion)
                .mirrorFilePath("/mirror/" + mirrorVersion + ".zip")
                .description(description)
                .time(time)
                .build();
        try {
            mirrorModel.setMd5(DigestUtils.md5DigestAsHex(uploadFile.getInputStream()));
        } catch (IOException e) {
            log.error("文件不存在");
            return ResultUtils.failed("上传文件不存在");
        }

        try {
            uploadFile.transferTo(file);
        } catch (IOException e) {
            log.error("文件储存出错");
            return ResultUtils.failed("上传错误");
        }

        mirrorMapper.addOneMirror(mirrorModel);

        return ResultUtils.success();
    }

    /**
     * 删除一个镜像
     */
    public Result<?> deleteOneMirror(String id) {
        MirrorModel mirror = mirrorMapper.getOneMirror(id);

        carMapper.resetCarTargetVersion(mirror.getId());

        boolean fileIsDelete = true;
        File file = new File(Variable.storagePath + mirror.getMirrorFilePath());
        if (file.exists()) {
            fileIsDelete = file.delete();
        }

        return fileIsDelete && mirrorMapper.deleteOneMirror(id) == 1
                ? ResultUtils.success()
                : ResultUtils.failed("删除失败");
    }

    /**
     * 更新一个镜像
     */
    public Result<?> updateOneMirror(MirrorModel mirror) {
        MirrorModel oldMirror = mirrorMapper.getOneMirror(mirror.getId());
        MirrorModel newMirror = mirrorMapper.getOneMirrorByVersion(mirror.getMirrorVersion());
        if (oldMirror == null) {
            return ResultUtils.failed("操作版本不存在");
        }
        if (newMirror != null) {
            return ResultUtils.failed("目标版本已存在");
        }

        boolean rename = true;
        if (!Utils.isStrBlank(mirror.getMirrorVersion())) {
            File oldFile = new File(Variable.storagePath + oldMirror.getMirrorFilePath());
            File newFile = new File(Variable.storagePath + "/mirror/" + mirror.getMirrorVersion() + ".zip");
            rename = false;
            if (oldFile.exists() && !newFile.exists()) {
                rename = oldFile.renameTo(newFile);
            }
        }

        if (!rename) {
            return ResultUtils.failed("文件重命名失败");
        }

        return mirrorMapper.updateOneMirror(mirror) == 1
                ? ResultUtils.success()
                : ResultUtils.failed("该版本不存在");
    }

    /**
     * 获得车辆允许更新的版本
     */
    public Result<?> getMirrorAfterTargetVersion(String currentVersion) {
        MirrorModel mirrorModel = mirrorMapper.getOneMirrorByVersion(currentVersion);
        return ResultUtils.success(mirrorModel == null
                ? mirrorMapper.getMirror() : mirrorMapper.getMirrorAfterTargetVersion(mirrorModel.getId()));
    }

}
