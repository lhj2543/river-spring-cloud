package com.river.api.entity.system;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 系统应用表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysApp对象", description="系统应用表")
public class SysApp extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "父节点Id")
    private String parentId;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "显示名称")
    private String displayName;

    @ApiModelProperty(value = "应用url")
    private String url;

    @ApiModelProperty(value = "状态：0=不显示，1=显示")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;


}
