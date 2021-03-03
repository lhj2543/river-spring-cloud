package com.river.site.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.entity.site.NotebookCategory;
import com.river.common.core.constant.CacheConstants;
import com.river.site.mapper.NotebookCategoryMapper;
import com.river.site.service.INotebookCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 笔记分类 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@Service
public class NotebookCategoryServiceImpl extends ServiceImpl<NotebookCategoryMapper, NotebookCategory> implements INotebookCategoryService {

    @Override
    @Cacheable(value = CacheConstants.PREFIX_SITE_NOTEBOOKCATEGORY,key = "'"+CacheConstants.KEY_SITE_NOTEBOOKCATEGORY+"' + #param.getCategoryName()",unless = "#result==null || #result.size()<=0")
    public List<NotebookCategory> getNotebookCategoryTree(NotebookCategory param) {

        LambdaQueryWrapper<NotebookCategory> query = Wrappers.<NotebookCategory>lambdaQuery().eq(NotebookCategory::getParentId, "-1")
                .eq(NotebookCategory::getStatus,"1");

        if(StringUtils.isNotBlank(param.getCategoryName())){
            query.ne(NotebookCategory::getCategoryName,param.getCategoryName());
        }
        query.orderByAsc(NotebookCategory::getSortKey);

        List<NotebookCategory> roots = this.list(query);

        List<NotebookCategory> rows = this.list(
                Wrappers.<NotebookCategory>lambdaQuery().ne(NotebookCategory::getParentId,"-1").eq(NotebookCategory::getStatus,"1")
                        .orderByAsc(NotebookCategory::getSortKey)
        );

        for (NotebookCategory m:roots){
            m.setExpand(param.isExpand());
            this.recurrence(m,rows,param);
        }

        return roots;
    }

    @Override
    @Cacheable(value = CacheConstants.PREFIX_SITE_NOTEBOOKCATEGORY,key = "'"+CacheConstants.KEY_SITE_NOTEBOOKCATEGORY+"' + #param.getSid()+#param.getCategoryName()+#param.getParentId()",unless = "#result==null || #result.size()<=1")
    public List<String> getNotebookCategoryTreeAllIds(NotebookCategory param) {

        LambdaQueryWrapper<NotebookCategory> query = Wrappers.<NotebookCategory>lambdaQuery()
                .select(NotebookCategory::getSid,NotebookCategory::getParentId,NotebookCategory::getCategoryName,NotebookCategory::getDisplayName)
                .eq(NotebookCategory::getStatus,"1")
                .orderByAsc(NotebookCategory::getSortKey);

        if(StringUtils.isNotBlank(param.getSid())){
            query.eq(NotebookCategory::getSid,param.getSid());
        }
        if(StringUtils.isNotBlank(param.getCategoryName())){
            query.eq(NotebookCategory::getCategoryName,param.getCategoryName());
        }
        if(StringUtils.isNotBlank(param.getParentId())){
            query.eq(NotebookCategory::getParentId, param.getParentId());
        }

        List<NotebookCategory> roots = this.list(query);

        List<NotebookCategory> rows = this.list(
                Wrappers.<NotebookCategory>lambdaQuery()
                        .select(NotebookCategory::getSid,NotebookCategory::getParentId,NotebookCategory::getCategoryName,NotebookCategory::getDisplayName)
                        .ne(NotebookCategory::getParentId,"-1")
                        .eq(NotebookCategory::getStatus,"1")
                        .orderByAsc(NotebookCategory::getSortKey)
        );

        List<String> result = new ArrayList<String>();

        for (NotebookCategory m:roots){
            result.add(m.getSid());
            this.recurrence(m,rows,result);
        }

        return result;
    }

    /**
     * 递归笔记分类
     * @param row
     * @param rows
     * @param params
     */
    public void recurrence(NotebookCategory row,List<NotebookCategory> rows,NotebookCategory params){

        for(NotebookCategory m:rows){

            if(StringUtils.equals(m.getParentId(),row.getSid())){
                m.setExpand(params.isExpand());
                row.getChildren().add(m);
                this.recurrence(m,rows,params);
            }

        }

    }

    /**
     * 递归笔记获取所有ID
     * @param row
     * @param rows
     */
    public void recurrence(NotebookCategory row,List<NotebookCategory> rows,List<String> result){

        for(NotebookCategory m:rows){

            if(StringUtils.equals(m.getParentId(),row.getSid()) && !result.contains(m.getSid())){
                result.add(m.getSid());
                this.recurrence(m,rows,result);
            }

        }

    }

}
