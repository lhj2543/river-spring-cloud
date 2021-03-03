package com.river.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.api.dto.system.SysMenuDto;
import com.river.api.entity.system.SysMenu;
import com.river.common.core.component.CacheCommonManage;
import com.river.common.core.util.ConvertUtils;
import com.river.common.core.util.Result;
import com.river.common.security.util.SecurityUtils;
import com.river.system.service.ISysMenuService;
import com.river.system.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/sysMenu")
@Slf4j
@Api(value = "sysMenu",tags="菜单管理")
public class SysMenuController {

    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private CacheCommonManage cacheCommonManage;


    /**
     * 获取用户菜单
     * @param param
     * @return
     */
    @PostMapping("/getUserMenu")
    @ApiOperation(value = "获取用户菜单", httpMethod = "POST", response = Result.class, notes = "")
    public Result getUserMenu(@RequestBody SysMenuDto param){

        try {

            String userCd = SecurityUtils.getUser().getUsername();
            log.info("获取用户菜单开始，userName="+userCd);

            param.setUserId(userCd);
            SysMenuDto userMenu = sysMenuService.getUserMenu(param);
            return Result.result(userMenu,userMenu!=null);

        } catch(Exception e) {
            log.error("获取用户菜单异常",e.getMessage());
            e.printStackTrace();
            return Result.failed(e.getMessage(),"获取用户菜单异常");
        }

    }

    @RequestMapping(value = "/query")
    @PreAuthorize("@pms.hasPermission('sys_menu_query')")
    public Result query(SysMenuDto param){

        log.info("查询菜单列表开始");

        try {

            LambdaQueryWrapper<SysMenu> rootQuery = Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getParentId, "-1").orderByAsc(SysMenu::getSortKey);
            List<SysMenu> rm = sysMenuService.list(rootQuery);
            List<SysMenuDto> rootMenus = ConvertUtils.beansInListCopy(rm, SysMenuDto.class);

            QueryWrapper query = Wrappers.query().ne("parent_id","-1").orderByAsc("sort_key");
            List<SysMenu> r = sysMenuService.list(query);
            List<SysMenuDto> rows = ConvertUtils.beansInListCopy(r, SysMenuDto.class);


            for (SysMenuDto m:rootMenus){
                m.setExpand(param.isExpand());
                this.recurrenceMenu(m,rows,param,1);
            }

           return Result.ok(rootMenus);

        } catch(Exception e) {
            log.error("查询菜单异常",e);
            e.printStackTrace();
            return Result.failed("查询菜单异常");
        }

    }

    /**
     * 递归菜单
     * @param menu
     * @param menus
     * @param params
     */
    public void recurrenceMenu(SysMenuDto menu,List<SysMenuDto> menus,SysMenuDto params,int level){
        level ++;
        for(SysMenuDto m:menus){

            if(StringUtils.equals(m.getParentId(),menu.getSid())){
                if(level<=2){
                    m.setExpand(params.isExpand());
                }
                menu.getChildren().add(m);
                this.recurrenceMenu(m,menus,params,level);
            }

        }

    }

    @RequestMapping(value = "/detail")
    @PreAuthorize("@pms.hasPermission('sys_menu_detail')")
    public Result detail(SysMenuDto param){

        log.info("查询菜单详情开始");
        SysMenuDto result = new SysMenuDto();
        try {

            if(StringUtils.isNotBlank(param.getSid())){
                SysMenu menu = sysMenuService.getOne(Wrappers.query(param));
                result = menu == null ? result: (SysMenuDto) menu;
            }else{
                //新增时初始化数据
                result.setStatus("1");
            }

        } catch(Exception e) {
            log.error("查询菜单详情异常",e);
            e.printStackTrace();
        }
        return  Result.ok(result);

    }

    @PostMapping(value = "/deletes")
    @PreAuthorize("@pms.hasPermission('sys_menu_delete')")
    public Result delete(@RequestBody SysMenuDto param){

        log.info("删除菜单开始");
        SysMenu result = new SysMenu();
        try {

            if(param.getChildren().size()>0){
                this.delChildrenMenu(param.getChildren());
            }

            boolean b = sysMenuService.removeById(param.getSid());

            //清除菜单相关缓存
            cacheCommonManage.cleanMenuCache(b);

            return Result.result(b);

        } catch(Exception e) {
            log.error("删除菜单异常",e);
            e.printStackTrace();
            return  Result.failed("删除菜单异常");
        }

    }

    /**
     * 递归删除菜单
     * @param menus
     */
    public void delChildrenMenu(List<SysMenuDto> menus){

        for(SysMenuDto m:menus){
            sysMenuService.removeById(m.getSid());
            this.delChildrenMenu(m.getChildren());
        }

    }


    @PostMapping(value = "/save")
    @PreAuthorize("@pms.hasPermission('sys_menu_save')")
    public Result save(@RequestBody SysMenuDto param){

        log.info("新增/修改菜单开始 ");
        try {

            if(StringUtils.isNotBlank(param.getSid())){
                //判断是否纯在子类
                List<SysMenu> childrens = sysMenuService.list(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getParentId, param.getSid()));
                if(childrens!=null && childrens.size()>0){
                    param.setType("3");
                }
            }

            boolean b = sysMenuService.saveOrUpdate(param);

            if(!StringUtils.equals("-1",param.getParentId())){
                //更新父节点类型为3=父节点
                LambdaUpdateWrapper<SysMenu> up = Wrappers.<SysMenu>lambdaUpdate().set(SysMenu::getType, "3").eq(SysMenu::getSid, param.getParentId()).ne(SysMenu::getType, "3");
                sysMenuService.update(up);
            }

            //清除菜单相关缓存
            cacheCommonManage.cleanMenuCache(b);

            return  Result.result(param,b);

        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }

    }

    @GetMapping(value = "/getMaxSortKey")
    @PreAuthorize("@pms.hasPermission('sys_menu_save')")
    public Result getMaxSortKey(String sid){

        log.info("获取菜单最大排序开始 ");
        try {
            if(StringUtils.isNotBlank(sid)){
                QueryWrapper<SysMenu> query = Wrappers.<SysMenu>query().select("max(sort_key) as sortKey").eq("parent_id", sid);
                SysMenu menu = sysMenuService.getOne(query);
                if(menu==null){
                    return  Result.ok(0L);
                }
                return  Result.ok(menu.getSortKey());
            }

            return  Result.failed("菜单id为空");
        } catch(Exception e) {
            log.error("获取菜单最大排序异常",e);
            e.printStackTrace();
            return Result.failed("获取菜单最大排序异常");
        }

    }


}

