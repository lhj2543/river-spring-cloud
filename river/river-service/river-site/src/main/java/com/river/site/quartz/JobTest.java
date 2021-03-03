package com.river.site.quartz;


import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *  job测试
 *  : @PersistJobDataAfterExecution和 @DisallowConcurrentExecution注解是不让某个定时任务并发执行，只有等当前任务完成下一个任务才会去执行。
 * @author river
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
@Slf4j
public class JobTest implements Job {

    @Override
    public void execute(JobExecutionContext job) throws JobExecutionException {

        //获取传进来的参数
        JobDataMap data=job.getTrigger().getJobDataMap();
        Map<String,Object> invokeParam =(Map<String,Object>) data.get("invokeParam");

        log.info("执行JobTest......invokeParam={}",invokeParam);

    }

}
