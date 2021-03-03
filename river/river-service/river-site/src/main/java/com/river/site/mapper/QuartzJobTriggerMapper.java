package com.river.site.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.entity.site.QuartzJobTrigger;
import com.river.site.quartz.QuartzJobInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * quartz job 触发器 Mapper 接口
 * </p>
 *
 * @author river
 * @since 2020-10-04
 */
public interface QuartzJobTriggerMapper extends BaseMapper<QuartzJobTrigger> {

    Page<QuartzJobInfo> getTriggerPage(Page<QuartzJobInfo> page, @Param("quartzJobInfo")QuartzJobInfo quartzJobInfo);

}
