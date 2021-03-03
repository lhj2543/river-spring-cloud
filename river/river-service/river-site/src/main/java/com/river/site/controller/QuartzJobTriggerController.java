package com.river.site.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.dto.site.SiteConnentMainDto;
import com.river.api.entity.site.QuartzJobDetail;
import com.river.api.entity.site.QuartzJobLogs;
import com.river.api.entity.site.QuartzJobTrigger;
import com.river.common.core.exception.BusinessServiceException;
import com.river.common.core.util.Result;
import com.river.site.quartz.QuartzJobInfo;
import com.river.site.quartz.QuartzManager;
import com.river.site.service.IQuartzJobDetailService;
import com.river.site.service.IQuartzJobLogsService;
import com.river.site.service.IQuartzJobTriggerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * quartz job触发器 控制层
 *
 * @author river
 * @since 2020-09-17
 */
@RestController
@RequestMapping("/quartzJobTrigger")
@Slf4j
@Api(value = "quartzJobTrigger",tags = "quartz job 触发器")
public class QuartzJobTriggerController {

    @Autowired
    private QuartzManager quartzManager;
    
    @Autowired
    private IQuartzJobDetailService quartzJobDetailService;

    @Autowired
    private IQuartzJobTriggerService quartzJobTriggerService;

    @Autowired
    private IQuartzJobLogsService quartzJobLogsService;

    @PostMapping(value = "/query")
    @ApiOperation(value = "query", httpMethod = "POST", response = Result.class, notes = "查询")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result query(@RequestBody QuartzJobInfo param){

        log.info("查询job trigger开始");
        try {

            Page triggerPage = quartzJobTriggerService.getTriggerPage(param.getPage(), param);
            return Result.ok(triggerPage);

        } catch(Exception e) {
            log.error("查询job trigger异常",e);
            e.printStackTrace();
            return Result.failed("查询job trigger异常");
        }

    }

    @PostMapping(value = "/jobLogs")
    @ApiOperation(value = "jobLogs", httpMethod = "POST", response = Result.class, notes = "查询job运行日志")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result jobLogs(@RequestBody QuartzJobLogs param){

        log.info("查询job运行日志开始");
        try {
            Page page = param.getPage();
            String targetId = param.getTargetId();
            if(StringUtils.isNotBlank(targetId)){
                LambdaQueryWrapper<QuartzJobLogs> query = Wrappers.<QuartzJobLogs>lambdaQuery(param)
                        .orderByDesc(QuartzJobLogs::getCreatedDate);
                page = quartzJobLogsService.page(param.getPage(), query);
            }

            return Result.ok(page);

        } catch(Exception e) {
            log.error("查询job运行日志异常",e);
            e.printStackTrace();
            return Result.failed("查询job运行日志异常");
        }

    }

    @GetMapping(value = "/jobList")
    @ApiOperation(value = "jobList", httpMethod = "GET", response = Result.class, notes = "job列表")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result jobList(QuartzJobInfo param){

        log.info("查询job列表开始");
        try {

            List jobList = quartzManager.getJobList();
            return Result.ok(jobList);

        } catch(Exception e) {
            log.error("查询job列表异常",e);
            e.printStackTrace();
            return Result.failed("查询job列表异常");
        }

    }

    @PostMapping(value = "/deletes")
    @ApiOperation(value = "deletes", httpMethod = "POST", response = Result.class, notes = "删除")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result delete(@RequestBody List<QuartzJobInfo> rows){

        log.info("删除job触发器开始");
        try {
            rows.forEach(row->{
                quartzManager.deleteTrigger(row.getTriggerName(),row.getTriggerGroup());
                quartzJobTriggerService.removeById(row.getTriggerSid());
            });

            return Result.result(true);

        } catch(Exception e) {
            log.error("删除job触发器异常",e);
            e.printStackTrace();
            return Result.failed("删除job触发器异常");
        }

    }


