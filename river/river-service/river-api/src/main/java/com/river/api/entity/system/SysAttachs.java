package com.river.api.entity.system;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * 系统附件表
 * </p>
 *
 * @author river
 * @since 2020-09-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysAttachs对象", description="系统附件表")
public class SysAttachs extends Pojo<SysAttachs> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "app_id")
    private String appId;

    @ApiModelProperty(value = "目标ID")
    private String targetId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "显示名称")
    private String displayName;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "应用url")
    private String path;

    @ApiModelProperty(value = "附件大小")
    private long size;

    @ApiModelProperty(value = "状态：1=公开，2=私有")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;


}
