package com.river.api.entity.site;

import com.baomidou.mybatisplus.annotation.TableField;
import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 笔记主表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="NotebookMain对象", description="笔记主表")
public class NotebookMain extends Pojo<NotebookMain> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "笔记标题")
    private String title;

    @ApiModelProperty(value = "笔记内容")
    private String bodys;

    @ApiModelProperty(value = "是否公开（1=公开，2=私有）")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;

    //==============非数据库字段==================
    /**
     *  统配检索
     */
    @TableField(exist = false)
    private String searchValue;

    //==============非数据库字段==================


}
