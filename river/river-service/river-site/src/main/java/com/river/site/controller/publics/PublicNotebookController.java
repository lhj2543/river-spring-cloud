package com.river.site.controller.publics;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.dto.site.IndexDto;
import com.river.api.entity.site.NotebookCategory;
import com.river.api.entity.site.NotebookMain;
import com.river.api.entity.site.SiteAttrs;
import com.river.common.core.util.Result;
import com.river.site.service.INotebookCategoryService;
import com.river.site.service.INotebookMainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页 前端控制器
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/public/notebook")
@Slf4j
@Api(value = "notebook",tags="站点笔记内容")
public class PublicNotebookController {

    @Autowired
    private INotebookCategoryService notebookCategoryService;

    @Autowired
    private INotebookMainService notebookMainService;
    private NotebookMain result;

    /**
     * 获取笔记分类树
     * @param param
     * @return
     */
    @RequestMapping("/getNotebookCategoryTree")
    @ApiOperation(value = "getNotebookCategoryTree", httpMethod = "GET", response = Result.class, notes = "站点首页数据初始化")
    public Result getNotebookCategoryTree(NotebookCategory param){

        log.info("获取笔记分类树");

        try {

            param.setCategoryName("other");
            param.setExpand(true);

            List<NotebookCategory> roots = notebookCategoryService.getNotebookCategoryTree(param);

            return  Result.ok(roots);

        } catch(Exception e) {
            log.error("加载首页数据异常",e);
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }

    }

    @PostMapping(value = "/getNotebook")
    @ApiOperation(value = "getNotebook", httpMethod = "POST", response = Result.class, notes = "查询笔记列表")
    public Result getNotebook(@RequestBody NotebookMain param){

        log.info("查询笔记列表开始");
        NotebookMain result = param;
        try {

            //LambdaQueryWrapper<NotebookMain> query = Wrappers.<NotebookMain>lambdaQuery(param).orderByDesc(NotebookMain::getCreatedDate);
            QueryWrapper<NotebookMain> query = Wrappers.<NotebookMain>query()
                    .select("sid","category_id","title","LEFT(fnStripTags(LEFT(bodys,500)),200) as bodys","notes","created_by","created_date","update_by","update_date")
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

            NotebookCategory nbc = new NotebookCategory();
            nbc.setCategoryName("other");
            nbc.setParentId("-1");
            List<String> treeAllIds = notebookCategoryService.getNotebookCategoryTreeAllIds(nbc);
            if(treeAllIds.size()>0){
                query.notIn("category_id",treeAllIds);
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
    @ApiOperation(value = "detail", httpMethod = "GET", response = Result.class, notes = "查询笔记详情")
    public Result detail(NotebookMain param){

        log.info("查询笔记详情开始");
        NotebookMain result = param;
        try {

            if(StringUtils.isNotBlank(param.getSid())){
                NotebookMain notebookMain = notebookMainService.getById(param.getSid());
                result = notebookMain==null?result:notebookMain;
            }else{
                return Result.failed("id不能为空");
            }

        } catch(Exception e) {
            log.error("查询笔记详情异常",e);
            e.printStackTrace();
            return  Result.failed("查询笔记详情异常");
        }

        log.info("查询笔记详情结束");

        return  Result.ok(result);
    }

}
