package com.river.site.dataSource.dto;

import com.river.common.mybatis.model.Pojo;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ColumnDto extends Pojo<ColumnDto> {
    private static final long serialVersionUID = 1L;
    private int sqlType;
    private String sqlTypeName;
    private String sqlName;
    private boolean isPk;
    private boolean isFk;
    private int size;
    private int decimalDigits;
    private boolean isNullable;
    private boolean isIndexed;
    private boolean isUnique;
    private String defaultValue;
    private String remarks;
    private String columnAlias;

}
