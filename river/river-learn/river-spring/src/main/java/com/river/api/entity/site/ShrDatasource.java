package com.river.api.entity.site;

import com.river.common.mybatis.model.Pojo;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 数据源信息表
 * </p>
 *
 * @author river
 * @since 2020-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ShrDatasource对象", description="数据源信息表")
public class ShrDatasource extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主机IP")
    @TableField("IP")
    private String ip;

    @ApiModelProperty(value = "标题")
    @TableField("TITLE")
    private String title;

    @ApiModelProperty(value = "类型：mysql,oracle,sqlserver")
    @TableField("DB_TYPE")
    private String dbType;

    @ApiModelProperty(value = "数据库名称")
    @TableField("DB_NAME")
    private String dbName;

    @ApiModelProperty(value = "数据库帐号")
    @TableField("DB_USER")
    private String dbUser;

    @ApiModelProperty(value = "数据库密码")
    @TableField("DB_PASSWORD")
    private String dbPassword;

    @ApiModelProperty(value = "连接url")
    @TableField("DB_URL")
    private String dbUrl;

    @ApiModelProperty(value = "数据库端口")
    @TableField("DB_PORT")
    private Integer dbPort;

    @ApiModelProperty(value = "数据库编码：GBK,UTF-8")
    @TableField("DB_ENCODE")
    private String dbEncode;

    @ApiModelProperty(value = "驱动名称")
    @TableField("DRIVER_NAME")
    private String driverName;

    @ApiModelProperty(value = "连接测试SQL")
    @TableField("LINK_TEST_SQL")
    private String linkTestSql;

    @ApiModelProperty(value = "连接池最大空闲数")
    @TableField("POOL_MAX_IDLE")
    private Integer poolMaxIdle;

    @ApiModelProperty(value = "连接池最大活动数")
    @TableField("POOL_MAX_ACTIVE")
    private Integer poolMaxActive;

    @ApiModelProperty(value = "状态：字典DBSTS：0异常，1就绪，2活动")
    @TableField("STATUS")
    private Integer status;

    @ApiModelProperty(value = "显示顺序")
    @TableField("ORDERED")
    private Integer ordered;

    @ApiModelProperty(value = "描述")
    @TableField("DESCS")
    private String descs;


}
