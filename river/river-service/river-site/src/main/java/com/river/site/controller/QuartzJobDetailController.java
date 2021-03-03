package com.river.site.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.api.entity.site.QuartzJobDetail;
import com.river.common.core.util.Result;
import com.river.common.core.util.TreeUtils;
import com.river.site.service.IQuartzJobDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * quartz job详情 控制层
 *
 * @author river
 * @since 2020-09-17
 */
@RestController
@RequestMapping("/quartzJobDetail")
@Slf4j
@Api(value = "quartzJobDetail",tags = "quartz定时任务")
public class QuartzJobDetailController {

    @Autowired
    private IQuartzJobDetailService quartzJobDetailService;

    @GetMapping(value = "/query")
    @ApiOperation(value = "query", httpMethod = "GET", response = Result.class, notes = "查询")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result query(QuartzJobDetail param){

        log.info("查询job树开始");
        try {

            LambdaQueryWrapper<QuartzJobDetail> query = Wrappers.<QuartzJobDetail>lambdaQuery().orderByAsc(QuartzJobDetail::getSortKey);
            if(StringUtils.isNotBlank(param.getType())){
                query.eq(QuartzJobDetail::getType,param.getType());
            }
            List<QuartzJobDetail> rows = quartzJobDetailService.list(query);

            List<TreeUtils.TreeNode> list = new ArrayList<TreeUtils.TreeNode>();
            rows.forEach(row->{
                TreeUtils.TreeNode<QuartzJobDetail> treeNode = new TreeUtils.TreeNode<QuartzJobDetail>();
                treeNode.setSid(row.getSid());
                treeNode.setParentId(row.getParentId());
                treeNode.setDisplayName(row.getDisplayName());
                treeNode.setCode(row.getName());
                treeNode.setExpand(true);
                treeNode.setRefData(row);
                list.add(treeNode);
            });

            List<TreeUtils.TreeNode> treeNodes = TreeUtils.buildTree(list);

            return Result.ok(treeNodes);

        } catch(Exception e) {
            log.error("查询job树异常",e);
            e.printStackTrace();
            return Result.failed("查询job树异常");
        }

    }

    @PostMapping(value = "/deletes")
    @ApiOperation(value = "deletes", httpMethod = "POST", response = Result.class, notes = "删除")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result delete(@RequestBody List<QuartzJobDetail> rows){

        log.info("删除Job开始");
        try {

            if(rows!=null && rows.size()>0){
                rows.forEach(row->{
                    quartzJobDetailService.removeById(row.getSid());
                });
            }

            return Result.result(true);

        } catch(Exception e) {
            log.error("删除Job异常",e);
            e.printStackTrace();
            return Result.failed("删除Job异常");
        }

    }

    @PostMapping(value = "/save")
    @ApiOperation(value = "save", httpMethod = "POST", response = Result.class, notes = "添加job")
    @PreAuthorize("@pms.hasPermission('job_manage')")
    public Result save(@RequestBody QuartzJobDetail param){
        log.info("新增/修改job detail开始");
        try {
            boolean b = quartzJobDetailService.saveOrUpdate(param);
            return Result.result(param.getSid(),b);
        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }
    }


}

