package com.river.api.entity.system;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色菜单关联表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysRoleMenu对象", description="角色菜单关联表")
public class SysRoleMenu extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "角色ID")
    private String roleId;

    @ApiModelProperty(value = "菜单ID")
    private String menuId;

    @ApiModelProperty(value = "状态0=有效，1=无效")
    private String status;

    @ApiModelProperty(value = "备注")
    private String notes;


}