    @PostMapping(value = "/add")
    @ApiOperation(value = "add", httpMethod = "POST", response = Result.class, notes = "添加job")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result add(@RequestBody QuartzJobTrigger param){

        log.info("添加job 触发器开始");
        try {

            //1:就绪 2:运行中 3：已停止 4：运行异常
            param.setStatus("1");
            boolean b = quartzJobTriggerService.save(param);
            if(b){
                QuartzJobInfo jobInfo = this.setQuartzJobInfo(param);
                quartzManager.addJob(jobInfo);
            }

            return Result.result(b);

        } catch(Exception e) {
            log.error("添加job 触发器异常",e);
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    @RequestMapping(value = "/detail")
    @ApiOperation(value = "detail", httpMethod = "GET", response = Result.class, notes = "详情")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result detail(QuartzJobTrigger param){

        log.info("查询job 触发器详情开始");
        QuartzJobTrigger result = param;
        List<QuartzJobDetail> jobList = null;
        try {

            if(StringUtils.isNotBlank(param.getSid())){
                QuartzJobTrigger row = quartzJobTriggerService.getById(param.getSid());
                result = row==null?result:row;
            }

            jobList = this.getJobList();

        } catch(Exception e) {
            log.error("查询job 触发器详情异常",e);
            e.printStackTrace();
            return  Result.failed("查询job 触发器详情异常");
        }

        log.info("查询job 触发器详情结束");

        return  Result.ok(result).setUnknown(jobList);
    }

    private List<QuartzJobDetail> getJobList(){

        log.info("获取jobDetail列表开始");
        try {

            LambdaQueryWrapper<QuartzJobDetail> query = Wrappers.<QuartzJobDetail>lambdaQuery()
                    .ne(QuartzJobDetail::getType,"1");
            List<QuartzJobDetail> list = quartzJobDetailService.list(query);

            return  list;
        } catch(Exception e) {
            log.error("获取jobDetail列表异常",e);
            e.printStackTrace();
        }

        return null;

    }

    @PostMapping(value = "/modify")
    @ApiOperation(value = "modify", httpMethod = "POST", response = Result.class, notes = "修改触发器")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result modify(@RequestBody QuartzJobTrigger param){

        log.info("添加job 触发器开始");
        try {

            QuartzJobInfo jobInfo = this.setQuartzJobInfo(param,true);

            quartzManager.editTrigger(jobInfo);

            //1:就绪 2:运行中 3：已停止 4：运行异常
            boolean b = quartzJobTriggerService.updateById(param);

            return Result.result(b);

        } catch(Exception e) {
            log.error("添加job 触发器异常",e);
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping(value = "/start")
    @ApiOperation(value = "start", httpMethod = "POST", response = Result.class, notes = "启动触发器")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result start(@RequestBody QuartzJobInfo param){

        log.info("启动触发器开始");
        try {


            quartzManager.resumeTrigger(param.getTriggerName(),param.getTriggerGroup());

            boolean b = this.updateStatus(param.getTriggerSid(), "2");

            return Result.ok("job启动成功！");

        } catch(Exception e) {
            log.error("启动触发器开始异常",e);
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping(value = "/stop")
    @ApiOperation(value = "stop", httpMethod = "POST", response = Result.class, notes = "停止触发器")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result stop(@RequestBody QuartzJobInfo param){

        log.info("停止触发器开始");
        try {

            quartzManager.pauseTrigger(param.getTriggerName(),param.getTriggerGroup());

            boolean b = this.updateStatus(param.getTriggerSid(), "3");

            return Result.ok("job停止成功！");

        } catch(Exception e) {
            log.error("停止触发器开始异常",e);
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping(value = "/runOne")
    @ApiOperation(value = "runOne", httpMethod = "POST", response = Result.class, notes = "job运行一次")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result runOne(@RequestBody QuartzJobInfo param){

        log.info("job运行一次开始");
        try {

            quartzManager.runOne(param);

            return Result.ok("运行成功！");

        } catch(Exception e) {
            log.error("job运行一次异常",e);
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    private boolean updateStatus(String sid,String status){

        //1:就绪 2:运行中 3：已停止 4：运行异常
        LambdaUpdateWrapper<QuartzJobTrigger> update = Wrappers.<QuartzJobTrigger>lambdaUpdate()
                .set(QuartzJobTrigger::getStatus, status).eq(QuartzJobTrigger::getSid, sid);
        return quartzJobTriggerService.update(update);

    }

    private QuartzJobInfo setQuartzJobInfo(QuartzJobTrigger param) throws Exception{
        return this.setQuartzJobInfo(param,false);
    }

    private QuartzJobInfo setQuartzJobInfo(QuartzJobTrigger param,boolean isEdit) throws Exception{

        QuartzJobInfo jobInfo = new QuartzJobInfo();
        String targetId = param.getTargetId();
        if(StringUtils.isNotBlank(targetId)){

            if(isEdit){
                QuartzJobInfo qji = new QuartzJobInfo();
                qji.setTriggerSid(param.getSid());
                Page<QuartzJobInfo> triggerPage = quartzJobTriggerService.getTriggerPage(new Page<QuartzJobInfo>(), qji);
                List<QuartzJobInfo> records = triggerPage.getRecords();
                if(records!=null && records.size()>0){
                    jobInfo.setOldTriggerName(records.get(0).getTriggerName());
                    jobInfo.setOldTriggerGroup(records.get(0).getTriggerGroup());
                }else{
                    throw new BusinessServiceException("job触发器不存在！");
                }
            }

            QuartzJobDetail jobDetail = quartzJobDetailService.getById(targetId);
            if(jobDetail == null){
                throw new BusinessServiceException("job不存在，jobId="+targetId);
            }

            jobInfo.setJobSid(targetId);
            jobInfo.setJobName(jobDetail.getName());
            jobInfo.setJobGroup(jobDetail.getJobGroup());
            jobInfo.setJobDesc(jobDetail.getNotes());
            jobInfo.setJobType(jobDetail.getType());
            jobInfo.setJobClassName(jobDetail.getClassName());
            jobInfo.setJobData(jobDetail.getJobData());

            jobInfo.setTriggerSid(param.getSid());
            jobInfo.setTriggerType(1);
            jobInfo.setTriggerName(param.getTriggerName());
            jobInfo.setTriggerGroup(jobDetail.getJobGroup());
            jobInfo.setCronExpression(param.getCronExpression());
            jobInfo.setTriggerDesc(param.getNotes());
            jobInfo.setTriggerData(param.getTriggerData());

        }else {
            throw new BusinessServiceException("job id 不能为空");
        }
        return jobInfo;

    }

}

