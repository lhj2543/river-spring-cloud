package com.river.api.entity.site;

import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author river
 * @since 2020-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="Test对象", description="用户表")
public class Test extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户名")
    private String userCd;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "搜索名")
    private String userSearchName;

    @ApiModelProperty(value = "性别（1=男，2=女）")
    private String sex;

    @ApiModelProperty(value = "国家")
    private String countryCd;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    private String email;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "排序")
    private BigDecimal sortKey;

    @ApiModelProperty(value = "备注")
    private String notes;

    private String content1;

    private String content2;

    private String content3;

    private String content4;

    private String content5;

    private String content6;

    private String content7;


}
