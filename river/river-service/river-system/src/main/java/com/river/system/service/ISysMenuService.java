package com.river.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.river.api.dto.system.SysMenuDto;
import com.river.api.dto.system.SysUserDto;
import com.river.api.entity.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     *  用户菜单权限获取
     * @param param
     * @return
     */
    public List<SysMenu> findSysUserMenu(SysUserDto param);

    /**
     * 获取用户菜单
     * @param param
     * @return
     */
    public SysMenuDto getUserMenu(SysMenuDto param);



}
