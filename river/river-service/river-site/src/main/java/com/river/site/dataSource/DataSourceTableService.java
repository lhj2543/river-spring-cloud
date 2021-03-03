package com.river.site.dataSource;

import com.river.api.entity.site.ShrDatasource;
import com.river.api.entity.site.Test;
import com.river.site.dataSource.dbtool.GeneratorConstants;
import com.river.site.dataSource.dbtool.GeneratorProperties;
import com.river.site.dataSource.dbtool.provider.db.DataSourceProvider;
import com.river.site.dataSource.dbtool.provider.db.table.Column;
import com.river.site.dataSource.dbtool.provider.db.table.Table;
import com.river.site.dataSource.dbtool.provider.db.table.TableFactory;
import com.river.site.dataSource.dto.ColumnDto;
import com.river.site.dataSource.dto.TableDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 17822
 */
@Service
@Slf4j
public class DataSourceTableService {

    public List<Table> getAllTable(ShrDatasource dtDataSources)
    {
        log.debug("进入-->获取表信息服务");
        List tList = new ArrayList();
        try {
            if (dtDataSources != null) {
                setProperty(dtDataSources);
                tList = TableFactory.getInstance().getAllTables();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("退出-->获取表信息服务");
        return tList;
    }

    public Table getTable(ShrDatasource dtDataSources, String tableName)
    {
        log.debug("进入-->获取表信息服务");
        Table t = null;
        try {
            if (tableName != null) {
                setProperty(dtDataSources);
                t = TableFactory.getInstance().getTable(tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("退出-->获取表信息服务");
        return t;
    }

    public TableDto getTableDto(ShrDatasource dtDataSources, String tableName)
    {
        log.debug("进入-->获取表信息服务");
        Table t = new Table();
        TableDto dto = new TableDto();
        try {
            if (tableName != null) {
                setProperty(dtDataSources);
                t = TableFactory.getInstance().getTable(tableName);
                dto = getTableDto(t);
                dto.setDbName(dtDataSources.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("退出-->获取表信息服务");
        return dto;
    }

    /**
     * 数据源连接测试服务
     * @param dtDataSources
     * @return
     */
    public ShrDatasource testConnect(ShrDatasource dtDataSources)
    {
        log.debug("进入-->数据源连接测试服务");
        try {
            this.setProperty(dtDataSources);
            TableFactory.getInstance();
            Connection conn = DataSourceProvider.getConnection();
            GeneratorProperties.clear();
            conn.close();
            dtDataSources.setStatus(Integer.valueOf(1));
            log.debug("数据库连接成功,{}",dtDataSources.getDbUrl());
        } catch (Exception e) {
            e.printStackTrace();
            dtDataSources.setStatus(Integer.valueOf(0));
            log.error("数据库连接失败,{}",dtDataSources.getDbUrl());
        }
        log.debug("退出-->数据源连接测试服务");
        return dtDataSources;
    }

    public void setProperty(ShrDatasource dtDataSources) {
        try
        {
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
            } else if ("sqlServer2005".equals(dbType)) {
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_USERNAME, dtDataSources.getDbUser());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_PASSWORD, dtDataSources.getDbPassword());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_URL, dtDataSources.getDbUrl());
                GeneratorProperties.setProperty(GeneratorConstants.JDBC_DRIVER, dtDataSources.getDriverName());
            } else {
                throw new RuntimeException("请指定数据库类型");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TableDto getTableDto(Table table) {
        List cList = new ArrayList();
        TableDto dto = new TableDto();
        try {
            dto.setSqlName(table.getSqlName());
            dto.setClassName(table.getClassName());
            dto.setRemarks(table.getRemarks());
            dto.setTableType(table.getTableType());
            for (Column c : table.getColumns()) {
                ColumnDto cdto = new ColumnDto();
                cdto.setSqlTypeName(c.getSqlTypeName());
                cdto.setDecimalDigits(c.getDecimalDigits());
                cdto.setSize(c.getSize());
                cdto.setColumnAlias(c.getColumnAlias());
                cdto.setSqlName(c.getSqlName());
                cdto.setPk(c.isPk());
                cdto.setFk(c.isFk());
                cList.add(cdto);
            }
            dto.setCList(cList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    public static void main(String[] args) {

        DataSourceTableService dataSourceTableService = new DataSourceTableService();

        ShrDatasource shrDatasource = new ShrDatasource();
        shrDatasource.setIp("192.168.0.100");
        shrDatasource.setDbType("mysql");
        shrDatasource.setDbName("river_site");
        shrDatasource.setDbUser("root");
        shrDatasource.setDbPassword("123456");
        shrDatasource.setDbUrl("jdbc:mysql://192.168.0.100:3306/river_site?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai");
        shrDatasource.setDbPort(3306);
        shrDatasource.setDbEncode("utf-8");
        shrDatasource.setDriverName("com.mysql.cj.jdbc.Driver");
        shrDatasource.setStatus(1);

        //ShrDatasource shrDatasource1 = dataSourceTableService.testConnect(shrDatasource);

        TableDto table = dataSourceTableService.getTableDto(shrDatasource, "test");

        System.err.println(table);

        List<Table> allTable = dataSourceTableService.getAllTable(shrDatasource);
        System.err.println(allTable.toString());


    }

}
