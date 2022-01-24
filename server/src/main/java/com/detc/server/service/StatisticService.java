package com.detc.server.service;

import com.detc.server.mapper.StatisticMapper;
import com.detc.server.model.Result;
import com.detc.server.util.ResultUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author szqsy17
 */
@Log4j2
@Service
public class StatisticService {

    private final StatisticMapper statisticMapper;

    public StatisticService(StatisticMapper statisticMapper) {
        this.statisticMapper = statisticMapper;
    }

    /**
     * 获取所有升级信息
     */
    public Result<?> getAllMirrorStatistic() {
        return ResultUtils.success(statisticMapper.getAllMirrorStatistic());
    }

}
