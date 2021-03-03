package com.river.api.dto.system;

import com.river.api.entity.system.SysMenu;
import com.river.api.entity.system.SysUserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author river
 * @date 2020-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuDto extends SysMenu {

    private Set<SysUserRole> userRoles = new HashSet<SysUserRole>(0);

    private List<SysMenuDto> children = new LinkedList<SysMenuDto>();

    /**
     * 是否展开
     */
    private boolean expand;

    /**
     * 是否选择
     */
    private boolean checked;

    /**
     * 角色ID
     */
    private  String roleId;

    /**
     * 层级
     */
    private String level;

}
