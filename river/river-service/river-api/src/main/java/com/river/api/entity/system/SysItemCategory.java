package com.river.api.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 字典类别(多语言)表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysItemCategory对象", description="字典类别(多语言)表")
public class SysItemCategory extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "父节点Id")
    private String parentId;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "类别名称")
    private String categoryName;

    @ApiModelProperty(value = "类别显示名")
    private String displayName;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "状态：0=不显示，1=显示")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;

    //=============非数据库字段=======================

    @TableField(exist = false)
    private List<SysItemCategory> children = new LinkedList<SysItemCategory>();

    @TableField(exist = false)
    private List<SysItem> sysItems = new ArrayList<SysItem>();

    /**
     * 是否展开
     */
    @TableField(exist = false)
    private boolean expand;

    //=============非数据库字段=======================


}
