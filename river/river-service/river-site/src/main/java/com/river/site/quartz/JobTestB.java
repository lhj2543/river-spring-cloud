package com.river.site.quartz;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * : @PersistJobDataAfterExecution和 @DisallowConcurrentExecution注解是不让某个定时任务并发执行，只有等当前任务完成下一个任务才会去执行。
 * @author river
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobTestB implements Job {

    @Override
    public void execute(JobExecutionContext job) throws JobExecutionException {

        Trigger trigger = job.getTrigger();
        JobDataMap triggerJobDataMap = trigger.getJobDataMap();

        JobDetail jobDetail = job.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        Gson gson = new Gson();
        String s = gson.toJson(jobDataMap);

        log.info("进入quartz定时任务class = {}",this.getClass().getName());
        log.info("jobDataMap={}",s);
        log.info("triggerJobDataMap={}",gson.toJson(triggerJobDataMap));

    }

}
