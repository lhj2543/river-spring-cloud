package com.river.system.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.api.dto.site.SiteConnentMainDto;
import com.river.api.entity.site.SiteConnent;
import com.river.api.entity.system.SysAttachs;
import com.river.common.core.util.Result;
import com.river.system.service.impl.SysAttachsServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.util.List;

/**
 * <p>
 * 系统附件表 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-20
 */
@RestController
@RequestMapping("/sysAttachs")
@Slf4j
@Api(value = "sysAttachs",tags = "系统附件管理")
public class SysAttachsController {

    @Autowired
    private SysAttachsServiceImpl sysAttachsService;

    @PostMapping(value = "/save")
    @ApiOperation(value = "save", httpMethod = "POST", response = Result.class, notes = "保存")
    public Result modify(SysAttachs param, MultipartHttpServletRequest request){

        log.info("保存附件开始");
        try {
            param.setStatus("1");
            SysAttachs sysAttachs = sysAttachsService.saveAttach(param, request);
            return Result.ok(sysAttachs.getRows());
        } catch(Exception e) {
            log.error("保存附件异常",e);
            e.printStackTrace();
            return Result.failed("保存附件异常");
        }

    }

    @GetMapping(value = "/getListByTargetId")
    @ApiOperation(value = "getListByTargetId", httpMethod = "GET", response = Result.class, notes = "根据目标ID获取附件集合")
    public Result getListByTargetId(SysAttachs param){

        log.info("根据目标ID获取附件集合开始");
        try {
            if(StringUtils.isBlank(param.getTargetId())){
                return Result.failed("目标Id不能为空");
            }
            List<SysAttachs> rows = sysAttachsService.list(Wrappers.<SysAttachs>lambdaQuery().eq(SysAttachs::getTargetId, param.getTargetId()));
            return Result.ok(rows);
        } catch(Exception e) {
            log.error("根据目标ID获取附件集合开始异常",e);
            e.printStackTrace();
            return Result.failed("根据目标ID获取附件集合开始异常");
        }

    }

    @PostMapping(value = "/updateTargetId")
    @ApiOperation(value = "updateTargetId", httpMethod = "POST", response = Result.class, notes = "更新附件targetId")
    public Result updateTargetId(String targetId,String oldTargetId){

        log.info("更新附件targetId开始");
        try {
            if(StringUtils.isBlank(targetId) || StringUtils.isBlank(oldTargetId)){
                return Result.failed("目标Id不能为空");
            }

            boolean b = sysAttachsService.update(Wrappers.<SysAttachs>lambdaUpdate().set(SysAttachs::getTargetId, targetId).eq(SysAttachs::getTargetId, oldTargetId));

            return Result.result(b);
        } catch(Exception e) {
            log.error("更新附件targetId开始异常",e);
            e.printStackTrace();
            return Result.failed("更新附件targetId开始异常");
        }

    }

    @GetMapping(value = "/preview")
    @ApiOperation(value = "preview", httpMethod = "GET", response = Result.class, notes = "预览")
    public void view(HttpServletRequest request,HttpServletResponse response){

        log.info("保存预览开始");
        try {
            String sid = request.getParameter("sid");
            sysAttachsService.preview(sid,request, response);
        } catch(Exception e) {
            log.error("保存附件异常",e);
            e.printStackTrace();
        }

    }

    @DeleteMapping(value = "/deleteByTargetIds")
    @ApiOperation(value = "deleteByTargetIds", httpMethod = "DELETE", response = Result.class, notes = "根据targetId删除")
    public Result deleteByTargetIds(@RequestBody List<String> targetIds){

        log.info("根据targetId删除附件开始");
        try {
            if(targetIds==null){
                return Result.failed("targetId不能为空");
            }
            for(String id:targetIds){
                sysAttachsService.deleteAttachByOwner(id);
            }
            return Result.ok();

        } catch(Exception e) {
            log.error("根据targetId删除附件异常",e);
            e.printStackTrace();
            return Result.failed("根据targetId删除附件异常");
        }

    }

    @DeleteMapping(value = "/deletes")
    @ApiOperation(value = "deletes", httpMethod = "DELETE", response = Result.class, notes = "list删除")
    public Result deletes(String[] ids){

        log.info("删除附件开始");
        try {
            if(ids==null){
                return Result.failed("id不能为空");
            }
            for(String id:ids){
               sysAttachsService.deleteAttach(id);
            }
            return Result.ok();

        } catch(Exception e) {
            log.error("删除附件异常",e);
            e.printStackTrace();
            return Result.failed("删除附件异常");
        }

    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "delete", httpMethod = "DELETE", response = Result.class, notes = "删除")
    public Result delete(String id){

        log.info("删除附件开始");
        try {
            if(StringUtils.isBlank(id)){
                return Result.failed("id不能为空");
            }
            boolean b = sysAttachsService.deleteAttach(id);
            return Result.result(b);
        } catch(Exception e) {
            log.error("删除附件异常",e);
            e.printStackTrace();
            return Result.failed("删除附件异常");
        }

    }

}

