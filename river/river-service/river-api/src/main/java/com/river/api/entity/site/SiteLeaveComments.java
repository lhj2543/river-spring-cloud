package com.river.api.entity.site;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 站点留言
 * </p>
 *
 * @author river
 * @since 2020-09-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SiteLeaveComments对象", description="站点留言")
public class SiteLeaveComments extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "留言人ID")
    private String userCd;

    @ApiModelProperty(value = "留言人姓名")
    private String userName;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "备注")
    private String notes;


}
