package com.river.api.entity.system;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysRole对象", description="角色表")
public class SysRole extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "角色code")
    private String roleCode;

    @ApiModelProperty(value = "角色名")
    private String roleName;

    @ApiModelProperty(value = "类别")
    private String category;

    @ApiModelProperty(value = "状态1=有效，0=无效")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;


}
