package com.river.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.api.entity.system.SysItem;
import com.river.api.entity.system.SysItemCategory;
import com.river.common.core.component.CacheCommonManage;
import com.river.common.core.util.Result;
import com.river.system.service.ISysItemCategoryService;
import com.river.system.service.ISysItemService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 字典类别(多语言)表 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/sysItemCategory")
@Slf4j
@Api(value = "sysItemCategory",tags="字典分类管理")
public class SysItemCategoryController {

    @Autowired
    private ISysItemCategoryService sysItemCategoryService;

    @Autowired
    private ISysItemService sysItemService;

    @Autowired
    private CacheCommonManage cacheCommonManage;


    @RequestMapping(value = "/query")
    @PreAuthorize("@pms.hasPermission('sys_item_query')")
    public Result query(SysItemCategory param){

        log.info("查询字典分类列表开始");
        SysItemCategory result = param;
        try {

            LambdaQueryWrapper<SysItemCategory> query = Wrappers.<SysItemCategory>lambdaQuery().eq(SysItemCategory::getParentId, "-1")
                    .orderByAsc(SysItemCategory::getSortKey);

            List<SysItemCategory> rootMenus =  sysItemCategoryService.list(query);

            List<SysItemCategory> rows = sysItemCategoryService.list(Wrappers.<SysItemCategory>query().ne("parent_id","-1").orderByAsc("sort_key"));

            for (SysItemCategory m:rootMenus){
                m.setExpand(param.isExpand());
                this.recurrence(m,rows,param);
            }

            return Result.ok(rootMenus);

        } catch(Exception e) {
            log.error("查询字典分类列表异常",e);
            e.printStackTrace();
            return Result.failed("查询字典分类异常");
        }
    }

    /**
     * 递归字典分类
     * @param menu
     * @param menus
     * @param params
     */
    public void recurrence(SysItemCategory menu,List<SysItemCategory> menus,SysItemCategory params){

        for(SysItemCategory m:menus){

            if(StringUtils.equals(m.getParentId(),menu.getSid())){
                m.setExpand(params.isExpand());
                menu.getChildren().add(m);
                this.recurrence(m,menus,params);
            }

        }

    }

    @PostMapping(value = "/deletes")
    @PreAuthorize("@pms.hasPermission('sys_item_delete')")
    public Result delete(@RequestBody SysItemCategory param){

        log.info("删除字典分类开始");
        SysItemCategory result = new SysItemCategory();
        try {

            if(param.getChildren().size()>0){
                this.delChildrenMenu(param.getChildren());
            }

            boolean b = deleteItem(param.getSid());

            //清除缓存
            if(b){
                cacheCommonManage.cleanSysItem(null,true);
            }

            return Result.result(b);

        } catch(Exception e) {
            log.error("删除字典分类异常",e);
            e.printStackTrace();
            return Result.failed("删除字典分类异常");
        }

    }

    /**
     * 递归删除字典分类
     * @param rows
     */
    public void delChildrenMenu(List<SysItemCategory> rows){

        for(SysItemCategory m:rows){
            deleteItem(m.getSid());
            this.delChildrenMenu(m.getChildren());
        }

    }

    public  boolean deleteItem(String id){
        sysItemService.remove(Wrappers.<SysItem>lambdaQuery().eq(SysItem::getCategoryId, id));
        return  sysItemCategoryService.removeById(id);
    }


    @PostMapping(value = "/save")
    @PreAuthorize("@pms.hasPermission('sys_item_save')")
    public Result save(@RequestBody SysItemCategory param){

        log.info("新增/修改字典分类开始 ");
        SysItemCategory result = param;

        try {

            boolean b =  sysItemCategoryService.saveOrUpdate(param);

            if(b){
                List<SysItem> sysItems = param.getSysItems();
                if(sysItems!=null && sysItems.size()>0){
                    sysItems.forEach(row->{
                        row.setCategoryId(param.getSid());
                    });
                    b = sysItemService.saveOrUpdateBatch(sysItems);
                }
            }

            //清除缓存
            if(b){
                cacheCommonManage.cleanSysItem(param.getCategoryName(),false);
            }

            return Result.result(param,b);

        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }

    }
    

}

