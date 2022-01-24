package com.detc.server.service;

import com.detc.server.config.Variable;
import com.detc.server.mapper.LogMapper;
import com.detc.server.model.LogModel;
import com.detc.server.model.Result;
import com.detc.server.util.ResultUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author szqsy17
 */
@Service
public class LogService {

    private final LogMapper logMapper;

    public LogService(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    /**
     * 获取所有日志
     */
    public Result<?> getAllLog() {
        return ResultUtils.success(logMapper.getAllLog());
    }

    /**
     * 删除日志文件（本地文件和数据库记录）
     */
    public Result<?> deleteLog(String[] ids) {
        List<LogModel> logModels = logMapper.getLog(ids);
        if (ids.length != logModels.size()) {
            return ResultUtils.failed("请刷新后再操作");
        }

        boolean fileIsDelete = true;
        for (LogModel log : logModels) {
            File file = new File(Variable.storagePath + log.getLogFilePath());
            if (file.exists()) {
                fileIsDelete = file.delete();
            }
        }

        if (!fileIsDelete) {
            return ResultUtils.failed("有文件正被使用，删除失败");
        }

        return logMapper.deleteLog(ids) > 0
                ? ResultUtils.success()
                : ResultUtils.failed("日志不存在");
    }

}
