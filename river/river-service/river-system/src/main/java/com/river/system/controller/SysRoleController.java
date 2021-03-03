package com.river.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.dto.system.SysUserDto;
import com.river.api.dto.system.SysUserRoleDto;
import com.river.api.entity.system.SysAccount;
import com.river.api.entity.system.SysRole;
import com.river.common.core.util.Result;
import com.river.common.mybatis.util.MyBatisUtils;
import com.river.system.service.ISysRoleService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/sysRole")
@Slf4j
@Api(value = "sysRole",tags="角色管理")
public class SysRoleController {

    @Autowired
    ISysRoleService sysRoleService;

    @RequestMapping(value = "/query")
    @PreAuthorize("@pms.hasPermission('sys_role_query')")
    public Result query(@RequestBody SysRole param){

        log.info("查询角色列表开始");

        try {
            QueryWrapper queryWrapper = Wrappers.query();

            MyBatisUtils.like(new String[]{"role_code","role_name"},param,queryWrapper);
            if(StringUtils.isNotBlank(param.getStatus())){
                queryWrapper.eq("status",param.getStatus());
            }

            Page page = sysRoleService.page(param.getPage(),queryWrapper);

            return Result.ok(page);

        } catch(Exception e) {
            log.error("查询角色列表异常",e);
            e.printStackTrace();
            return Result.failed("查询角色列表异常");
        }

    }

    @RequestMapping(value = "/detail")
    @PreAuthorize("@pms.hasPermission('sys_role_detail')")
    public Result detail(SysRole param){

        log.info("查询角色详情开始");
        SysRole result = new SysRole();
        try {

            if(StringUtils.isNotBlank(param.getSid())){
                SysRole sysRole = sysRoleService.getById(param.getSid());
                result = sysRole==null?result:sysRole;
            }else{
                //新增时初始化数据
                result.setStatus("1");
            }

        } catch(Exception e) {
            log.error("查询角色详情异常",e);
            e.printStackTrace();
            Result.failed("查询角色详情异常");
        }
        log.info("查询角色详情结束");

        return  Result.ok(result);
    }

    @PostMapping(value = "/deletes")
    @PreAuthorize("@pms.hasPermission('sys_role_delete')")
    public Result delete(@RequestBody List<SysRole> param){

        log.info("删除角色开始");
        SysRole result = new SysRole();
        try {
            if(param != null){
                List<String> ids = param.stream().map(SysRole::getSid).collect(Collectors.toList());
                boolean b = sysRoleService.removeByIds(ids);

                return  Result.ok(b,"删除角色成功");
            }else {
                return Result.failed("id不能为空！");
            }

        } catch(Exception e) {
            log.error("删除角色异常",e);
            e.printStackTrace();
            return Result.failed("删除角色异常");
        }

    }


    @PostMapping(value = "/modify")
    @PreAuthorize("@pms.hasPermission('sys_role_modify')")
    public Result modify(@RequestBody SysRole param){
        String roleCode = param.getRoleCode();

        log.info("新增/修改角色开始 roleCode="+roleCode);

        try {

            if(StringUtils.isBlank(param.getSid())){
                boolean isExist = this.roleCodeIsExist(roleCode);
                if(isExist){
                    return  Result.failed(roleCode+"该角色名已存在！");
                }
            }

            boolean b = sysRoleService.saveOrUpdate(param);
            return Result.ok(param,"保存成功");

        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }

    }

    /**
     * 角色code 是否已存在
     * @param roleCode
     * @return
     */
    public boolean roleCodeIsExist(String roleCode){

        log.info("验证角色CD是否已存在开始");
        boolean flag = false;
        try {
            SysRole role = sysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, roleCode));
            if(role != null && StringUtils.isNotBlank(role.getSid())){
                flag = true;
            }
        } catch(Exception e) {
            log.error("验证角色CD是否已存在异常",e);
            e.printStackTrace();
        }
        return  flag;

    }

}

