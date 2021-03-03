package com.river.api.entity.site;

import com.baomidou.mybatisplus.annotation.TableField;
import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * quartz job 触发器
 * </p>
 *
 * @author river
 * @since 2020-10-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="QuartzJobTrigger对象", description="quartz job 触发器")
public class QuartzJobTrigger extends Pojo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "job ID")
    private String targetId;

    @ApiModelProperty(value = "类型：1:Cron表达式2:简单模式")
    private String type;

    @ApiModelProperty(value = "触发器名：唯一")
    private String triggerName;

    @ApiModelProperty(value = "显示名称")
    private String displayName;

    @ApiModelProperty(value = "触发器参数")
    private String cronExpression;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "开始执行时间")
    private Date startTime;

    @ApiModelProperty(value = "结束执行时间")
    private Date endTime;

    @ApiModelProperty(value = "间隔(多少秒执行一次)")
    @TableField("intervalIn_seconds")
    private Integer intervalinSeconds;

    @ApiModelProperty(value = "执行次数（<=0情况重复执行）")
    private Integer repeatCount;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @ApiModelProperty(value = "触发器参数")
    private String triggerData;

    @ApiModelProperty(value = "备注")
    private String notes;


}
