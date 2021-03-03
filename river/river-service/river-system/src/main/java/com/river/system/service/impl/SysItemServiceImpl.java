package com.river.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.dto.system.SysItemDto;
import com.river.api.entity.system.SysItem;
import com.river.common.core.constant.CacheConstants;
import com.river.system.mapper.SysItemMapper;
import com.river.system.service.ISysItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 字典(多语言)表 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@Service
public class SysItemServiceImpl extends ServiceImpl<SysItemMapper, SysItem> implements ISysItemService {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Map<String,Object> findSysItemByCategoryName(SysItemDto param) {

        String keySysItem = CacheConstants.KEY_SYS_ITEM;
        //缓存时效
        long time = 12;

        Boolean hasItem = redisTemplate.hasKey(keySysItem);
        Map<String,Object> reidsItem = null;
        if(hasItem){
            reidsItem = (Map<String, Object>) redisTemplate.opsForValue().get(keySysItem);
        }

        Map<String,Object> result = new HashMap<String,Object>();

        String categoryName = param.getCategoryName();
        if(StringUtils.isNotBlank(categoryName)){

            if(hasItem && reidsItem!=null && reidsItem.size()>0){
                Map<String,Object> m = (Map<String, Object>) reidsItem.get(categoryName);
                if(m!=null && m.size()>0){
                    return  m;
                }
            }

            List<SysItemDto> rows = baseMapper.findSysItemByCategoryName(param);

            if(rows!=null && rows.size()>0){
                Map<String,Object> itemMap = new HashMap<String,Object>();
                for (SysItemDto i:rows){
                    itemMap.put(i.getItemKey(),i.getItemValue());
                }
                if(reidsItem!=null){
                    reidsItem.put(categoryName,itemMap);
                    redisTemplate.opsForValue().setIfPresent(keySysItem,reidsItem,time, TimeUnit.HOURS);
                }else {
                    reidsItem = new HashMap<String, Object>();
                    reidsItem.put(categoryName,itemMap);
                    redisTemplate.opsForValue().set(keySysItem,reidsItem,time, TimeUnit.HOURS);
                }
                return itemMap;
            }

        }else  if(param.getCategoryNames()!=null && param.getCategoryNames().size()>0){

            Map<String,Object> itemMap = new HashMap<String,Object>();

            List<String> notCacheCategorys = new ArrayList<String>();

            if(hasItem && reidsItem!=null && reidsItem.size()>0){
                for(String category : param.getCategoryNames()){
                    Map<String,Object> m = (Map<String, Object>) reidsItem.get(category);
                    if(m!=null && m.size()>0){
                        itemMap.put(category,m);
                        continue;
                    }
                    notCacheCategorys.add(category);
                }
            }

            if(notCacheCategorys.size()>0){
                param.setCategoryNames(notCacheCategorys);
            }

            List<SysItemDto> rows = baseMapper.findSysItemByCategoryNames(param);

            if(rows!=null && rows.size()>0){

                for (SysItemDto i:rows){
                    String categoryNameT = i.getCategoryName();
                    Map<String, String> item = (Map<String, String>) itemMap.get(categoryNameT);
                    if(item!=null){
                        item.put(i.getItemKey(),i.getItemValue());
                    }else {
                        Map<String,Object> im = new HashMap<String, Object>();
                        im.put(i.getItemKey(),i.getItemValue());
                        itemMap.put(categoryNameT,im);
                    }
                }

                if(reidsItem!=null){
                    reidsItem.putAll(itemMap);
                    redisTemplate.opsForValue().setIfPresent(keySysItem,reidsItem,time, TimeUnit.HOURS);
                }else {
                    redisTemplate.opsForValue().set(keySysItem,itemMap,time, TimeUnit.HOURS);
                }

                return  itemMap;

            }

        }

        return result;
    }

}
