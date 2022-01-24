package com.detc.server.controller;

import com.detc.server.annotation.NeedRoleOne;
import com.detc.server.model.Code;
import com.detc.server.model.Result;
import com.detc.server.service.CarService;
import com.detc.server.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author szqsy17
 */
@Log4j2
@NeedRoleOne
@RestController
@RequestMapping(value = "/car", produces = "application/json;charset=utf-8")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    private Result<?> res;

    @GetMapping(value = "/getAllCar")
    public String getAllCar() {
        res = carService.getAllCar();

        return HttpUtils.setMessage("200", res.getData());
    }

    @PostMapping(value = "/toggleCarUpgrade")
    public String toggleCarUpgrade(String id, Integer before) {
        res = carService.toggleCarUpgrade(id, before);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "切换成功");
    }

    @PostMapping(value = "/changeCarTargetVersion")
    public String changeCarTargetVersion(String id, String targetVersionId) {
        res = carService.changeCarTargetVersion(id, targetVersionId);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "修改成功");
    }

}
