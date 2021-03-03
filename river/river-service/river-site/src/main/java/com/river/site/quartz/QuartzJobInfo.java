package com.river.site.quartz;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.common.mybatis.model.Pojo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * @author river
 */
@Data
@Accessors(chain = true) // @Accessors用于配置getter和setter方法的生成结果 chain的中文含义是链式的，设置为true，则setter方法返回当前对象
public class QuartzJobInfo{

    /**
     * job sid
     */
    private String jobSid;
    /**
     * job 名称
     */
    private String jobName;
    /**
     * job 显示名称
     */
    private String jobDisplayName;
    /**
     * job 分组
     */
    private String jobGroup;
    /**
     * 任务类名
     */
    private String jobClassName;
    /**
     * job 描述
     */
    private String jobDesc;
    /**
     * job 类型
     */
    private String jobType;
    /**
     * job 状态
     */
    private String jobStatus;
    /**
     * job 参数
     */
    private String jobData;




    /**
     * 主键
     */
    private String triggerSid;

    /**
     * job 触发器名
     */
    private String triggerName;
    /**
     * job 触发器显示名
     */
    private String triggerDisplayName;
    /**
     * job 触发器组
     */
    private String triggerGroup;

    /**
     * job 触发器类型 1:CronSchedule 2:SimpleSchedule
     */
    private int triggerType;
    /**
     * corn表达式
     */
    private String cronExpression;

    /**
     * 触发器执行参数
     */
    private String triggerData;
    /**
     * 触发器描述
     */
    private String triggerDesc;
    /**
     * 触发器状态
     */
    private String triggerStatus;

    private String oldTriggerName;
    private String oldTriggerGroup;



    /**
     * job 开始执行时间
     */
    private Date startTime;
    /**
     * job 结束执行时间
     */
    private Date endTime;
    /**
     * job 间隔(多少秒执行一次)
     */
    private int withIntervalInSeconds;
    /**
     * 总共执行次数（<=0情况重复执行）
     */
    private int repeatCount;

    /**
     * 优先级
     */
    private int priority;

    private String updateBy;
    private Date updateDate;

    private Page page;

    public Page getPage(){
        return this.page == null?new Page():this.page;
    }

}
