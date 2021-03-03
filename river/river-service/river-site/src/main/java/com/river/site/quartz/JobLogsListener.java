package com.river.site.quartz;

import com.alibaba.fastjson.JSONObject;
import com.river.api.entity.site.QuartzJobLogs;
import com.river.site.service.IQuartzJobLogsService;
import com.river.site.service.impl.QuartzJobLogsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;

import java.util.Date;

/**
 * job监听
 * @author river
 */
@Slf4j
public class JobLogsListener implements JobListener {

    private IQuartzJobLogsService quartzJobLogsService;

    public JobLogsListener(IQuartzJobLogsService quartzJobLogsService){
        this.quartzJobLogsService = quartzJobLogsService;
    }

    @Override
    public String getName() {
        return "JobLogsListener";
    }

    /**
     * 调度前执行
     * @param jobExecutionContext
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {

    }

    /**
     * 调度拒绝执行
     * @param jobExecutionContext
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    /**
     *  调度完成或异常时执行
     * @param job
     * @param e
     */
    @Override
    public void jobWasExecuted(JobExecutionContext job, JobExecutionException e) {

        JobDetail jobDetail = job.getJobDetail();
        Trigger trigger = job.getTrigger();

        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        JobDataMap triggerDataMap = trigger.getJobDataMap();

        String jobName = jobDetail.getKey().getName();
        String jobGroup = jobDetail.getKey().getGroup();

        String jobClass = jobDetail.getJobClass().getName();
        String description = jobDetail.getDescription();
        String exception = null;
        String cronExpression = null;
        Integer status = 1;

        String triggerClass = trigger.getClass().getName();
        if (trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = (CronTrigger) trigger;
            cronExpression = cronTrigger.getCronExpression();
        }
        if (e != null) {
            status = 2;
            exception = e.getMessage();

            log.error("执行job异常，通知管理员！");
        }

        QuartzJobLogs jobLog = new QuartzJobLogs();
        jobLog.setTargetId((String) triggerDataMap.get("triggerId"));
        jobLog.setJobName(jobName);
        jobLog.setJobGroup(jobGroup);
        jobLog.setJobClass(jobClass);
        jobLog.setJobDescription(description);
        jobLog.setRunTime(job.getJobRunTime());
        jobLog.setCronExpression(cronExpression);
        jobLog.setStartTime(job.getFireTime());
        jobLog.setTriggerClass(triggerClass);
        jobLog.setEndTime(new Date(job.getFireTime().getTime() + job.getJobRunTime()));
        jobLog.setJobData(JSONObject.toJSONString(jobDataMap));
        jobLog.setException(exception);
        jobLog.setStatus(status);

        quartzJobLogsService.save(jobLog);

    }

}
