package com.detc.server.controller;

import com.detc.server.annotation.NeedRoleOne;
import com.detc.server.model.Code;
import com.detc.server.model.MirrorModel;
import com.detc.server.model.Result;
import com.detc.server.service.MirrorService;
import com.detc.server.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author szqsy17
 */
@Log4j2
@NeedRoleOne
@RestController
@RequestMapping(value = "/mirror", produces = "application/json;charset=utf-8")
public class MirrorController {

    private final MirrorService mirrorService;

    public MirrorController(MirrorService mirrorService) {
        this.mirrorService = mirrorService;
    }

    private Result<?> res;

    @GetMapping(value = "/getAllMirror")
    public String getAllMirror() {
        res = mirrorService.getAllMirror();

        return HttpUtils.setMessage("200", res.getData());
    }

    @PostMapping(value = "/beforeUpload")
    public String beforeUploadMirror(String mirrorVersion) {
        res = mirrorService.isMirrorNotExist(mirrorVersion);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "文件不存在");
    }

    @PostMapping(value = "/upload")
    public String uploadMirror(String projectName, String mirrorVersion, String iviVersion, String clusterVersion, String mcuVersion, String description, String time, MultipartFile uploadFile) {
        res = mirrorService.uploadMirrorFile(projectName, mirrorVersion, iviVersion, clusterVersion, mcuVersion, description, time, uploadFile);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "上传成功");
    }

    @PostMapping(value = "/deleteOneMirror")
    public String deleteOneMirror(String id) {
        res = mirrorService.deleteOneMirror(id);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "删除成功");
    }

    @PostMapping(value = "/updateOneMirror")
    public String updateOneMirror(MirrorModel mirrorModel) {
        res = mirrorService.updateOneMirror(mirrorModel);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "更新成功");
    }

    @PostMapping(value = "/getMirrorAfterTargetVersion")
    public String getMirrorAfterTargetVersion(String currentVersion) {
        res = mirrorService.getMirrorAfterTargetVersion(currentVersion);

        return HttpUtils.setMessage("200", res.getData());
    }

}
