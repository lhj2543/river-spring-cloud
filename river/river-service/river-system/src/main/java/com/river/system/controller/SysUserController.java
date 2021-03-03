package com.river.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.dto.system.SysUserDto;
import com.river.api.entity.system.SysAccount;
import com.river.api.entity.system.SysMenu;
import com.river.api.entity.system.SysUser;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.Result;
import com.river.common.mybatis.util.MyBatisUtils;
import com.river.common.security.annotation.Inner;
import com.river.system.service.ISysAccountService;
import com.river.system.service.ISysMenuService;
import com.river.system.service.ISysUserRoleService;
import com.river.system.service.ISysUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/sysUser")
@Slf4j
@Api(value = "sysUser",tags="用户管理")
public class SysUserController {

    @Autowired
    ISysUserService sysUserService;

    @Autowired
    ISysMenuService sysMenuService;

    @Autowired
    ISysUserRoleService userRoleService;

    @Autowired
    ISysAccountService sysAccountService;

    /**
     * 获取用户登录信息
     * @param param
     * @return
     */
    @Inner //feign内部接口调用不鉴权注解
    @PostMapping("/getLoginUser")
    public Result getLoginUser(@RequestBody  SysUserDto param){

        try {
            log.info("获取用户登录信息,loginId="+param.getUserCd());
            if(StringUtils.isBlank(param.getUserCd())){
                return Result.failed("用户名不能为空！");
            }

            param.setRoleStatus("1");
            SysUserDto user = sysUserService.findSysUserLogin(param);

            if(user==null){
                return Result.failed("用户不存在！");
            }

            if(!user.getUserRoles().isEmpty()){
                //获取用户资源权限
                List<SysMenu> sysUserMenu = sysMenuService.findSysUserMenu(user);
                user.setSysUserMenu(sysUserMenu);
            }

            return Result.ok(user);

        }catch (Exception e){
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }


    }

    /**
     * 用户列表
     * @param param 参数
     * @return Result
     */
    @RequestMapping(value = "/query")
    @PreAuthorize("@pms.hasPermission('sys_user_query')")
    public Result query(@RequestBody SysUserDto param){

        log.info("查询用户列表开始");
        try {
            QueryWrapper queryWrapper = Wrappers.query()
                    .select("*,(select licence from sys_account a where a.user_cd=SYS_USER.user_cd) as accountStatus");

            MyBatisUtils.like(new String[]{"user_cd","user_name","phone_number"},param,queryWrapper);

            Page page = sysUserService.page(param.getPage(),queryWrapper);

            return Result.ok(page);

        } catch(Exception e) {
            log.error("查询用户列表异常",e);
            e.printStackTrace();
            return Result.failed("查询用户列表异常");
        }
    }

    @RequestMapping(value = "/detail")
    @PreAuthorize("@pms.hasPermission('sys_user_detail')")
    public Result detail(SysUserDto param){

        log.info("查询用户详情开始");
        SysUserDto result = new SysUserDto();
        try {

            if(StringUtils.isNotBlank(param.getSid())){

                SysUserDto sysUser = sysUserService.findSysUserDetail(param);

                result = sysUser == null ? result : sysUser;
                SysAccount sysAccount = result.getSysAccount();
                if(sysAccount == null){
                    result.setSysAccount(new SysAccount());
                }
            }else{
                //新增时初始化数据
                SysAccount sysAccount = new SysAccount();
                sysAccount.setLicence("1");
                sysAccount.setType("1");
                sysAccount.setStartDate(new Date());
                sysAccount.setEndDate(DateUtils.parseDate("2999-12-31","yyyy-MM-dd"));
                result.setSysAccount(sysAccount);
            }

        } catch(Exception e) {
            log.error("查询用户详情异常",e);
            e.printStackTrace();
            return Result.failed("查询用户详情异常");
        }

        return Result.ok(result);
    }

    @DeleteMapping(value = "/delRole")
    @PreAuthorize("@pms.hasPermission('sys_user_delete')")
    public Result delRole(String sid){

        log.info("删除用户角色开始");
        try {
            if(StringUtils.isBlank(sid)){
                return  Result.failed("id不能为空！");
            }

            boolean b = userRoleService.removeById(sid);
            return Result.ok(b,"删除用户角色成功");

        } catch(Exception e) {
            log.error("删除用户角色异常",e);
            e.printStackTrace();
            return Result.failed("删除用户角色异常");
        }

    }

    @PostMapping(value = "/deletes")
    @PreAuthorize("@pms.hasPermission('sys_user_delete')")
    public Result delete(@RequestBody List<SysUser> param){

        log.info("删除用户开始");
        SysUser result = new SysUser();
        try {
            for(SysUser u:param){
                sysUserService.removeById(u.getSid());

                SysAccount sysAccount = new SysAccount();
                sysAccount.setUserCd(u.getUserCd());
                sysAccountService.remove(Wrappers.<SysAccount>lambdaQuery().eq(SysAccount::getUserCd,u.getUserCd()));
            }

           return Result.ok("删除用户成功");

        } catch(Exception e) {
            log.error("删除用户异常",e);
            e.printStackTrace();
            return Result.failed("删除用户异常");
        }

    }

    @PostMapping(value = "/modify")
    @PreAuthorize("@pms.hasPermission('sys_user_modify')")
    public Result modify(@RequestBody SysUserDto param){
        String userCd = param.getUserCd();

        log.info("新增/修改用户开始 usreCd="+userCd);
        SysUser result = param;
        try {
            return sysUserService.modify(param);
        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }
    }

}

