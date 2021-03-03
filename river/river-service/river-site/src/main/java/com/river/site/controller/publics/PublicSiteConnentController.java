package com.river.site.controller.publics;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.dto.site.SiteConnentMainDto;
import com.river.api.entity.site.NotebookMain;
import com.river.api.entity.site.SiteConnent;
import com.river.api.entity.site.SiteConnentMain;
import com.river.common.core.util.ConvertUtils;
import com.river.common.core.util.Result;
import com.river.site.service.ISiteConnentMainService;
import com.river.site.service.ISiteConnentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 站点内容 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-17
 */
@RestController
@RequestMapping("/public/siteConnent")
@Slf4j
@Api(value = "siteConnent",tags="站点内容")
public class PublicSiteConnentController {
    
    @Autowired
    private ISiteConnentMainService siteConnentMainService;

    @Autowired
    private ISiteConnentService siteConnentService;

    @PostMapping(value = "/list")
    @ApiOperation(value = "list", httpMethod = "POST", response = Result.class, notes = "站点内容列表")
    public Result list(@RequestBody SiteConnentMainDto param){

        log.info("查询站点内容列表开始");
        try {

            /*Thread.sleep(1000*2);*/
            LambdaQueryWrapper<SiteConnentMain> query = Wrappers.<SiteConnentMain>lambdaQuery(param);

            if(StringUtils.isNotBlank(param.getSearchValue())){
                query.and(Wrappers->Wrappers.like(SiteConnentMain::getTitle,param.getSearchValue())
                        .or().like(SiteConnentMain::getNotes,param.getSearchValue())
                        .or().like(SiteConnentMain::getCategory,param.getSearchValue())
                );
            }

            List<OrderItem> orders = param.getPage().getOrders();
            if(orders==null || orders.size()<=0){
                //空值排序在后面
                query.last(" ORDER BY IF(ISNULL(toping),1,0) , IF((toping = ''),1,0) ,toping asc,sort_key,update_date");
            }else if(StringUtils.equals("toping",orders.get(0).getColumn())){
                //空值排序在后面
                query.last(" ORDER BY IF(ISNULL(toping),1,0) , IF((toping = ''),1,0) ,toping asc");
                orders.remove(0);
            }



            Page page = siteConnentMainService.page(param.getPage(),query);

            return Result.ok(page);

        } catch(Exception e) {
            log.error("查询站点内容列表异常",e);
            e.printStackTrace();
            return Result.failed("查询站点内容列表异常");
        }
    }

    @GetMapping(value = "/detail")
    @ApiOperation(value = "detail", httpMethod = "GET", response = Result.class, notes = "站点内容详情")
    public Result detail(SiteConnentMainDto param){

        log.info("查询站点内容详情开始");
        try {

            SiteConnentMainDto row = siteConnentMainService.getDetail(param.getSid());

            return Result.ok(row);

        } catch(Exception e) {
            log.error("查询站点内容详情异常",e);
            e.printStackTrace();
            return Result.failed("查询站点内容详情异常");
        }
    }

}

