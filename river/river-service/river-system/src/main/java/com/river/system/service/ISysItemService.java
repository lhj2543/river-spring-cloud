package com.river.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.river.api.dto.system.SysItemDto;
import com.river.api.entity.system.SysItem;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典(多语言)表 服务类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
public interface ISysItemService extends IService<SysItem> {

    /**
     * 根据分类名称获取字典
     * @param param
     * @return
     */
    Map<String,Object> findSysItemByCategoryName(SysItemDto param);

}
