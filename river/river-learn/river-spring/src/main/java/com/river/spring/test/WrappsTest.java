package com.river.spring.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.spring.entity.TestEntity;

/**
 * @author river
 */
public class WrappsTest {



    public static void main(String[] args) {

        QueryWrapper query = Wrappers.query().select("sid","name")
                .eq("name", "aaa").like("sid","bbbb");


        System.out.println(query.getSqlSelect());
        System.out.println(query.getEntity());
        System.out.println(query.getTargetSql());
        System.out.println(query.getParamNameValuePairs());
        System.out.println(query.getSqlSet());
        System.out.println(query.getCustomSqlSegment());


        query.getParamNameValuePairs().forEach((key,value)->{
            System.out.println(key);
            System.out.println(value);
        });


    }


}
