package com.detc.server.controller;

import com.detc.server.model.Code;
import com.detc.server.model.Result;
import com.detc.server.service.LogService;
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
@RestController
@RequestMapping(value = "/log", produces = "application/json;charset=utf-8")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    private Result<?> res;

    @GetMapping(value = "/getAllLog")
    public String getAllLog() {
        res = logService.getAllLog();

        return HttpUtils.setMessage("200", res.getData());
    }

    @PostMapping(value = "/deleteLog")
    public String deleteLog(String[] ids) {
        res = logService.deleteLog(ids);

        if (res.getStatus() == Code.FAILED) {
            return HttpUtils.setMessage("201", res.getMsg());
        }

        return HttpUtils.setMessage("200", "删除成功");
    }

}
