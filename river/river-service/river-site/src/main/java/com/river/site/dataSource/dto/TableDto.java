package com.river.site.dataSource.dto;

import com.river.common.mybatis.model.Pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class TableDto extends Pojo<TableDto> {
    private String sqlName;
    private String remarks;
    private String className;
    private String dataSourceId;
    private String dbName;
    private String tableType;
    private List<ColumnDto> cList = new ArrayList();
    private String dbType;
}