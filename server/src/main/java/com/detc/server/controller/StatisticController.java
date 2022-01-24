package com.detc.server.controller;

import com.detc.server.annotation.NeedRoleOne;
import com.detc.server.model.Result;
import com.detc.server.service.StatisticService;
import com.detc.server.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author szqsy17
 */
@Log4j2
@NeedRoleOne
@RestController
@RequestMapping(value = "/statistic", produces = "application/json;charset=utf-8")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping(value = "/getAllMirrorStatistic")
    public String getAllMirrorStatistic() {
        Result<?> res = statisticService.getAllMirrorStatistic();
        
        return HttpUtils.setMessage("200", res.getData());
    }

}
