package com.river.api.entity.system;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 用户角色关联表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysUserRole对象", description="用户角色关联表")
public class SysUserRole extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户名")
    private String userCd;

    @ApiModelProperty(value = "角色ID")
    private String roleId;

    @ApiModelProperty(value = "状态1=有效，0=无效")
    private String status;

    @ApiModelProperty(value = "有效开始时间")
    private Date startDate;

    @ApiModelProperty(value = "有效结束时间")
    private Date endDate;

    @ApiModelProperty(value = "备注")
    private String notes;


}
