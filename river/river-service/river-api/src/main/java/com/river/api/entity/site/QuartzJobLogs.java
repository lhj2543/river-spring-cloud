package com.river.api.entity.site;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * quartz任务调度日志
 * </p>
 *
 * @author river
 * @since 2020-10-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="QuartzJobLogs对象", description="quartz任务调度日志")
public class QuartzJobLogs extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "触发器ID")
    private String targetId;

    @ApiModelProperty(value = "任务名称")
    private String jobName;

    @ApiModelProperty(value = "任务组名")
    private String jobGroup;

    @ApiModelProperty(value = "任务执行类")
    private String jobClass;

    @ApiModelProperty(value = "任务描述")
    private String jobDescription;

    @ApiModelProperty(value = "任务触发器")
    private String triggerClass;

    @ApiModelProperty(value = "任务表达式")
    private String cronExpression;

    @ApiModelProperty(value = "运行时间")
    private Long runTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8") //优先级高于全局配置 spring.jackson.date-format: yyyy-MM-dd HH:mm:ss
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8") //优先级高于全局配置 spring.jackson.date-format: yyyy-MM-dd HH:mm:ss
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "任务执行数据")
    private String jobData;

    @ApiModelProperty(value = "异常")
    private String exception;

    @ApiModelProperty(value = "状态：2-失败 1-成功")
    private Integer status;


}
