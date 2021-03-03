package com.river.site.controller.publics;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.river.api.entity.site.Test;
import com.river.common.core.util.Result;
import com.river.site.service.ITestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 首页 前端控制器
 *
 * @author river
 * @since 2020-09-01
 */
@RestController
@RequestMapping("/public/test")
@Slf4j
@Api(value = "index",tags="站点首页管理")
public class TestController {

    @Autowired
    private ITestService iTestService;

    private ExecutorService executorService;

    public TestController(){
        log.info("线程池初始化");
        //cup内核数
        int cup = Runtime.getRuntime().availableProcessors();

        ArrayBlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<Runnable>(15);

        //拒绝策略: 默认 直接抛出RejectedExcutionException异常，阻止系统正常运行
        ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        executorService = new ThreadPoolExecutor(
                cup * 2,
                cup * 2 +1,
                10,
                TimeUnit.SECONDS,
                arrayBlockingQueue,
                threadFactory,
                abortPolicy
        );

        log.info("线程池初始化完了");
    }

    /**
     * 获取测试数据
     * @param param
     * @return
     */
    @RequestMapping("/list")
    @ApiOperation(value = "list", httpMethod = "GET", response = Result.class, notes = "测试")
    public Result list(Test param){

        log.info("获取测试数据开始");
        Page<Test> page = new Page<>();
        try {

            page = iTestService.page(page);

        } catch(Exception e) {
        }

        return  Result.ok(page);
    }


}

