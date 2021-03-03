package com.river.common.mybatis.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 公共方面类
 * @author river
 */

@UtilityClass
public class MyBatisUtils {

    /**
     * 设置排序
     * @param page
     * @param query
     * @return
     */
    public QueryWrapper setOrder(Page page, QueryWrapper query){
        return setOrder(page,query,false);
    }

    /**
     * 设置排序
     * @param page
     * @param query
     * @param isDefault 是否默认排序
     * @return
     */
    public QueryWrapper setOrder(Page page, QueryWrapper query,boolean isDefault){

        if(page == null){
            return  query;
        }

        List<OrderItem> orders = page.orders();
        if(orders!=null){
            orders.forEach(oi->{
                query.orderBy(true,oi.isAsc(),oi.getColumn());
            });
        }else{
            if(isDefault){
                //默认排序
                query.orderBy(true,false,"update_date");
            }
        }
        return  query;
    }

    /**
     * 设置like 查询字段
     * @param fields
     * @param object
     */
    public  void  like(String[] fields,Object object,QueryWrapper query){
        like(fields,object,query,false);
    }

    /**
     * 设置like 查询字段
     * @param fields
     * @param object
     * @param isOr
     */
    public  void  like(String[] fields,Object object,QueryWrapper query,boolean isOr){

        try {
            Class<?> clazz = object.getClass();
            Class superclass = clazz.getSuperclass();

            for (int i=0;i<fields.length;i++) {
                String f = fields[i];
                try {
                    String field = camelCase(f);

                    Field declaredField = null;
                    try{
                        //先从父类查找
                        declaredField = superclass.getDeclaredField(field);
                    }catch (Exception e){
                        //获取所有字段,public和protected和private,但是不包括父类字段
                        declaredField = clazz.getDeclaredField(field);
                    }

                    if(declaredField != null){
                        //设置权限 可以对非public修饰的变量操作
                        declaredField.setAccessible(true);
                        Object value = declaredField.get(object);

                        Class<?> type = declaredField.getType();
                        if(value!=null && type == String.class && StringUtils.isNotBlank(value.toString())){
                            query.like(f,value);
                            if(isOr && i<fields.length-1){
                                query.or();
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  驼峰命名转换 _aa__Bc_C_c_  ==>  aaBcCc
     * @param str
     * @return 驼峰命名字符串
     */
    public String camelCase(String str){
        String camelCase = "";
        String [] arr = str.split("_");
        List<String> list = new ArrayList<String>();

        //将数组中非空字符串添加至list
        for(String a : arr){
            if(a.length() > 0){
                list.add(a);
            }
        }

        for(int i=0;i<list.size();i++){
            //后面单词首字母大写
            if(i>0){
                char c = list.get(i).charAt(0);
                String s = String.valueOf(c).toUpperCase() + list.get(i).substring(1).toLowerCase();
                camelCase+=s;
            }else{  //首个单词小写
                camelCase+=list.get(i).toLowerCase();
            }
        }
        return camelCase;
    }

}
