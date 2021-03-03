package com.river.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.river.api.dto.system.SysUserDto;
import com.river.api.entity.system.SysUser;
import com.river.common.core.util.Result;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
public interface ISysUserService extends IService<SysUser> {


    /**
     *  用户登录信息获取
     * @param param
     * @return
     */
    SysUserDto findSysUserLogin(SysUserDto param);

    /**
     *  用户登录信息获取
     * @param param
     * @return
     */
    SysUserDto findSysUserDetail(SysUserDto param);

    /**
     * 修改用户
     * @param param
     * @return
     */
    Result modify(SysUserDto param) throws Exception;


}
