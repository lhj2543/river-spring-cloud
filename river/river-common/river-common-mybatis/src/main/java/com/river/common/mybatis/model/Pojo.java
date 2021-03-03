package com.river.common.mybatis.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author rivere
 * @since 2020/4/1
 */

@Data //提供类的get、set、equals、hashCode、canEqual、toString方法
@EqualsAndHashCode(callSuper = false) //此注解会生成equals(Object other) 和 hashCode()方法。 它默认仅使用该类中定义的属性且不调用父类的方法
@Accessors(chain = true) // @Accessors用于配置getter和setter方法的生成结果 chain的中文含义是链式的，设置为true，则setter方法返回当前对象
public class Pojo<T> implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "sid", type = IdType.ASSIGN_ID) //IdType.ASSIGN_ID 雪花算法
    private String sid;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT,updateStrategy = FieldStrategy.NOT_NULL)
    /*@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8") //优先级高于全局配置 spring.jackson.date-format: yyyy-MM-dd HH:mm:ss
    @DateTimeFormat(pattern="yyyy-MM-dd")*/
    private Date createdDate;

    @ApiModelProperty(value = "创建人cd")
    @TableField(fill = FieldFill.INSERT,updateStrategy = FieldStrategy.NOT_NULL)
    private String createdBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    /*@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd")*/
    private Date updateDate;

    @ApiModelProperty(value = "更新人cd")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    //===================非数据库字段========================
    /**
     * 分页
     */
    @TableField(exist = false)
    private Page page;

    public Page getPage(){
        return this.page == null?new Page():this.page;
    }

    /**
     * 预留
     */
    @TableField(exist = false)
    private Map<String,Object> reserve;

    @TableField(exist = false)
    private List<T> rows;

    /**
     * 用户cd
     */
    @TableField(exist = false)
    private String userId;

    //===================非数据库字段========================




}