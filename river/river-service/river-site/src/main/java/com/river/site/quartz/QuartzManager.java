package com.river.site.quartz;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.river.common.core.exception.BusinessServiceException;
import com.river.common.core.util.Result;
import com.river.common.core.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Quartz 管理
 * @author river
 */
@Service
@Slf4j
public class QuartzManager {

    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;

    /**
     * 新建一个ob
     */
    public void addJob(QuartzJobInfo param ) throws Exception {

        int type = param.getTriggerType();
        if(type==1){
            this.addCronJob(param);
        }else if(type==2){
            this.addSimpleJob(param);
        }else {
            throw  new BusinessServiceException(String.format("job类型设置错误！type=%s",type));
        }

    }

    /**
     * 新建一个Cron表达式job
     * corn表达式方式
     */
    public void addCronJob(QuartzJobInfo param ) throws Exception {


        try {
            boolean b = this.checkTriggerExists(param.getTriggerName(), param.getTriggerGroup());
            if(b){
                throw  new BusinessServiceException("job trigger已存在！");
            }

            JobDetail jobDetail = this.getJobDetail(param);

            Trigger cronTrigger = this.createCronTrigger(param, jobDetail);

            if(scheduler.checkExists(jobDetail.getKey())){
                scheduler.scheduleJob(cronTrigger);
            }else {
                scheduler.scheduleJob(jobDetail, cronTrigger);
            }
            scheduler.pauseTrigger(cronTrigger.getKey());

        }catch (ClassNotFoundException e){
            log.error("添加Quartz定时任务失败jobName={}",param.getJobName(),e);
            throw new BusinessServiceException("job类找不到calss="+e.getMessage());
        }catch (Exception e){
            log.error("添加Quartz定时任务失败jobName={}",param.getJobName(),e);
            throw new BusinessServiceException(e.getMessage());
        }

    }

    /**
     * 新建简单模式job
     * 简单模式
     */
    public void addSimpleJob(QuartzJobInfo param) {
        
        try{

            boolean b = this.checkTriggerExists(param.getTriggerName(), param.getTriggerGroup());
            if(b){
                throw  new BusinessServiceException("job trigger已存在！");
            }

            JobDetail jobDetail = this.getJobDetail(param);

            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(param.getTriggerName(), param.getTriggerGroup());

            Date startTime = param.getStartTime();
            if (startTime != null) {
                triggerBuilder.startAt(startTime);
            }else {
                triggerBuilder.startNow();
            }
            Date endTime = param.getEndTime();
            if (endTime != null) {
                triggerBuilder.endAt(endTime);
            }
            int interval = param.getWithIntervalInSeconds();
            int repeatCount = param.getRepeatCount();
            if ((interval != 0) || (repeatCount != 0)) {
                SimpleScheduleBuilder simpleBuilder = SimpleScheduleBuilder.simpleSchedule();
                if (interval != 0) {
                    simpleBuilder.withIntervalInSeconds(interval);
                }
                if (repeatCount != 0) {
                    simpleBuilder.withRepeatCount(repeatCount);
                } else {
                    //重复执行
                    simpleBuilder.repeatForever();
                }
                triggerBuilder.withSchedule(simpleBuilder);
            }

            //传递参数
            JobDataMap jobDataMap = this.setJobDataMap(param.getTriggerData());
            jobDataMap.put("triggerId",param.getTriggerSid());
            triggerBuilder.usingJobData(jobDataMap);

            SimpleTrigger trigger = (SimpleTrigger)triggerBuilder
                    .forJob(jobDetail)
                    .build();

            if(scheduler.checkExists(jobDetail.getKey())){
                scheduler.scheduleJob(trigger);
            }else {
                scheduler.scheduleJob(jobDetail, trigger);
            }

        } catch (ClassNotFoundException e){
            log.error("添加Quartz定时任务失败jobName={}",param.getJobName(),e);
            throw new BusinessServiceException("job类找不到calss="+e.getMessage());
        }catch (Exception e) {
            log.error("添加Quartz定时任务失败jobName={}",param.getJobName(),e);
            throw new BusinessServiceException(e.getMessage());
        }
        
    }

    /**
     * 获取JobDetail
     * @param param
     * @return
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    public JobDetail getJobDetail(QuartzJobInfo param) throws SchedulerException, ClassNotFoundException {

        JobDetail jobDetail = null;
        JobKey jobKey = JobKey.jobKey(param.getJobName(), param.getJobGroup());
        if (scheduler.checkExists(jobKey)){
            jobDetail = scheduler.getJobDetail(jobKey);
        }else{
            //构建job信息
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(param.getJobClassName());
            jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(param.getJobName(), param.getJobGroup())
                    .withDescription(param.getJobDesc())
                    .usingJobData(this.setJobDataMap(param.getJobData()))
                    .build();
        }

        return  jobDetail;
    }

    /**
     * 创建触发器
     */
    public Trigger createCronTrigger(QuartzJobInfo param,JobDetail jobDetail){

        if (!CronExpression.isValidExpression(param.getCronExpression())) {
            //表达式格式不正确
            throw new BusinessServiceException("corn表达式格式错误");
        }

        //按新的cronExpression表达式构建一个新的trigger
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger();

        triggerBuilder.withIdentity(param.getTriggerName(), param.getTriggerGroup());
        triggerBuilder.startNow();

        //表达式调度构建器(即任务执行的时间,不立即执行)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(param.getCronExpression()).withMisfireHandlingInstructionDoNothing();
        triggerBuilder.withSchedule(scheduleBuilder);
        //传递参数
        JobDataMap jobDataMap = this.setJobDataMap(param.getTriggerData());
        jobDataMap.put("triggerId",param.getTriggerSid());
        triggerBuilder.usingJobData(jobDataMap);
        //绑定jobdetail
        triggerBuilder.forJob(jobDetail);

        CronTrigger trigger = (CronTrigger)triggerBuilder.build();

        return  trigger;

    }

