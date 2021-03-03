package com.river.learn.java.base;

import com.aifa.project.dmp.core.dbtool.provider.db.table.Table;
import com.aifa.project.dmp.model.ShrDatasource;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class Test {

    public static void main(String[] args) throws UnsupportedEncodingException {
        /*Object obj = new Object();
        obj.hashCode();

        User u = new User();
        u.setName("a");

        User u2 = u;

        u2.setName("b");

        System.out.println(u.getName());

        System.out.println(u.equals(u2));

        byte[] bytes = new byte[]{'a','b'};

        String s = new String(bytes,StandardCharsets.UTF_8);*/


        DataSourceTableService dbService = new DataSourceTableService();

        ShrDatasource shrDatasource = new ShrDatasource();
        shrDatasource.setDbType("mysql");
        shrDatasource.setDbName("river_site");
        shrDatasource.setDbUser("root");
        shrDatasource.setDbPassword("123456");
        shrDatasource.setDbUrl("jdbc:mysql://127.0.0.1:3306/river_site?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai");
        shrDatasource.setDbPort(3306);
        shrDatasource.setDbEncode("utf-8");
        shrDatasource.setDriverName("com.mysql.cj.jdbc.Driver");
        shrDatasource.setStatus(1);

        List<Table> allTable =  dbService.getAllTable(shrDatasource);
        System.out.println(allTable);



    }

}

@Data
class User{
    private String name;
    private int age;
}
