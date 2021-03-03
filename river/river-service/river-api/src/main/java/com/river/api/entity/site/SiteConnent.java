package com.river.api.entity.site;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 站点内容
 * </p>
 *
 * @author river
 * @since 2020-09-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SiteConnent对象", description="站点内容")
public class SiteConnent extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "所属目标ID （站点内容主表）")
    private String targetId;

    @ApiModelProperty(value = "语言")
    private String localeId;

    @ApiModelProperty(value = "内容")
    private String bodys;

    @ApiModelProperty(value = "备注")
    private String notes;


}
