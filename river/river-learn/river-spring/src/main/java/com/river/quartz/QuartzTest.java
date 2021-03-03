package com.river.quartz;


import com.google.common.collect.Lists;
import com.river.common.core.exception.BusinessServiceException;
import org.apache.commons.lang3.ArrayUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QuartzTest {


    public static Scheduler scheduler;


    public static void main(String[] args) throws Exception {

        // 3. 创建scheduler
        SchedulerFactory sfact = new StdSchedulerFactory();
        scheduler = sfact.getScheduler();

        String cron1 = "0/2 * * * * ?";
        addJob("com.river.quartz.JobTestA","JobTestA",cron1,false);

        String cron2 = "0/5 * * * * ?";
        addJob("com.river.quartz.JobTestB","JobTestB",cron2,true);

        /*TriggerKey triggerKey = TriggerKey.triggerKey("JobTestA","job_test");
        scheduler.pauseTrigger(triggerKey);*/

        List jobList = getJobList();
        System.err.println(ArrayUtils.toString(jobList));

        TimeUnit.SECONDS.sleep(2);

        /*scheduler.start();
        System.err.println(ArrayUtils.toString(getJobList()));*/

        TimeUnit.SECONDS.sleep(2);


    }


    public static void addJob(String className,String triggerName,String cron,boolean isStart){
        try {

            String jobName = className;
            String jobGroup = "job_test";

            if (!CronExpression.isValidExpression(cron)) {
                //表达式格式不正确
                throw new RuntimeException("corn表达式格式错误");
            }

            // 1. 创建一个JodDetail实例 将该实例与Hello job class绑定 (链式写法)
            Class<? extends Job> calzz = (Class<? extends Job>) Class.forName(className);
            JobDetail jobDetail = JobBuilder.newJob(calzz).withIdentity(jobName,jobGroup).build();

            //按新的cronExpression表达式构建一个新的trigger
            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger();

            triggerBuilder.withIdentity(triggerName, jobGroup);
            triggerBuilder.startNow();

            //表达式调度构建器(即任务执行的时间,不立即执行)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing();
            triggerBuilder.withSchedule(scheduleBuilder);

            //传递参数
            Map<String,String> param = new HashMap<String,String>();
            param.put("name","卡迪夫");
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.putAll(param);
            triggerBuilder.usingJobData(jobDataMap);

            CronTrigger trigger = (CronTrigger)triggerBuilder.build();

            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.pauseTrigger(trigger.getKey());

            scheduler.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除定时任务
     * @param triggerName
     * @param triggerGroup
     */
    public void deleteJob(String triggerName, String triggerGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        try {
            if (checkExists(triggerName, triggerGroup)) {
                //停止触发器
                scheduler.pauseTrigger(triggerKey);
                //移除触发器
                scheduler.unscheduleJob(triggerKey);
                //删除job
                //scheduler.deleteJob(JobKey.jobKey(triggerName,triggerGroup));
            }
        } catch (SchedulerException e) {
            throw new BusinessServiceException(e.getMessage());
        }
    }

    /**
     * 暂停定时任务
     * @param triggerName
     * @param triggerGroup
     */
    public void pauseTrigger(String triggerName, String triggerGroup) {

        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
            if (checkExists(triggerGroup, triggerGroup)) {
                scheduler.pauseTrigger(triggerKey);
            }
        } catch (SchedulerException e) {
            throw new BusinessServiceException(e.getMessage());
        }

    }

    /**
     * 恢复暂停任务
     * @param triggerName
     * @param triggerGroup
     */
    public void resumeTrigger(String triggerName, String triggerGroup) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
            if (checkExists(triggerName, triggerGroup)) {
                scheduler.resumeTrigger(triggerKey);
            }
        } catch (SchedulerException e) {
            throw new BusinessServiceException(e.getMessage());
        }
    }

    /**
     * 获取任务分组名称
     *
     * @return
     */
    public static List<String> getJobGroupNames() {
        try {
            return scheduler.getJobGroupNames();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    /**
     * 获取任务列表
     */
    public static List getJobList() {
        List list = new ArrayList();
        try {
            List<String> jobGroups = getJobGroupNames();
            for (String groupJob :jobGroups) {
                Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(groupJob));
                for (JobKey jobKey : jobKeys) {
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    for (Trigger trigger : triggers) {
                        TriggerKey triggerKey = trigger.getKey();
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);

                        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                        String cronExpression = "";

                        Long milliSeconds = 0L;
                        Integer repeatCount = 0;
                        Date startDate = null;
                        Date endDate = null;

                        if (trigger instanceof CronTrigger) {
                            CronTrigger cronTrigger = (CronTrigger) trigger;
                            cronExpression = cronTrigger.getCronExpression();
                        } else if (trigger instanceof SimpleTrigger) {
                            SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
                            milliSeconds = simpleTrigger.getRepeatInterval();
                            repeatCount = simpleTrigger.getRepeatCount();
                            startDate = simpleTrigger.getStartTime();
                            endDate = simpleTrigger.getEndTime();
                        }

                        QuartzJobInfo info = new QuartzJobInfo();
                        info.setInvokeParam(trigger.getJobDataMap());
                        info.setJobName(jobKey.getName());
                        info.setJobGroup(jobKey.getGroup());
                        info.setJobClassName(jobDetail.getJobClass().getName());
                        info.setDesc(jobDetail.getDescription());

                        info.setTriggerName(triggerKey.getName());
                        info.setTriggerGroup(triggerKey.getGroup());

                        info.setJobStatus(triggerState.name());
                        info.setCronExpression(cronExpression);

                        info.setRepeatCount(repeatCount);
                        info.setStartTime(startDate);
                        info.setEndTime(endDate);
                        info.setWithIntervalInSeconds(milliSeconds);
                        list.add(info);
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 验证任务是否存在
     * @param triggerName
     * @param triggerGroup
     * @return
     * @throws SchedulerException
     */
    private boolean checkExists(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        return scheduler.checkExists(triggerKey);
    }

}
