package com.river.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import javax.naming.Name;

/**
 * @author river
 */
@Slf4j
public class JobTestB implements Job {

    @Override
    public void execute(JobExecutionContext job) throws JobExecutionException {

        Trigger trigger = job.getTrigger();
        JobDataMap jobDataMap = trigger.getJobDataMap();
        String name = (String) jobDataMap.get("name");

        log.info("name={},进入quartz定时任务class = {}",name,this.getClass().getName());

    }

}
