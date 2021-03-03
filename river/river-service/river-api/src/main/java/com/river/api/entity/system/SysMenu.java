package com.river.api.entity.system;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysMenu对象", description="菜单表")
public class SysMenu extends Pojo<SysMenu> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "应用ID")
    private String appId;

    @ApiModelProperty(value = "父节点Id")
    private String parentId;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "权限")
    private String permission;

    @ApiModelProperty(value = "菜单名")
    private String menuName;

    @ApiModelProperty(value = "菜单显示名")
    private String displayName;

    @ApiModelProperty(value = "url")
    private String url;

    @ApiModelProperty(value = "页面文件路径")
    private String pagePath;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "类别")
    private String category;

    @ApiModelProperty(value = "类型：0=菜单，1=iframe，2=页面 ,3=父菜单,4=按钮")
    private String type;

    @ApiModelProperty(value = "状态：0=不显示，1=显示")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;


}
