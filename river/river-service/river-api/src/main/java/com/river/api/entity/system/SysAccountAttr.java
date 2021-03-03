package com.river.api.entity.system;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 账户属性表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysAccountAttr对象", description="账户属性表")
public class SysAccountAttr extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户名")
    private String userCd;

    @ApiModelProperty(value = "密码")
    private String attrName;

    @ApiModelProperty(value = "区域")
    private String attrValue;

    @ApiModelProperty(value = "备注")
    private String notes;


}
