package com.river.site.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.dto.site.SiteConnentMainDto;
import com.river.api.entity.site.SiteConnent;
import com.river.api.entity.site.SiteConnentMain;
import com.river.api.entity.system.SysMenu;
import com.river.api.feign.RemoteAllService;
import com.river.common.core.component.CacheCommonManage;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.ConvertUtils;
import com.river.common.core.util.Result;
import com.river.common.mybatis.util.MyBatisUtils;
import com.river.site.service.ISiteConnentMainService;
import com.river.site.service.ISiteConnentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 站点内容主表 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-17
 */
@RestController
@RequestMapping("/siteConnentMain")
@Slf4j
@Api(value = "siteConnentMain",tags = "站点内容管理")
public class SiteConnentMainController {

    @Autowired
    private ISiteConnentMainService siteConnentMainService;

    @Autowired
    private ISiteConnentService siteConnentService;

    @Autowired
    private RemoteAllService remoteAllService;

    @Autowired
    private CacheCommonManage cacheCommonManage;
    
    @PostMapping(value = "/query")
    @ApiOperation(value = "query", httpMethod = "POST", response = Result.class, notes = "查询")
    @PreAuthorize("@pms.hasPermission('sit_connent_query')")
    public Result query(@RequestBody SiteConnentMainDto param){

        log.info("查询站点内容列表开始");
        try {

            QueryWrapper<SiteConnentMain> query = Wrappers.<SiteConnentMain>query();

            MyBatisUtils.like(new String[]{"title","notes"},param,query,false);

            if(StringUtils.isNotBlank(param.getCategory())){
                query.eq("category",param.getCategory());
            }
            if(StringUtils.isNotBlank(param.getTargetId())){
                query.eq("target_id",param.getTargetId());
            }
            if(StringUtils.isNotBlank(param.getStatus())){
                query.eq("status",param.getStatus());
            }

            List<OrderItem> orders = param.getPage().getOrders();
            if(orders==null || orders.size()<=0){
                orders.addAll(OrderItem.descs("update_date","category","sort_key"));
            }else if(StringUtils.equals("toping",orders.get(0).getColumn())){
                String order = orders.get(0).isAsc()?"asc":"desc";
                //空值排序在后面
                query.last(" ORDER BY IF(ISNULL(toping),1,0) , IF((toping = ''),1,0) ,toping "+ order);
                orders.remove(0);
            }

            Page page = siteConnentMainService.page(param.getPage(), query);


            return Result.ok(page);

        } catch(Exception e) {
            log.error("查询站点内容列表异常",e);
            e.printStackTrace();
            return Result.failed("查询站点内容列表异常");
        }

    }

    @RequestMapping(value = "/getTargetMenus")
    @ApiOperation(value = "getTargetMenus", httpMethod = "GET", response = Result.class, notes = "获取内容所属菜单集合")
    public Result getTargetMenus(SysMenu param){

        log.info("获取内容所属菜单集合开始");
        try {
            //获取所属菜单集合
            Map<String, String> menus = null;
            Result r = remoteAllService.loadTopMenus(param);
            if(r.isSuccess()){
                List data = (List) r.getData();
                List<SysMenu> rows = ConvertUtils.beansInListCopy(data, SysMenu.class);
                menus = rows.stream().collect(Collectors.toMap(SysMenu::getSid, SysMenu::getDisplayName));
            }

            return Result.result(menus,menus!=null);

        } catch(Exception e) {
            log.error("获取内容所属菜单集合异常",e);
            e.printStackTrace();
            return  Result.failed("获取内容所属菜单集合异常");
        }

    }

    @RequestMapping(value = "/detail")
    @ApiOperation(value = "detail", httpMethod = "GET", response = Result.class, notes = "详情")
    @PreAuthorize("@pms.hasPermission('site_connent_detail')")
    public Result detail(SiteConnentMainDto param){

        log.info("查询站点内容详情开始");
        SiteConnentMainDto result = param;
        try {

            if(StringUtils.isNotBlank(param.getSid())){
                SiteConnentMainDto row = siteConnentMainService.getDetail(param.getSid());
                result = row==null?result:row;
            }else{
                result.setStatus("1");
                result.setBodys("");
            }

        } catch(Exception e) {
            log.error("查询站点内容详情异常",e);
            e.printStackTrace();
            return  Result.failed("查询站点内容详情异常");
        }

        log.info("查询站点内容详情结束");

        return  Result.ok(result);
    }

    @PostMapping(value = "/deletes")
    @ApiOperation(value = "deletes", httpMethod = "POST", response = Result.class, notes = "删除")
    @PreAuthorize("@pms.hasPermission('site_connent_delete')")
    public Result delete(@RequestBody List<SiteConnentMainDto> rows){

        log.info("删除站点内容开始");
        try {
            List<String> imgIds =  new ArrayList<String>();
            List<String> ids = new ArrayList<String>();
            rows.forEach(row->{
                ids.add(row.getSid());
                if(StringUtils.isNotBlank(row.getImg())){
                    imgIds.add(row.getSid());
                }
            });
            boolean b = siteConnentService.remove(Wrappers.<SiteConnent>lambdaQuery().in(SiteConnent::getTargetId, ids));
            b = siteConnentMainService.removeByIds(ids);
            //附件删除
            if(imgIds.size()>0 && b){
                remoteAllService.deleteByTargetIds(imgIds);
            }

            if(b){
                cacheCommonManage.cleanCache(CacheConstants.PREFIX_SITE,"site-index-data");
            }

            return Result.result(b);

        } catch(Exception e) {
            log.error("删除站点内容异常",e);
            e.printStackTrace();
            return Result.failed("删除站点内容异常");
        }

    }


    @PostMapping(value = "/modify")
    @ApiOperation(value = "modify", httpMethod = "POST", response = Result.class, notes = "新增/修改")
    @PreAuthorize("@pms.hasPermission('site_connent_modify')")
    public Result modify(@RequestBody SiteConnentMainDto param){

        log.info("新增/修改站点内容开始");
        try {

            boolean b = siteConnentMainService.saveOrUpdate(param);

            if(b && !StringUtils.equals(param.getCategory(),"2")){
                SiteConnent siteConnent = new SiteConnent();
                siteConnent.setSid(param.getConnectId());
                siteConnent.setTargetId(param.getSid());
                siteConnent.setBodys(param.getBodys());
                siteConnent.setTargetId(param.getSid());
                b = siteConnentService.saveOrUpdate(siteConnent);
            }

            if(b){
                cacheCommonManage.cleanCache(CacheConstants.PREFIX_SITE,"site-index-data");
            }

            return Result.result(param,b);

        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }
    }

}

