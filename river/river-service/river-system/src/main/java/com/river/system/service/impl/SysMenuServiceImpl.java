package com.river.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.dto.system.SysMenuDto;
import com.river.api.dto.system.SysUserDto;
import com.river.api.dto.system.SysUserRoleDto;
import com.river.api.entity.system.SysMenu;
import com.river.api.entity.system.SysRole;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.ConvertUtils;
import com.river.common.core.util.Result;
import com.river.common.security.util.SecurityUtils;
import com.river.system.mapper.SysMenuMapper;
import com.river.system.service.ISysMenuService;
import com.river.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private ISysRoleService sysRoleService;

    /**
     *  用户菜单权限获取
     * @param param
     * @return
     */
    @Override
    public List<SysMenu>  findSysUserMenu(SysUserDto param) {

        List<SysMenu> sysUserMenu = baseMapper.findSysUserMenu(param);

        return sysUserMenu;
    }


    @Cacheable(value = CacheConstants.PREFIX_USER_MENU,key = "#param.userId",unless = "#result==null")
    @Override
    public SysMenuDto getUserMenu(SysMenuDto param) {

        Set<SysUserRoleDto> userRoles = SecurityUtils.getUserRoles();

        param.setParentId("-1");
        param.setCategory("manage");

        SysMenu sysMenu = this.getOne(Wrappers.query(param));

        if(sysMenu != null){

            //角色未空时，给基本角色
            if(userRoles.isEmpty()){
                LambdaQueryWrapper<SysRole> eq = Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, "base");
                SysRole role = sysRoleService.getOne(eq);

                SysUserRoleDto userRole = new SysUserRoleDto();
                userRole.setRoleId(role.getSid());
                userRoles.add(userRole);

            }

            SysUserDto user = new SysUserDto();
            user.setUserRoles(userRoles);
            user.setCategory("manage");
            user.setAdministrator(SecurityUtils.isAdministrator());

            //获取用户资源权限
            List<SysMenu> sysUserMenu = this.findSysUserMenu(user);

            SysMenuDto sysMenuDto = ConvertUtils.beanCopy(sysMenu,SysMenuDto.class);
            sysMenuDto.setChildren(ConvertUtils.beansInListCopy(sysUserMenu,SysMenuDto.class));
            sysMenuDto.setUserId(param.getUserId());

            return  sysMenuDto;

        }

        return null;
    }


}
