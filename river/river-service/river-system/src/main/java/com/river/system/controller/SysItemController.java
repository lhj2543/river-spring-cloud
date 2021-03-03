package com.river.system.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.api.dto.system.SysItemDto;
import com.river.api.entity.system.SysItem;
import com.river.api.entity.system.SysItemCategory;
import com.river.common.core.component.CacheCommonManage;
import com.river.common.core.util.JsonSupport;
import com.river.common.core.util.Result;
import com.river.system.service.ISysItemCategoryService;
import com.river.system.service.ISysItemService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/sysItem")
@Slf4j
@Api(value = "sysItem",tags="字典管理")
public class SysItemController {


    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JsonSupport jsonSupport;

    @Autowired
    private ISysItemService sysItemService;

    @Autowired
    private ISysItemCategoryService sysItemCategoryService;

    @Autowired
    private CacheCommonManage cacheCommonManage;

    /**
     * 加载字典列表
     * @param param
     * @return
     */
    @PostMapping(value = "/getSysItemByCategoryName")
    public Result getSysItemByCategoryName(@RequestBody SysItemDto param){

        logger.info("查询字典开始");
        SysItemDto result = param;
        try {

            Map<String, Object> items = sysItemService.findSysItemByCategoryName(param);
            result.setItemMap(items);

            return Result.ok(result);

        } catch(Exception e) {
            logger.error("查询字典详情异常",e);
            e.printStackTrace();
            return Result.failed("查询字典详情异常");
        }

    }

    @RequestMapping(value = "/query")
    public Result query(SysItem param){

        logger.info("查询字典列表开始");
        try {

            if(StringUtils.isBlank(param.getCategoryId())){
                return Result.failed("字典分类ID不能为空！");
            }

            List<SysItem> rows = sysItemService.list(Wrappers.query(param).orderByAsc("sort_key"));

            return  Result.ok(rows);

        } catch(Exception e) {
            logger.error("查询字典列表异常",e);
            e.printStackTrace();
            return  Result.failed("查询字典列表异常");
        }

    }

    @PostMapping(value = "/deletes")
    @PreAuthorize("@pms.hasPermission('sys_item_delete')")
    public Result delete(@RequestBody SysItem param){

        logger.info("删除字典开始");
        logger.info(jsonSupport.toJson(param));
        try {
            boolean b = sysItemService.removeById(param.getSid());
            //清除缓存
            if(b){
                SysItemCategory category = sysItemCategoryService.getById(param.getCategoryId());
                cacheCommonManage.cleanSysItem(category.getCategoryName(),false);
            }
            return  Result.ok(b,"删除字典成功");

        } catch(Exception e) {
            logger.error("删除字典异常",e);
            e.printStackTrace();
            return Result.failed("删除字典成功");
        }

    }


    @PostMapping(value = "/save")
    @PreAuthorize("@pms.hasPermission('sys_item_save')")
    public Result modify(@RequestBody SysItem param){
        String itemKey = param.getItemKey();

        logger.info("新增/修改字典开始 itemKey="+itemKey);
        SysItem result = param;
        try {

            if(StringUtils.isBlank(param.getSid())){

                if(this.isExist(itemKey)){
                    return Result.failed(itemKey+"该字典key已存在！");
                }

            }

            boolean b = sysItemService.saveOrUpdate(param);

            return Result.ok(b,"保存成功！");

        } catch(Exception e) {
            logger.error("保存异常",e);
            e.printStackTrace();
            return  Result.failed("保存异常");
        }

    }

    public boolean isExist(String key){

        logger.info("验证字典key是否已存在开始");
        try {
            SysItem result = sysItemService.getOne(Wrappers.<SysItem>lambdaQuery().eq(SysItem::getItemKey,key));
            if(result!=null && StringUtils.isNotBlank(result.getSid())){
                return true;
            }
        } catch(Exception e) {
            logger.error("验证字典key是否已存在异常",e);
            e.printStackTrace();
        }

        logger.info("验证字典key是否已存在结束");
        return  false;
    }



}

