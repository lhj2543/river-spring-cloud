package com.river.site.service.impl;

import com.river.common.mybatis.model.DynamicTable;
import com.river.site.mapper.DynamicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 动态查询
 * @author river
 * @since 2020-09-17
 */
@Service
public class DynameicService {

    @Autowired
    private DynamicMapper dynamicMapper;

   public BigDecimal getMaxNumber(DynamicTable params){

       BigDecimal maxNumber = dynamicMapper.getMaxNumber(params);

       return maxNumber;

   }

}
