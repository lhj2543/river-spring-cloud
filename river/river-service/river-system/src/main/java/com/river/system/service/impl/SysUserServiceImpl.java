package com.river.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.dto.system.SysUserDto;
import com.river.api.dto.system.SysUserRoleDto;
import com.river.api.entity.system.SysAccount;
import com.river.api.entity.system.SysUser;
import com.river.api.entity.system.SysUserRole;
import com.river.common.core.component.CacheCommonManage;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.Result;
import com.river.system.mapper.SysUserMapper;
import com.river.system.service.ISysAccountService;
import com.river.system.service.ISysUserRoleService;
import com.river.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private ISysAccountService sysAccountService;

    @Autowired
    private ISysUserRoleService userRoleService;

    @Autowired
    private  CacheCommonManage cacheCommonManage;

    @Override
    public SysUserDto findSysUserLogin(SysUserDto param){
        return  baseMapper.findSysUserLogin(param);
    }

    @Override
    public SysUserDto findSysUserDetail(SysUserDto param) {
        return baseMapper.findSysUserDetail(param);
    }

    /**
     * 新增/修改用户
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public Result modify(SysUserDto param) throws Exception {

        String userCd = param.getUserCd();

        SysAccount sysAccount = param.getSysAccount();
        sysAccount.setUserCd(userCd);

        if(!StringUtils.equals(sysAccount.getPassword(),sysAccount.getOldPassword())){
            String encodePwd = new BCryptPasswordEncoder().encode(sysAccount.getPassword());
            sysAccount.setPassword(encodePwd);
        }
        /*新增用户*/
        if(StringUtils.isBlank(param.getSid())){

            boolean userIsExist = this.userCdIsExist(userCd);
            if(userIsExist){
                return  Result.failed(userCd+"该用户名已存在！");
            }

            this.save(param);
            sysAccountService.save(sysAccount);

        }else{
            //修改用户
            this.updateById(param);
            sysAccountService.update(sysAccount, Wrappers.<SysAccount>lambdaQuery().eq(SysAccount::getUserCd,sysAccount.getUserCd()));

            param.setAccountStatus(sysAccount.getLicence());
        }

        //用户角色
        Set<SysUserRoleDto> userRoles = param.getUserRoles();
        for(SysUserRole ur: userRoles){
            ur.setUserCd(userCd);
            userRoleService.saveOrUpdate(ur);
        }

        //清除登录用户缓存
        String key = CacheConstants.PREFIX_LOGIN_USER + ":" + userCd;
        cacheCommonManage.cleanCache(null, key);

        return  Result.ok(param,"保存成功");
    }

    /**
     * 验证用户cd 存在
     * @param userCd
     * @return
     */
    public boolean userCdIsExist(String userCd){

        boolean flag = false;

        try {
            SysAccount sysAccount = sysAccountService.getOne(Wrappers.<SysAccount>query().lambda().eq(SysAccount::getUserCd, userCd));
            if(sysAccount !=  null && StringUtils.isNotBlank(sysAccount.getSid())){
                flag = true;
            }

        } catch(Exception e) {
            log.error("验证用户CD是否已存在异常",e);
            e.printStackTrace();
        }

        return  flag;
    }

}
