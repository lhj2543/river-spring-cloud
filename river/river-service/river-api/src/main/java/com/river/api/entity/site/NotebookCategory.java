package com.river.api.entity.site;

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
 * 笔记分类
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="NotebookCategory对象", description="笔记分类")
public class NotebookCategory extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "父节点ID")
    private String parentId;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "类别名称")
    private String categoryName;

    @ApiModelProperty(value = "类别显示名")
    private String displayName;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "状态：2=无效，1=有效")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;

    //==============非数据库字段==================

    @TableField(exist = false)
    private List<NotebookCategory> children = new LinkedList<NotebookCategory>();

    @TableField(exist = false)
    private List<NotebookMain> notebookMain = new ArrayList<NotebookMain>();

    /**
     * 是否展开
     */
    @TableField(exist = false)
    private boolean expand;

    //==============非数据库字段==================

}
