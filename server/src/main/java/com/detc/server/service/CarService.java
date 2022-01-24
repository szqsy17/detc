package com.detc.server.service;

import com.detc.server.mapper.CarMapper;
import com.detc.server.model.Result;
import com.detc.server.util.ResultUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author szqsy17
 */
@Log4j2
@Service
public class CarService {

    private final CarMapper carMapper;

    public CarService(CarMapper carMapper) {
        this.carMapper = carMapper;
    }

    /**
     * 获得所有车辆信息
     */
    public Result<?> getAllCar() {
        return ResultUtils.success(carMapper.getAllCar());
    }

    /**
     * 控制车辆是否升级
     */
    public Result<?> toggleCarUpgrade(String id, Integer before) {
        return carMapper.toggleCarUpgrade(id, before) == 1
                ? ResultUtils.success()
                : ResultUtils.failed("该车不存在");
    }

    /**
     * 修改车辆目标版本
     */
    public Result<?> changeCarTargetVersion(String id, String targetVersionId) {
        return carMapper.changeCarTargetVersion(id, targetVersionId) == 1
                ? ResultUtils.success()
                : ResultUtils.failed("该车不存在");
    }

}
