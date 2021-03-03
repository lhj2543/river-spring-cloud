package com.river.common.mybatis.model;


import cn.hutool.db.meta.Table;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true) // @Accessors用于配置getter和setter方法的生成结果 chain的中文含义是链式的，设置为true，则setter方法返回当前对象
public class DynamicTable {

    public DynamicTable() {}

    public DynamicTable(String tableName, QueryWrapper queryWrapper) {
        this.tableName = tableName;
        this.setQueryWrapper(queryWrapper);
    }

    private String tableName;

    private QueryWrapper queryWrapper;

    private String where;

    private String numberColumn;

    public String getWhere(){
        StringBuffer result = new StringBuffer();

        if(queryWrapper!=null){
            String targetSql = queryWrapper.getTargetSql();
            Map<String,String> paramNameValuePairs = queryWrapper.getParamNameValuePairs();
            String customSqlSegment = queryWrapper.getCustomSqlSegment();

            if(paramNameValuePairs!=null){

                for (Map.Entry<String, String> entry : paramNameValuePairs.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    customSqlSegment = customSqlSegment.replace("#{ew.paramNameValuePairs." + key + "}", "'"+value+"'");
                }}

                result.append(customSqlSegment);

            }

        return result.toString();
    }

}
