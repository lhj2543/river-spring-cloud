package com.river.api.entity.site;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 站点内容主表
 * </p>
 *
 * @author river
 * @since 2020-09-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SiteConnentMain对象", description="站点内容主表")
public class SiteConnentMain extends Pojo<SiteConnentMain> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "所属目标ID （菜单）")
    private String targetId;

    @ApiModelProperty(value = "语言")
    private String localeId;

    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "类型：1,=链接，2=内容")
    private String type;

    @ApiModelProperty(value = "跳转url")
    private String url;

    @ApiModelProperty(value = "img")
    private String img;

    @ApiModelProperty(value = "置顶")
    private Integer toping;

    @ApiModelProperty(value = "访问量")
    private Integer visits;

    @ApiModelProperty(value = "分类")
    private String category;

    @ApiModelProperty(value = "状态：1:有效，2：无效")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;


}
