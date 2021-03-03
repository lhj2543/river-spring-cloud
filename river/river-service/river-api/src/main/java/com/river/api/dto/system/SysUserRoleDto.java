package com.river.api.dto.system;

import com.river.api.entity.system.SysUserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author river
 * @date 2020-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserRoleDto extends SysUserRole {

    /**
     * 角色code
     */
    private String roleCode;
    /**
     * 角色名称
     */
    private String roleName;


}
