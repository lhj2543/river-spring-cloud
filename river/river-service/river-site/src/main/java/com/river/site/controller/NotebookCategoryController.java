package com.river.site.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.api.entity.site.NotebookCategory;
import com.river.common.core.component.CacheCommonManage;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.Result;
import com.river.site.service.INotebookCategoryService;
import com.river.site.service.INotebookMainService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 笔记分类 前端控制器
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/noteBookCategory")
@Slf4j
@Api(value = "noteBookCategory",tags="笔记分类管理")
public class NotebookCategoryController {

    @Autowired
    private INotebookCategoryService notebookCategoryService;

    @Autowired
    private INotebookMainService notebookMainService;

    @Autowired
    private CacheCommonManage cacheCommonManage;

    @RequestMapping(value = "/query")
    public Result query(NotebookCategory param){

        log.info("查询笔记分类列表开始");
        try {

            List<NotebookCategory> roots = notebookCategoryService.getNotebookCategoryTree(param);
            return Result.ok(roots);

        } catch(Exception e) {
            log.error("查询笔记分类异常",e);
            e.printStackTrace();
            return Result.failed("查询笔记分类异常");
        }
    }


    @PostMapping(value = "/deletes")
    public Result delete(@RequestBody NotebookCategory param){

        log.info("删除笔记分类开始");
        NotebookCategory result = new NotebookCategory();
        try {

            if(param.getChildren().size()>0){
                this.delChildrenNode(param.getChildren());
            }

            boolean b = notebookCategoryService.removeById(param.getSid());

            if(b){
                cacheCommonManage.cleanCache(CacheConstants.PREFIX_SITE_NOTEBOOKCATEGORY,null);
            }

            return Result.result(b);

        } catch(Exception e) {
            log.error("删除笔记分类异常",e);
            e.printStackTrace();
            return Result.failed("删除笔记分类异常");
        }
    }

    /**
     * 递归删除笔记分类
     * @param rows
     */
    public void delChildrenNode(List<NotebookCategory> rows){

        for(NotebookCategory m:rows){
            notebookCategoryService.removeById(m.getSid());
            this.delChildrenNode(m.getChildren());
        }

    }


    @PostMapping(value = "/save")
    public Result save(@RequestBody NotebookCategory param){

        log.info("新增/修改笔记分类开始 ");
        try {

            boolean b = notebookCategoryService.saveOrUpdate(param);
            if(b){
                cacheCommonManage.cleanCache(CacheConstants.PREFIX_SITE_NOTEBOOKCATEGORY,null);
            }
            return Result.result(param,b);

        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }

    }


}

