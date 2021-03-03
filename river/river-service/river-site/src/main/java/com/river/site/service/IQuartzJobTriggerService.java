package com.river.site.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.river.api.entity.site.QuartzJobTrigger;
import com.river.site.quartz.QuartzJobInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * quartz job 触发器 服务类
 * </p>
 *
 * @author river
 * @since 2020-10-04
 */
public interface IQuartzJobTriggerService extends IService<QuartzJobTrigger> {

    Page<QuartzJobInfo> getTriggerPage(Page<QuartzJobInfo> page,QuartzJobInfo quartzJobInfo);

}
