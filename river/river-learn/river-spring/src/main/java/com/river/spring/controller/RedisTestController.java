package com.river.spring.controller;

import com.river.common.core.util.Result;
import com.river.spring.constant.CommonConstant;
import com.river.spring.entity.TestEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author river
 */
@RestController
@RequestMapping("/redisTest")
@Api(value = "redisTest",tags = "redis测试")
@Slf4j
public class RedisTestController {

    @Autowired
    RedisTemplate redisTemplate;

    private final String redisKey = CommonConstant.REDIS_USER_KEY;


    @ApiOperation(value = "redis添加缓存", httpMethod = "GET", response = Result.class, notes = "其他说明")
    @RequestMapping("/redisAdd")
    public Result redisAdd(String key,TestEntity param){
        String k = "index:bb";
        redisTemplate.opsForValue().set(redisKey,param,60L,TimeUnit.SECONDS);

        return Result.ok();
    }

    @RequestMapping("/redisDel")
    public Result redisDel(String key){

        Boolean delete = redisTemplate.delete(StringUtils.isBlank(key)?redisKey:key);

        return Result.result(delete);
    }

    @RequestMapping("/getRedis")
    public Result getRedis(String key){

        TestEntity r = (TestEntity) redisTemplate.opsForValue().get(StringUtils.isBlank(key)?redisKey:key);

        return Result.ok(r);
    }

    @RequestMapping("/hasKey")
    public Result hasKey(String key){
        Boolean r = redisTemplate.hasKey(StringUtils.isBlank(key)?redisKey:key);
        return Result.ok(r);
    }

}
