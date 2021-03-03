package com.river.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.api.dto.system.SysMenuDto;
import com.river.api.entity.system.SysMenu;
import com.river.api.entity.system.SysRole;
import com.river.api.entity.system.SysRoleMenu;
import com.river.common.core.component.CacheCommonManage;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.ConvertUtils;
import com.river.common.core.util.Result;
import com.river.system.service.ISysMenuService;
import com.river.system.service.ISysRoleMenuService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色菜单关联表 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/sysRoleMenu")
@Slf4j
@Api(value = "sysRoleMenu",tags="角色授权")
public class SysRoleMenuController {


    @Autowired
    private ISysRoleMenuService sysRoleMenuService;

    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private CacheCommonManage cacheCommonManage;

    @RequestMapping(value = "/query")
    @PreAuthorize("@pms.hasPermission('sys_role_menu_query')")
    public Result query(SysMenuDto param){

        log.info("查询角色菜单授权列表开始");
        SysMenu result = param;
        try {
            SysMenu sysMenu = new SysMenu();
            sysMenu.setParentId("-1");
            sysMenu.setMenuName("manage");
            List<SysMenu> lrms = sysMenuService.list(Wrappers.query(sysMenu));
            List<SysMenuDto> rootMenus = ConvertUtils.beansInListCopy(lrms, SysMenuDto.class);

            QueryWrapper query = Wrappers.query().ne("parent_id","-1")
                    .orderByAsc("sort_key");
            List<SysMenu> r = sysMenuService.list(query);
            List<SysMenuDto> rows = ConvertUtils.beansInListCopy(r, SysMenuDto.class);

            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(param.getRoleId());
            List<SysRoleMenu> rms  = sysRoleMenuService.list(Wrappers.query(rm));

            for (SysMenuDto md:rootMenus){
                md.setExpand(param.isExpand());
                md.setRoleId(param.getRoleId());
                this.recurrenceMenu(md,rows,param,rms);
            }

           return  Result.ok(rootMenus);

        } catch(Exception e) {
            log.error("查询角色菜单授权列表异常",e);
            e.printStackTrace();
            return Result.failed("查询角色菜单授权列表异常!");
        }

    }

    /**
     * 递归角色菜单授权
     * @param menu
     * @param menus
     * @param params
     * @param rms
     */
    public void recurrenceMenu(SysMenuDto menu,List<SysMenuDto> menus,SysMenuDto params,List<SysRoleMenu> rms){

        for(SysMenuDto m:menus){

            if(StringUtils.equals(m.getParentId(),menu.getSid())){
                m.setExpand(params.isExpand());
                //判断该角色是否有该菜单
                for (SysRoleMenu rm:rms){
                    if(StringUtils.equals(rm.getMenuId(),m.getSid()) && !StringUtils.equals(m.getType(),"3")){
                        m.setChecked(true);
                        break;
                    }
                }
                m.setRoleId(params.getRoleId());
                menu.getChildren().add(m);
                this.recurrenceMenu(m,menus,params,rms);
            }

        }

    }


    @PostMapping(value = "/save")
    @PreAuthorize("@pms.hasPermission('sys_role_menu_save')")
    public Result save(@RequestBody SysMenuDto param){

        log.info("新增/修改角色菜单授权开始 ");
        SysMenu result = param;
        try {

            sysRoleMenuService.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getRoleId, param.getRoleId()));

            List<SysMenu> srms = param.getRows();

            if(srms!=null && srms.size()>0){
                List<SysRoleMenu> rows = new ArrayList<SysRoleMenu>();

                for (SysMenu m:srms){
                    SysRoleMenu rm = new SysRoleMenu();
                    rm.setRoleId(param.getRoleId());
                    rm.setMenuId(m.getSid());
                    rm.setStatus("1");
                    rows.add(rm);
                }

                boolean b = sysRoleMenuService.saveBatch(rows);
                if(b){
                    //清除用户菜单缓存
                    cacheCommonManage.cleanCache(CacheConstants.PREFIX_USER_MENU,null);
                }
                return Result.result(b);
            }

            return Result.ok();

        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }

    }

}

