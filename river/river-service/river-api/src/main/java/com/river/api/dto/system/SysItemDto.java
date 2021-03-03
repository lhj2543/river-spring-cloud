package com.river.api.dto.system;

import com.river.api.entity.system.SysItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @author river
 * @date 2020-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysItemDto extends SysItem {

    private String categoryName;

    private List<String> categoryNames;

    /**
     * 字典集合
     */
    private Map<String,Object> itemMap;

}
