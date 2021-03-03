package com.river.spring.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.river.common.core.util.Result;
import com.river.spring.constant.CommonConstant;
import com.river.spring.entity.TestEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author river
 */
@RestController
@RequestMapping("/CacheTest")
@Api(value = "CacheTest",tags = "Cache测试")
@Slf4j
public class CacheTestController {

    @Autowired
    CacheManager cacheManager;


    @ApiOperation(value = "CacheManager 测试", httpMethod = "GET", response = Result.class, notes = "其他说明")
    @RequestMapping("/CacheManagerTest")
    public Result CacheManagerTest(TestEntity param){
        String key = param.getName();

        Cache cache = cacheManager.getCache(CommonConstant.REDIS_USER_KEY);

        cache.put(key,param);

        Cache.ValueWrapper valueWrapper = cache.get(key);

        TestEntity o = null;
        if(valueWrapper != null ){
            o = (TestEntity) valueWrapper.get();
        }

        return Result.ok(o);
    }

    @ApiOperation(value = "@Cacheable 测试", httpMethod = "GET", response = Result.class, notes = "其他说明")
    @RequestMapping("/CacheableTest")
    /**
     * value/cacheNames :缓存名 可以多个{"name1", "name2"} 当执行方法之前，这些关联的每一个缓存都会被检查，而且只要至少其中一个缓存命中了，那么这个缓存中的值就会被返回
     * key :缓存key
     *      #root.methodName 当前方法名
     *      #root.method.name 当前方法
     *      #root.target 当前被调用的对象
     *      #root.targetClass 当前被调用的对象的class
     *      #root.args[0] 当前方法参数组成的数组
     *      #root.caches[0].name 当前被调用的方法使用的Cache
     * sync : 是否同步 是否同步，true/false。在一个多线程的环境中，某些操作可能被相同的参数并发地调用，这样同一个 value 值可能被多次计算（或多次访问 db），这样就达不到缓存的目的。针对这些可能高并发的操作，我们可以使用 sync 参数来告诉底层的缓存提供者将缓存的入口锁住，这样就只能有一个线程计算操作的结果值，而其它线程需要等待，这样就避免了 n-1 次数据库访问。sync = true 可以有效的避免缓存击穿的问题。
     * condition ：调用前判断，缓存的条件。有时候，我们可能并不想对一个方法的所有调用情况进行缓存，我们可能想要根据调用方法时候的某些参数值，来确定是否需要将结果进行缓存或者从缓存中取结果。比如当我根据年龄查询用户的时候，我只想要缓存年龄大于 35 的查询结果。那么 condition 能实现这种效果。　　
     * unless : 执行后判断，不缓存的条件。unless 接收一个结果为 true 或 false 的表达式
     *   condition & unless同时指定情况
     *      condition 不指定相当于 true，unless 不指定相当于 false
     *      当 condition = false，一定不会缓存；
     *      当 condition = true，且 unless = true，不缓存；
     *      当 condition = true，且 unless = false，缓存；
     *
     */
    @Cacheable(value = CommonConstant.REDIS_TEST_CACHENAMES,key = "#root.methodName",unless = "#result.success==false")
    public Result CacheableTest(TestEntity param){
        log.info("访问DB");
        return Result.ok(param);
    }

    @ApiOperation(value = "@CachePut 测试", httpMethod = "GET", response = Result.class, notes = "其他说明")
    @RequestMapping("/CachePutTest")
    /**
     * 与@Cacheable不同的是使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中
     */
    @CachePut(value = CommonConstant.REDIS_TEST_CACHENAMES,key = "#root.method.name",unless = "#result.success") //每次都会执行方法，并将结果存入指定的缓存中
    public Result CachePutTest(TestEntity param){
        log.info("每次都会执行方法，并将结果存入指定的缓存中");
        return Result.failed(param);
    }

    @ApiOperation(value = "@CacheEvict 测试", httpMethod = "GET", response = Result.class, notes = "其他说明")
    @RequestMapping("/CacheEvictTest")
    /**
     *  @CacheEvict是用来标注在需要清除缓存元素的方法或类上的。当标记在一个类上时表示其中所有的方法的执行都会触发缓存的清除操作。@CacheEvict可以指定的属性有value、key、condition、allEntries和beforeInvocation。其中value、key和condition的语义与@Cacheable对应的属性类似。即value表示清除操作是发生在哪些Cache上的（对应Cache的名称）；key表示需要清除的是哪个key，如未指定则会使用默认策略生成的key；condition表示清除操作发生的条件。下面我们来介绍一下新出现的两个属性allEntries和beforeInvocation。
     *      allEntries是boolean类型，表示是否需要清除缓存中的所有元素。默认为false，表示不需要。当指定了allEntries为true时，Spring Cache将忽略指定的key。有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。
     *      beforeInvocation 清除操作默认是在对应方法成功执行之后触发的，即方法如果因为抛出异常而未能成功返回时也不会触发清除操作。使用beforeInvocation可以改变触发清除操作的时间，当我们指定该属性值为true时，Spring会在调用该方法之前清除缓存中的指定元素。
     */
    @CacheEvict(value = CommonConstant.REDIS_TEST_CACHENAMES,allEntries = true)
    public Result CacheEvictTest(TestEntity param){
        log.info("清除{}下所有缓存",CommonConstant.REDIS_TEST_CACHENAMES);
        return Result.ok();
    }

    @ApiOperation(value = "@Caching 测试", httpMethod = "GET", response = Result.class, notes = "其他说明")
    @RequestMapping("/CachingTest")
    /**
     *  @Caching注解可以让我们在一个方法或者类上同时指定多个Spring Cache相关的注解。其拥有三个属性：cacheable、put和evict，分别用于指定@Cacheable、@CachePut和@CacheEvict。
     */
    @Caching(
            cacheable = @Cacheable("users"),
            evict = { @CacheEvict("cache2"),@CacheEvict(value = "cache3", allEntries = true) }
            )
    public Result CachingTest(TestEntity param){

        return Result.ok();
    }

}
