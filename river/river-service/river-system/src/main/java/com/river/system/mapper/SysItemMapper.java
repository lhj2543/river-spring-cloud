package com.river.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.river.api.dto.system.SysItemDto;
import com.river.api.entity.system.SysItem;

import java.util.List;

/**
 * <p>
 * 字典(多语言)表 Mapper 接口
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
public interface SysItemMapper extends BaseMapper<SysItem> {

    /**
     * 根据分类名称获取字典
     * @param param
     * @return
     */
    List<SysItemDto> findSysItemByCategoryName(SysItemDto param);

    /**
     * 根据分类名称集合获取字典
     * @param param
     * @return
     */
    List<SysItemDto> findSysItemByCategoryNames(SysItemDto param);

}
