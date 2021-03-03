package com.river.site.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.entity.site.NotebookCategory;
import com.river.api.entity.site.NotebookMain;
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
import java.util.stream.Collectors;

/**
 * 笔记管理
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/noteBook")
@Slf4j
@Api(value = "noteBook",tags="笔记分类管理")
public class NotebookController {

    @Autowired
    private INotebookMainService notebookMainService;
    @Autowired
    private INotebookCategoryService notebookCategoryService;

    @RequestMapping(value = "/query")
    public Result query(@RequestBody NotebookMain param){

        log.info("查询笔记列表开始");
        NotebookMain result = param;
        try {

            //LambdaQueryWrapper<NotebookMain> query = Wrappers.<NotebookMain>lambdaQuery(param).orderByDesc(NotebookMain::getCreatedDate);
            QueryWrapper<NotebookMain> query = Wrappers.<NotebookMain>query()
                    .select("sid","category_id","title","LEFT(fnStripTags(LEFT(bodys,500)),200)  as bodys","notes","created_by","created_date","update_by","update_date")
                    .orderByDesc("created_date");

            if(StringUtils.isNotBlank(param.getSearchValue())){
                query.and(wrapper->wrapper.like("title",param.getSearchValue()).or().like("bodys",param.getSearchValue()));
            }

            if(StringUtils.isNotBlank(param.getCategoryId())){
                NotebookCategory nbc = new NotebookCategory();
                nbc.setSid(param.getCategoryId());
                List<String> treeAllIds = notebookCategoryService.getNotebookCategoryTreeAllIds(nbc);
                if(treeAllIds.size()>0){
                    query.in("category_id",treeAllIds);
                }
            }

            
            Page page = notebookMainService.page(param.getPage(), query);

            return Result.ok(page);

        } catch(Exception e) {
            log.error("查询笔记列表异常",e);
            e.printStackTrace();
            return Result.failed("查询笔记列表异常");
        }
    }

    @RequestMapping(value = "/detail")
    public Result detail(NotebookMain param){

        log.info("查询笔记详情开始");
        NotebookMain result = param;
        try {

            if(StringUtils.isNotBlank(param.getSid())){
                NotebookMain notebookMain = notebookMainService.getById(param.getSid());
                result = notebookMain==null?result:notebookMain;
            }else{
                result.setBodys("");
            }

        } catch(Exception e) {
            log.error("查询笔记详情异常",e);
            e.printStackTrace();
            return  Result.failed("查询笔记详情异常");
        }

        log.info("查询笔记详情结束");

        return  Result.ok(result);
    }

    @PostMapping(value = "/deletes")
    public Result delete(@RequestBody NotebookMain param){

        log.info("删除笔记开始");
        NotebookMain result = new NotebookMain();
        try {
            List<NotebookMain> rows = param.getRows();
            List<String> ids = rows.stream().map(NotebookMain::getSid).collect(Collectors.toList());

            boolean b = notebookMainService.removeByIds(ids);
            return Result.result(b);


        } catch(Exception e) {
            log.error("删除笔记异常",e);
            e.printStackTrace();
            return Result.failed("删除笔记异常");
        }

    }


    @PostMapping(value = "/modify")
    public Result modify(@RequestBody NotebookMain param){

        log.info("新增/修改笔记开始");
        try {

            boolean b = notebookMainService.saveOrUpdate(param);
            return Result.result(param,b);

        } catch(Exception e) {
            log.error("保存异常",e);
            e.printStackTrace();
            return Result.failed("保存异常");
        }

    }

}
