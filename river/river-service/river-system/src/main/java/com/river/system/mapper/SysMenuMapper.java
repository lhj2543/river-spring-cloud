package com.river.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.river.api.dto.system.SysUserDto;
import com.river.api.entity.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 获取用户菜单资源
     * @param param
     * @return
     */
    List<SysMenu> findSysUserMenu(SysUserDto param);

}
