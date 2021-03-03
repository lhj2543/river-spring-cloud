package com.river.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.api.dto.system.SysMenuDto;
import com.river.api.entity.system.SysMenu;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.Result;
import com.river.system.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 公开接口
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/public")
@Slf4j
@Api(value = "public",tags="公开接口")
public class PublicController {

    @Autowired
    ISysMenuService sysMenuService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 加载站点首页菜单
     * @param param
     * @return
     */
    @PostMapping("/loadTopMenus")
    @ApiOperation(value = "加载站点首页菜单", httpMethod = "POST", response = Result.class, notes = "")
    public Result loadTopMenus(@RequestBody SysMenu param){

        log.info("加载个人主页菜单开始");
        String cacheSiteMenuKey = CacheConstants.KEY_SITE_MENU;

        List<SysMenu> result;

        try {

            Boolean chcaheHasMenu = redisTemplate.hasKey(cacheSiteMenuKey);
            if(chcaheHasMenu){
                List<SysMenu> o = (List<SysMenu>) redisTemplate.opsForValue().get(cacheSiteMenuKey);
                return Result.ok(o);
            }

            param.setCategory("site-top");
            param.setStatus("1");

            //QueryWrapper query = Wrappers.query(param).orderByAsc("sort_key");
            LambdaQueryWrapper<SysMenu> query = Wrappers.lambdaQuery(param).orderByAsc(SysMenu::getSortKey);

            result = sysMenuService.list(query);

            if(result!=null && result.size()>0){
                redisTemplate.opsForValue().set(cacheSiteMenuKey,result,4L, TimeUnit.HOURS);
            }

        } catch(Exception e) {
            log.error("加载个人主页菜单结束异常",e);
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }

        log.info("加载个人主页菜单结束");

        return  Result.ok(result);
    }

}
