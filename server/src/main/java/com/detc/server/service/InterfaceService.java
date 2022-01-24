package com.detc.server.service;

import com.alibaba.fastjson.JSONObject;
import com.detc.server.config.Variable;
import com.detc.server.mapper.CarMapper;
import com.detc.server.mapper.LogMapper;
import com.detc.server.mapper.MirrorMapper;
import com.detc.server.mapper.StatisticMapper;
import com.detc.server.model.*;
import com.detc.server.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author szqsy17
 */
@Log4j2
@Service
public class InterfaceService {

    private final LogMapper logMapper;
    private final CarMapper carMapper;
    private final MirrorMapper mirrorMapper;
    private final StatisticMapper statisticMapper;

    public InterfaceService(LogMapper logMapper, CarMapper carMapper, MirrorMapper mirrorMapper, StatisticMapper statisticMapper) {
        this.logMapper = logMapper;
        this.carMapper = carMapper;
        this.mirrorMapper = mirrorMapper;
        this.statisticMapper = statisticMapper;
    }

    /**
     * 查询车辆升级版本
     */
    public String queryOtaVersion(CarModel carModel, String protocolVersion) {
        JSONObject json = new JSONObject();
        CarModel car = carMapper.getOneCar(carModel.getVin());
        // 车辆信息不存在
        if (car == null) {
            carMapper.addOneCar(carModel);
            json.put("status", 1);
            json.put("protocolVersion", protocolVersion);
            json.put("extendData", "");
            json.put("module", "");
            return json.toString();
        }

        String targetMirrorId = car.getTargetVersionId();
        // 允许升级且有要升级的版本
        if (car.getAllowUpgrade() == 1 && !Utils.isStrBlank(targetMirrorId)) {
            MirrorModel targetMirror = mirrorMapper.getOneMirror(targetMirrorId);

            List<JSONObject> list = new ArrayList<>();
            JSONObject mirror = new JSONObject();
            mirror.put("version", targetMirror.getMirrorVersion());
            mirror.put("versionPath", Variable.webUrl + "/download" + targetMirror.getMirrorFilePath());
            mirror.put("md5", targetMirror.getMd5());
            mirror.put("encryptionKey", "");
            mirror.put("note", targetMirror.getDescription());
            mirror.put("moduleId", 0);
            list.add(mirror);

            JSONObject module = new JSONObject();
            module.put("ivi_version", targetMirror.getIviVersion());
            module.put("cluster_version", targetMirror.getClusterVersion());
            module.put("mcu_version", targetMirror.getMcuVersion());

            json.put("status", 0);
            json.put("protocolVersion", protocolVersion);
            json.put("extendData", module);
            json.put("module", list);
            return json.toString();
        }

        json.put("status", 1);
        json.put("protocolVersion", protocolVersion);
        json.put("extendData", "");
        json.put("module", "");
        return json.toString();
    }

    /**
     * 上传日志
     */
    public String uploadLogFile(String projectName, String vin, Integer type, String time, String fileName, String note, MultipartFile uploadFile) {
        JSONObject json = new JSONObject();
        String fullPath = Variable.storagePath + "/log/" + fileName;
        File file = new File(fullPath);
        if (file.exists()) {
            json.put("status", -1);
            json.put("error", "文件已存在");
            log.error("\"/uploadLog\": 文件已存在");
            return json.toString();
        }
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            json.put("status", -1);
            json.put("error", "文件夹创建失败");
            log.error("\"/uploadLog\": 文件夹创建失败");
            return json.toString();
        }

        try {
            uploadFile.transferTo(file);
        } catch (IOException e) {
            json.put("status", -1);
            json.put("error", "文件储存出错");
            log.error("\"/uploadLog\": 文件储存出错");
            return json.toString();
        }

        LogModel logModel = LogModel.builder()
                .projectName(projectName)
                .module(type)
                .vin(vin)
                .logFileName(fileName)
                .logFilePath("/log/" + fileName)
                .time(time)
                .note(note)
                .build();

        logMapper.addLog(logModel);

        json.put("status", 0);
        return json.toString();
    }

    /**
     * 升级结果
     */
    public String upgradeResult(OtaResult otaResult) {
        JSONObject json = new JSONObject();

        // 升级成功修改数据库中车辆信息
        if (otaResult.getStatus() == 0) {
            MirrorModel targetMirror = mirrorMapper.getOneMirrorByVersion(otaResult.getTargetVersion());

            if (targetMirror != null) {
                CarModel carModel = CarModel.builder()
                        .vin(otaResult.getVin())
                        .version(targetMirror.getMirrorVersion())
                        .iviVersion(targetMirror.getIviVersion())
                        .clusterVersion(targetMirror.getClusterVersion())
                        .mcuVersion(targetMirror.getMcuVersion())
                        .targetVersionId("")
                        .build();

                carMapper.carUpgradeSuccess(carModel);
            } else {
                json.put("warning", "升级的目标版本在数据库中不存在");
            }
        }

        MirrorStatisticModel mirror = MirrorStatisticModel.builder()
                .projectName(otaResult.getProjectName())
                .vin(otaResult.getVin())
                .beforeVersion(otaResult.getOriginVersion())
                .targetVersion(otaResult.getTargetVersion())
                .status(otaResult.getStatus())
                .time(otaResult.getTime())
                .gps(otaResult.getGps())
                .note(otaResult.getNote())
                .build();

        statisticMapper.addOneMirrorStatistic(mirror);

        List<OtaModuleResult> otaModuleResults = otaResult.getResult();
        List<ModuleStatisticModel> moduleStatisticModels = new ArrayList<>();
        for (OtaModuleResult result : otaModuleResults) {
            ModuleStatisticModel moduleStatisticModel = ModuleStatisticModel.builder()
                    .mirrorStatisticId(mirror.getId())
                    .module(result.getModuleId())
                    .beforeVersion(result.getOriginVersion())
                    .targetVersion(result.getTargetVersion())
                    .status(result.getResult())
                    .build();

            moduleStatisticModels.add(moduleStatisticModel);
        }

        statisticMapper.addModuleStatistic(moduleStatisticModels);

        json.put("status", 0);
        return json.toString();
    }

}
