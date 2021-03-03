package com.river.site.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.entity.site.QuartzJobTrigger;
import com.river.site.mapper.QuartzJobTriggerMapper;
import com.river.site.quartz.QuartzJobInfo;
import com.river.site.service.IQuartzJobTriggerService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * quartz job 触发器 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-10-04
 */
@Service
public class QuartzJobTriggerServiceImpl extends ServiceImpl<QuartzJobTriggerMapper, QuartzJobTrigger> implements IQuartzJobTriggerService {

    @Override
    public Page<QuartzJobInfo> getTriggerPage(Page<QuartzJobInfo> page, QuartzJobInfo quartzJobInfo) {
        Page<QuartzJobInfo> p = baseMapper.getTriggerPage(page, quartzJobInfo);
        return p;
    }
}
