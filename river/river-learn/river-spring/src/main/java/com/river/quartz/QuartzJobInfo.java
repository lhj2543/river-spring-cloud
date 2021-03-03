package com.river.quartz;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * @author river
 */
@Data
@Accessors(chain = true) // @Accessors用于配置getter和setter方法的生成结果 chain的中文含义是链式的，设置为true，则setter方法返回当前对象
public class QuartzJobInfo {

    /**
     * 主键
     */
    private String sid;
    /**
     * job 名称
     */
    private String jobName;
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
    private String desc;
    /**
     * job 类型 1:CronSchedule 2:SimpleSchedule
     */
    private int type;

    /**
     * corn表达式
     */
    private String cronExpression;
    /**
     * job 状态
     */
    private String jobStatus;
    /**
     * job执行目标
     */
    private String triggerName;
    /**
     * job执行目标组
     */
    private String triggerGroup;


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
    private long withIntervalInSeconds;
    /**
     * 总共执行次数（<=0情况重复执行）
     */
    private int repeatCount;
    /**
     * 下次执行时间
     */
    private Date nextFireTime;
    /**
     * 上次执行时间
     */
    private Date previousFireTime;
    /**
     * 优先级
     */
    private int priority;


    /**
     * 执行参数
     */
    private Map<String,Object> invokeParam;


}
