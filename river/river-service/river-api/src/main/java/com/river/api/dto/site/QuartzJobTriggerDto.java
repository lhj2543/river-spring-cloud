package com.river.api.dto.site;

import com.baomidou.mybatisplus.annotation.TableField;
import com.river.api.entity.site.QuartzJobDetail;
import com.river.api.entity.site.QuartzJobTrigger;
import com.river.common.mybatis.model.Pojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * quartz job 触发器
 *
 * @author river
 * @since 2020-10-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuartzJobTriggerDto extends Pojo {

    private QuartzJobTrigger trigger;

    private List<QuartzJobDetail> jobDetails;


}