    /**
     * 运行一次
     */
    public void runOne(QuartzJobInfo param) {

        param.setTriggerName(param.getTriggerName()+"#runOne"+Math.random());
        this.addSimpleJob(param);

    }

    /**
     * 修改定时任务
     * @param info
     */
    public void editTrigger(QuartzJobInfo info) {

        try {
            if(!StringUtils.equals(info.getTriggerName(),info.getOldTriggerName()) || !StringUtils.equals(info.getTriggerGroup(),info.getOldTriggerGroup())){
                boolean b = this.checkTriggerExists(info.getTriggerName(), info.getTriggerGroup());
                if(b){
                    throw  new BusinessServiceException("job trigger已存在！");
                }

                this.deleteTrigger(info.getOldTriggerName(),info.getOldTriggerGroup());
            }

            JobDetail jobDetail = this.getJobDetail(info);

            Trigger cronTrigger = this.createCronTrigger(info, jobDetail);

            HashSet<Trigger> triggerSet = new HashSet<>();
            triggerSet.add(cronTrigger);
            scheduler.scheduleJob(jobDetail, triggerSet, true);
            scheduler.pauseTrigger(cronTrigger.getKey());

        } catch (Exception e) {
            throw new BusinessServiceException(e.getMessage());
        }

    }

    /**
     * 删除触发器
     * @param triggerName
     * @param triggerGroup
     */
    public void deleteTrigger(String triggerName, String triggerGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        try {
            if (checkTriggerExists(triggerName, triggerGroup)) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
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
    public void pauseTrigger(String triggerName, String triggerGroup) throws SchedulerException {

        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        if (scheduler.checkExists(triggerKey)) {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            String state = triggerState.name();
            if(!StringUtils.equalsIgnoreCase(state,"PAUSED")){
                scheduler.pauseTrigger(triggerKey);
            }
        }else {
            throw new BusinessServiceException("停止失败，job不存在！");
        }

    }

    /**
     * 恢复暂停任务
     * @param triggerName
     * @param triggerGroup
     */
    public void resumeTrigger(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        if (scheduler.checkExists(triggerKey)) {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            String state = triggerState.name();
            if(StringUtils.equalsIgnoreCase(state,"PAUSED")){
                scheduler.resumeTrigger(triggerKey);
            }
        }else {
            throw new BusinessServiceException("启动失败，job不存在！");
        }
    }

    /**
     * 验证触发器是否存在
     * @param triggerName
     * @param triggerGroup
     * @return
     * @throws SchedulerException
     */
    private boolean checkTriggerExists(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        return scheduler.checkExists(triggerKey);
    }
    /**
     * 验证job是否存在
     * @param jobName
     * @param JobGroup
     * @return
     * @throws SchedulerException
     */
    private boolean checkJobExists(String jobName, String JobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, JobGroup);
        return scheduler.checkExists(jobKey);
    }

    /**
     * job 参数设置
     * @param json
     * @return
     */
    private JobDataMap setJobDataMap(String json){

        JobDataMap jobDataMap = new JobDataMap();

        if(StringUtils.isNotBlank(json)) {
            try {
                Gson gson = new Gson();
                HashMap<String, String> resultUserInfoMap = gson.fromJson(json, HashMap.class);
                jobDataMap.putAll(resultUserInfoMap);
            }catch (Exception e){
                throw new BusinessServiceException("job参数格式错误，必须为json格式！");
            }
        }
        return jobDataMap;
    }

    /**
     * 获取任务列表
     */
    public  List getJobList() {
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
                        //info.setTriggerData(trigger.getJobDataMap());
                        info.setJobName(jobKey.getName());
                        info.setJobGroup(jobKey.getGroup());
                        info.setJobClassName(jobDetail.getJobClass().getName());
                        info.setJobDesc(jobDetail.getDescription());

                        info.setTriggerName(triggerKey.getName());
                        info.setTriggerGroup(triggerKey.getGroup());
                        info.setTriggerStatus(triggerState.name());
                        info.setCronExpression(cronExpression);

                        info.setRepeatCount(repeatCount);
                        info.setStartTime(startDate);
                        info.setEndTime(endDate);
                        info.setWithIntervalInSeconds(new Long(milliSeconds).intValue());
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
     * 获取任务分组名称
     *
     * @return
     */
    public  List<String> getJobGroupNames() {
        try {
            return scheduler.getJobGroupNames();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    public static void main(String[] args) {
        try {
            Class<?> aClass = Class.forName("com.river.site.quartz.JobTest");
            String name = aClass.getName();
            System.out.println(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



}
