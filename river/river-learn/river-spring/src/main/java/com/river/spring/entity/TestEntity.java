package com.river.spring.entity;

import com.river.common.mybatis.model.Pojo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author river
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TestEntity extends Pojo<TestEntity> {

    private String name;


}
