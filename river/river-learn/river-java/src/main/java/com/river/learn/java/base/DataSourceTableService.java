package com.river.learn.java.base;


import com.aifa.project.dmp.core.dbtool.GeneratorConstants;
import com.aifa.project.dmp.core.dbtool.GeneratorProperties;
import com.aifa.project.dmp.core.dbtool.provider.db.DataSourceProvider;
import com.aifa.project.dmp.core.dbtool.provider.db.table.Column;
import com.aifa.project.dmp.core.dbtool.provider.db.table.Table;
import com.aifa.project.dmp.core.dbtool.provider.db.table.TableFactory;
import com.aifa.project.dmp.model.ShrDatasource;
import com.aifa.project.dmp.model.dto.ColumnDto;
import com.aifa.project.dmp.model.dto.TableDto;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class DataSourceTableService {

    public DataSourceTableService() {
    }

    public List<Table> getAllTable(ShrDatasource dtDataSources) {
        System.out.println("进入-->获取表信息服务");
        Object tList = new ArrayList();

        try {
            if (dtDataSources != null) {
                this.setProperty(dtDataSources);
                tList = TableFactory.getInstance().getAllTables();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        System.out.println("退出-->获取表信息服务");
        return (List)tList;
    }

    public Table getTable(ShrDatasource dtDataSources, String tableName) {
        System.out.println("进入-->获取表信息服务");
        Table t = new Table();

        try {
            if (tableName != null) {
                this.setProperty(dtDataSources);
                t = TableFactory.getInstance().getTable(tableName);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        System.out.println("退出-->获取表信息服务");
        return t;
    }

    public TableDto getTableDto(ShrDatasource dtDataSources, String tableName) {
        System.out.println("进入-->获取表信息服务");
        new Table();
        TableDto dto = new TableDto();

        try {
            if (tableName != null) {
                this.setProperty(dtDataSources);
                Table t = TableFactory.getInstance().getTable(tableName);
                dto = this.getTableDto(t);
                dto.setDbName(dtDataSources.getTitle());
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        System.out.println("退出-->获取表信息服务");
        return dto;
    }

    public ShrDatasource testConnect(ShrDatasource dtDataSources) {
        System.out.println("进入-->数据源连接测试服务");

        try {
            this.setProperty(dtDataSources);
            TableFactory.getInstance();
            Connection conn = DataSourceProvider.getConnection();
            GeneratorProperties.clear();
            conn.close();
            dtDataSources.setSuccess(true);
            dtDataSources.setStatus(1);
            dtDataSources.setMessage("数据库连接成功");
        } catch (Exception var3) {
            var3.printStackTrace();
            dtDataSources.setSuccess(false);
            dtDataSources.setStatus(0);
            dtDataSources.setMessage("数据库连接失败");
        }

        System.out.println("退出-->数据源连接测试服务");
        return dtDataSources;
    }

    public void setProperty(ShrDatasource dtDataSources) {
        try {
            String dbType = dtDataSources.getDbType();
            if (dbType == null) {
                return;
            }

            if ("hsql".equals(dbType)) {
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_URL, dtDataSources.getDbUrl());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_DRIVER, dtDataSources.getDriverName());
            } else if ("h2".equals(dbType)) {
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_USERNAME, dtDataSources.getDbUser());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_PASSWORD, dtDataSources.getDbPassword());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_URL, dtDataSources.getDbUrl());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_DRIVER, dtDataSources.getDriverName());
            } else if ("mysql".equals(dbType)) {
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_USERNAME, dtDataSources.getDbUser());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_PASSWORD, dtDataSources.getDbPassword());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_URL, dtDataSources.getDbUrl());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_DRIVER, dtDataSources.getDriverName());
            } else if ("oracle".equals(dbType)) {
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_SCHEMA, dtDataSources.getDbUser().toUpperCase());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_USERNAME, dtDataSources.getDbUser());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_PASSWORD, dtDataSources.getDbPassword());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_URL, dtDataSources.getDbUrl());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_DRIVER, dtDataSources.getDriverName());
            } else {
                if (!"sqlServer2005".equals(dbType)) {
                    throw new RuntimeException("请指定数据库类型");
                }

                GeneratorProperties.setProperty(GeneratorConstants.JDBC_USERNAME, dtDataSources.getDbUser());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_PASSWORD, dtDataSources.getDbPassword());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_URL, dtDataSources.getDbUrl());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_DRIVER, dtDataSources.getDriverName());
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private TableDto getTableDto(Table table) {
        List<ColumnDto> cList = new ArrayList();
        TableDto dto = new TableDto();

        try {
            dto.setSqlName(table.getSqlName());
            dto.setClassName(table.getClassName());
            dto.setRemarks(table.getRemarks());
            dto.setTableType(table.getTableType());
            Iterator var4 = table.getColumns().iterator();

            while(var4.hasNext()) {
                Column c = (Column)var4.next();
                ColumnDto cdto = new ColumnDto();
                cdto.setsqlTypeName(c.getSqlTypeName());
                cdto.setdecimalDigits(c.getDecimalDigits());
                cdto.setsize(c.getSize());
                cdto.setColumnAlias(c.getColumnAlias());
                cdto.setsqlName(c.getSqlName());
                cdto.setisPk(c.isPk());
                cdto.setisFk(c.isFk());
                cList.add(cdto);
            }

            dto.setcList(cList);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return dto;
    }
}

