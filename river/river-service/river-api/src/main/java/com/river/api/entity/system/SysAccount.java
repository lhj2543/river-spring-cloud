package com.river.api.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 系统账户表
 * </p>
 *
 * @author river
 * @since 2020-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysAccount对象", description="系统账户表")
public class SysAccount extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户名")
    private String userCd;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "区域")
    private String localeId;

    @ApiModelProperty(value = "许可证（0=无效效，1=有效）")
    private String licence;

    @ApiModelProperty(value = "账户类型:（0=超级管理员，1=普通用户，2=管理员）")
    private String type;

    @ApiModelProperty(value = "有效开始时间")
    private Date startDate;

    @ApiModelProperty(value = "有效结束时间")
    private Date endDate;

    @ApiModelProperty(value = "锁住时间")
    private Date lockDate;

    @ApiModelProperty(value = "登录失败次数")
    private BigDecimal loginFailureCount;

    @ApiModelProperty(value = "备注")
    private String notes;


    //=========================非持久化属性=============================
    /**
     *	用户名称
     */
    @TableField(exist = false)
    private String userName;


    /**
     * 旧密码
     */
    @TableField(exist = false)
    private String oldPassword;
    //=========================非持久化属性=============================

}
