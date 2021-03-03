package com.river.api.entity.site;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 站点属性
 * </p>
 *
 * @author river
 * @since 2020-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SiteAttrs对象", description="站点属性")
public class SiteAttrs extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "所属目标ID （菜单）")
    private String targetId;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "属性key")
    private String attrKey;

    @ApiModelProperty(value = "属性值")
    private String attrValue;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "1:有效，2：无效")
    private String status;

    @ApiModelProperty(value = "备注")
    private String notes;


}
