package com.river.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * @author 17822
 */
@Slf4j
public class JobTestA implements Job {

    @Override
    public void execute(JobExecutionContext job) throws JobExecutionException {

        Trigger trigger = job.getTrigger();
        JobDataMap jobDataMap = trigger.getJobDataMap();
        String name = (String) jobDataMap.get("name");

        log.info("name={},进入quartz定时任务class = {}",name,this.getClass().getName());

    }

}
