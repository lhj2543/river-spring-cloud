package com.river.site.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.entity.site.QuartzJobLogs;
import com.river.site.mapper.QuartzJobLogsMapper;
import com.river.site.service.IQuartzJobLogsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * quartz任务调度日志 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-10-03
 */
@Service
public class QuartzJobLogsServiceImpl extends ServiceImpl<QuartzJobLogsMapper, QuartzJobLogs> implements IQuartzJobLogsService {

}
