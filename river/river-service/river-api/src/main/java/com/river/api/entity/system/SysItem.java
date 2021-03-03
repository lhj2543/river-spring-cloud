package com.river.api.entity.system;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 字典(多语言)表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysItem对象", description="字典(多语言)表")
public class SysItem extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "类别id")
    private String categoryId;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "字典key")
    private String itemKey;

    @ApiModelProperty(value = "字典值")
    private String itemValue;

    @ApiModelProperty(value = "状态：0=不显示，1=显示")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;


}
