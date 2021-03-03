package com.river.system.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.river.common.security.util.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis plus 自动填充处理类
 * @author river
 */
@Component
public class RiverMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增时候自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createdDate", new Date(), metaObject);
        this.setFieldValByName("createdBy", SecurityUtils.getUserCd(), metaObject);
        this.setFieldValByName("updateDate", new Date(), metaObject);
        this.setFieldValByName("updateBy", SecurityUtils.getUserCd(), metaObject);
    }

    /**
     * 修改时候自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateDate", new Date(), metaObject);
        this.setFieldValByName("updateBy", SecurityUtils.getUserCd(), metaObject);
    }


}
