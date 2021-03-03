package com.river.common.core.component;


import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 缓存管理
 * @author river
 */
@Component
@Slf4j
public class CacheCommonManage {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 菜单相关缓存清除
     * @return
     */
    public Result cleanMenuCache(boolean flag){

        log.info("清除菜单缓存，boolean={}",flag);

        try {
            if(flag){
                //清除用户菜单缓存
                this.cleanCache(CacheConstants.PREFIX_USER_MENU,null);
                //清除站点菜单缓存
                this.cleanCache(null, CacheConstants.KEY_SITE_MENU);
            }

        }catch (Exception e){
            log.error("清除菜单缓存异常！",e);
            return Result.failed(e.getMessage());
        }

        return Result.ok();

    }

    /**
     * 清除字典缓存
     * @param categoryName 根据分类名称清除
     * @param isCleanAll 是否清除字典所有数据
     * @return
     */
    public Result cleanSysItem(String categoryName ,boolean isCleanAll){

        log.info("清除字典缓存，categoryName={}",categoryName);

        try {
            if(isCleanAll){
                this.cleanCache(null, CacheConstants.KEY_SYS_ITEM);
            }else {
                if(StringUtils.isNotBlank(categoryName)){

                    Boolean b = redisTemplate.hasKey(CacheConstants.KEY_SYS_ITEM);
                    if(b){
                        Map<String,Object> data = (Map<String, Object>) redisTemplate.opsForValue().get(CacheConstants.KEY_SYS_ITEM);
                        if(data.get(categoryName)!=null){
                            data.remove(categoryName);
                            redisTemplate.opsForValue().setIfPresent(CacheConstants.KEY_SYS_ITEM,data);
                        }
                    }
                }
            }

        }catch (Exception e){
            log.error("清除字典缓存异常！",e);
            return Result.failed(e.getMessage());
        }

        return Result.ok();
    }

    /**
     * 缓存清除
     * @param prefix 为空情况 直接清除key
     * @param key 为空情况 清除prefix所有 ，prefix 都不为空情况 清除key
     * @return
     */
    public Result cleanCache(String prefix,String key){

        log.info("清除redis缓存，prefix={},key={}",prefix,key);
        boolean b = false;

        if(StringUtils.isEmpty(prefix) && StringUtils.isEmpty(key)){
            return null;
        }

        if(StringUtils.isEmpty(prefix) && StringUtils.isNotBlank(key)){
            if(redisTemplate.hasKey(key)){
                b = redisTemplate.delete(key);
            }
        }else{
            Cache cache = cacheManager.getCache(prefix);

            if(cache == null){
                return null;
            }
            if(StringUtils.isEmpty(key)){
                //移除目录下所有缓存
                cache.clear();
            }else {
                //移除存在的缓存
                b = cache.evictIfPresent(key);
            }

        }

        return Result.result(b);
    }

}
