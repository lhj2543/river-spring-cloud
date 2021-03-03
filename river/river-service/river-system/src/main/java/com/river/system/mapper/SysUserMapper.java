package com.river.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.river.api.dto.system.SysUserDto;
import com.river.api.entity.system.SysUser;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     *  用户登录信息获取
     * @param param
     * @return
     */
    public SysUserDto findSysUserLogin(SysUserDto param);

    /**
     *  用户登录详情
     * @param param
     * @return
     */
    public SysUserDto findSysUserDetail(SysUserDto param);

}
