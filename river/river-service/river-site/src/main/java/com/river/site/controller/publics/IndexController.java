package com.river.site.controller.publics;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.dto.site.IndexDto;
import com.river.api.dto.system.SysUserDto;
import com.river.api.entity.site.NotebookCategory;
import com.river.api.entity.site.SiteAttrs;
import com.river.api.entity.site.SiteConnentMain;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.Result;
import com.river.site.service.INotebookCategoryService;
import com.river.site.service.ISiteAttrsService;
import com.river.site.service.ISiteConnentMainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页 前端控制器
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/public/index")
@Slf4j
@Api(value = "index",tags="站点首页管理")
public class IndexController {

    @Autowired
    private INotebookCategoryService notebookCategoryService;

    @Autowired
    private ISiteAttrsService siteAttrsService;

    @Autowired
    private ISiteConnentMainService siteConnentMainService;

    /**
     * 首页数据初始化
     * @param param
     * @return
     */
    @RequestMapping("/initData")
    @ApiOperation(value = "initData", httpMethod = "GET", response = Result.class, notes = "站点首页数据初始化")
    //key 字符串记得加''号
    @Cacheable(value = CacheConstants.PREFIX_SITE,key = "'site-index-data'",unless = "#result.success==false || #result.data==null")
    public Result initData(SiteAttrs param){

        log.info("加载首页数据开始");
        IndexDto result = new IndexDto();

        try {
            List<SiteAttrs> sa = siteAttrsService.list(Wrappers.query(param));
            result.setSiteAttrs(sa);

            NotebookCategory notebookCategory = new NotebookCategory();
            notebookCategory.setCategoryName("skill");
            NotebookCategory category = notebookCategoryService.getOne(Wrappers.query(notebookCategory));

            notebookCategory.setParentId(category.getSid());
            notebookCategory.setCategoryName(null);
            notebookCategory.setStatus("1");

            Page page =  new Page();
            page.setSize(8);

            page  = notebookCategoryService.page(page, Wrappers.<NotebookCategory>lambdaQuery(notebookCategory).orderByAsc(NotebookCategory::getSortKey));
            result.getReserve().put("notebookCategory",page.getRecords());


            LambdaQueryWrapper<SiteConnentMain> connetQuery = Wrappers.<SiteConnentMain>lambdaQuery()
                    .eq(SiteConnentMain::getStatus, "1");

            //空值排序在后面
            connetQuery.last(" ORDER BY IF(ISNULL(toping),1,0) , IF((toping = ''),1,0) ,toping asc,sort_key");

            Page projectPage =  new Page();
            projectPage.setSize(3);
            projectPage = siteConnentMainService.page(projectPage, connetQuery.clone());
            //.eq(SiteConnentMain::getCategory,"project")
            result.getReserve().put("projects",projectPage.getRecords());

            Page toolPage =  new Page();
            toolPage.setSize(4);

            projectPage = siteConnentMainService.page(toolPage, connetQuery.clone().eq(SiteConnentMain::getCategory,"tool"));
            result.getReserve().put("tools",toolPage.getRecords());

        } catch(Exception e) {
            log.error("加载首页数据异常",e);
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }

        log.info("加载首页数据结束");

        return  Result.ok(result);
    }



    /**
     * test
     * @param param
     * @return
     */
    @RequestMapping("/test")
    public Result test(SysUserDto param){
        //int a = 1/0;
        return Result.ok("测试成功！");
    }



}

