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
 * quartz job detail
 * </p>
 *
 * @author river
 * @since 2020-10-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="QuartzJobDetail对象", description="quartz job detail")
public class QuartzJobDetail extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "上级ID")
    private String parentId;

    @ApiModelProperty(value = "类型：1：组别 2：job 3:远程url")
    private String type;

    @ApiModelProperty(value = "类名/url")
    @TableField("className")
    private String className;

    @ApiModelProperty(value = "名称：唯一")
    private String name;

    @ApiModelProperty(value = "显示名称")
    private String displayName;

    @ApiModelProperty(value = "job group")
    private String jobGroup;

    @ApiModelProperty(value = "job参数")
    private String jobData;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;


}
