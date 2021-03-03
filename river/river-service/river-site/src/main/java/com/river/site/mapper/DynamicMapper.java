package com.river.site.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.river.common.mybatis.model.DynamicTable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * 动态 Mapper 接口
 *
 * @author river
 * @since 2020-09-17
 */
@Repository
public interface DynamicMapper extends BaseMapper {

    BigDecimal getMaxNumber(DynamicTable param);

}
