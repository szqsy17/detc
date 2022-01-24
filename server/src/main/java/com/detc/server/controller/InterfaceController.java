package com.detc.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.detc.server.annotation.SkipVerify;
import com.detc.server.model.*;
import com.detc.server.service.InterfaceService;
import com.detc.server.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Kami
 */
@Log4j2
@SkipVerify
@RestController
@RequestMapping(produces = "application/json;charset=utf-8")
public class InterfaceController {

    private final InterfaceService interfaceService;

    public InterfaceController(InterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }

    @PostMapping(value = "/queryOtaVersion")
    public String queryOtaVersion(@RequestBody OtaRequestInfo otaRequestInfo) {
        JSONObject json = new JSONObject();

        if (Utils.isStrExistBlank(otaRequestInfo.getVin(), otaRequestInfo.getVersion())) {
            json.put("status", -1);
            json.put("error", "参数错误");
            log.error("\"/queryOtaVersion\": 参数错误(vin, version)");
            return json.toString();
        }

        CarModel carModel = CarModel.builder()
                .vin(otaRequestInfo.getVin())
                .version(otaRequestInfo.getVersion())
                .iviVersion(otaRequestInfo.getIviVersion())
                .clusterVersion(otaRequestInfo.getClusterVersion())
                .mcuVersion(otaRequestInfo.getMcuVersion())
                .build();

        return interfaceService.queryOtaVersion(carModel, otaRequestInfo.getProtocolVersion());
    }

    @PostMapping(value = "/uploadLog")
    public String uploadLog(MultipartFile file, String fileInfo) {
        JSONObject json = new JSONObject();

        UploadFileInfo uploadFileInfo = JSON.parseObject(fileInfo, UploadFileInfo.class);

        boolean paramsError = file == null || file.isEmpty() || uploadFileInfo.getType() == null ||
                Utils.isStrExistBlank(uploadFileInfo.getVin(), uploadFileInfo.getFileName()) ||
                (uploadFileInfo.getType() != 1 && uploadFileInfo.getType() != 2 &&
                        uploadFileInfo.getType() != 3);

        if (paramsError) {
            json.put("status", -1);
            json.put("error", "参数错误");
            log.error("\"/uploadLog\": 参数错误(file, vin, fileName, type)");
            return json.toString();
        }

        return interfaceService.uploadLogFile(uploadFileInfo.getProjectName(), uploadFileInfo.getVin(), uploadFileInfo.getType(), uploadFileInfo.getTime(), uploadFileInfo.getFileName(), uploadFileInfo.getNote(), file);
    }

    @PostMapping(value = "/upgradeResult")
    public String upgradeResult(@RequestBody OtaResult otaResult) {
        JSONObject json = new JSONObject();

        boolean paramsError = otaResult.getStatus() == null ||
                Utils.isStrExistBlank(otaResult.getVin(), otaResult.getOriginVersion(), otaResult.getTargetVersion());

        if (paramsError) {
            json.put("status", -1);
            json.put("error", "参数错误");
            log.error("\"/upgradeResult\": 镜像参数错误(status, vin, originVersion, targetVersion)");
            return json.toString();
        }

        List<OtaModuleResult> module = otaResult.getResult();
        for (OtaModuleResult moduleResult : module) {

            paramsError = moduleResult.getResult() == null || moduleResult.getModuleId() == null ||
                    Utils.isStrExistBlank(moduleResult.getOriginVersion(), moduleResult.getTargetVersion()) ||
                    (moduleResult.getResult() != 0 && otaResult.getStatus() == 0) ||
                    (moduleResult.getModuleId() != 1 && moduleResult.getModuleId() != 2 &&
                            moduleResult.getModuleId() != 3);

            if (paramsError) {
                json.put("status", -1);
                json.put("error", "参数错误");
                log.error("\"/upgradeResult\": 模块参数错误(moduleId, result, originVersion, targetVersion)");
                return json.toString();
            }

        }

        return interfaceService.upgradeResult(otaResult);
    }

}
