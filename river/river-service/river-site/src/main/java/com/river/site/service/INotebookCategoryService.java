package com.river.site.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.river.api.entity.site.NotebookCategory;

import java.util.List;

/**
 * <p>
 * 笔记分类 服务类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
public interface INotebookCategoryService extends IService<NotebookCategory> {

    /**
     * 获取笔记分类树
     * @param param
     * @return
     */
    List<NotebookCategory>  getNotebookCategoryTree(NotebookCategory param);

    /**
     * 递归获取笔记分类ID
     * @param param
     * @return
     */
    List<String>  getNotebookCategoryTreeAllIds(NotebookCategory param);

}
