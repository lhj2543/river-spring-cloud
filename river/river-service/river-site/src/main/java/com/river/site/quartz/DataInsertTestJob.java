package com.river.site.quartz;

import com.river.api.entity.site.Test;
import com.river.common.core.util.Result;
import com.river.common.core.util.SpringContextHolder;
import com.river.site.service.ITestService;
import com.river.site.service.impl.TestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 数据测试
 */
@Slf4j
public class DataInsertTestJob implements Job {

    private ITestService iTestService;


    private ThreadPoolExecutor threadPoolExecutor;

    public DataInsertTestJob(){

        iTestService = SpringContextHolder.getBean(TestServiceImpl.class);

        this.initThreadPoolExecutor();

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        Trigger trigger = jobExecutionContext.getTrigger();

        JobDataMap jobDataMap = trigger.getJobDataMap();
        if(jobDataMap!=null && jobDataMap.get("type")!=null){

            String type = (String) jobDataMap.get("type");

            switch (type){
                case "add":
                    this.add();
                    break;
                case "add2":
                    this.add2();
                    break;
                default:
            }

        }


    }


    /**
     * 添加测试数据
     * @return
     */
    public Result add(){

        log.info("添加测试数据开始，"+ DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:SS"));
        long startDate = System.currentTimeMillis();

        AtomicInteger count = new AtomicInteger();

        try {

            IntStream.range(0,20).forEach((i)->{
                threadPoolExecutor.execute(()->{

                    IntStream.range(0,20000).forEach((j)->{
                        boolean b = this.inseart(j);
                        if (b){
                            count.getAndIncrement();
                        }
                    });

                });
            });


            int activeCount = -1;
            int queueSize = -1;
            while (true){
                TimeUnit.MICROSECONDS.sleep(6000);
                //获取活动的线程
                activeCount = threadPoolExecutor.getActiveCount();
                //获取线程等待中的队列
                queueSize = threadPoolExecutor.getQueue().size();
                /*System.err.println("当前活动线程数："+activeCount);
                System.err.println("当前队列等待数："+queueSize);*/
                if(activeCount==0 && queueSize==0){
                    break;
                }
            }

            //非阻塞(空闲线程会停止,非空闲线程标记为停止,等到线程任务执行完了后停止)
            threadPoolExecutor.shutdown();

            //等待线程任务结束 超时结束
            //threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);


        } catch(Exception e) {
            e.printStackTrace();
            return  Result.failed(e.getMessage());
        }

        long endDate = System.currentTimeMillis();
        double time = (double) ((endDate - startDate)/1000d);
        log.info("总运行时间：{} 秒",time);
        log.info("添加测试数据结束 ，"+ DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:SS"));

        return  Result.ok("插入数据成功：count=" + count);
    }

    /**
     * 添加测试数据
     * @return
     */
    public Result add2(){

        log.info("添加测试数据开始2，"+ DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:SS"));
        long startDate = System.currentTimeMillis();

        AtomicInteger count = new AtomicInteger();
        try {

            IntStream.range(0,10000).forEach((j)->{
                boolean b = this.inseart(j);
                if (b){
                    count.getAndIncrement();
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }

        long endDate = System.currentTimeMillis();
        double time = (double) ((endDate - startDate)/1000d);
        log.info("总运行时间：{} 秒",time);
        log.info("添加测试数据结束2 ，"+ DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:SS"));

        return  Result.ok("插入数据成功：count=" + count);
    }

    private boolean inseart(int i){

        boolean flag = false;
        try {
            double random = Math.random();
            Test t = new Test();
            t.setUserCd("administrator"+random);
            t.setUserName("超级管理员"+random);
            t.setUserSearchName("超级管理员"+random);
            t.setLocaleId("zh_CN");
            t.setSex("1");
            t.setCountryCd("中国");
            t.setAddress("江西省吉安市永丰县");
            t.setPhoneNumber("16210789251");
            t.setEmail("river_015@163.com");
            t.setStatus("1");
            t.setSortKey(new BigDecimal(i));
            t.setNotes("测试测试测试测试测试测试测试测");
            t.setContent1("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
            t.setContent2("啦啦啦啦啦啦啦啦啦啦啦啦阿拉啦啦啦啦阿拉蕾");
            t.setContent3("还大号好的好的换货单号的哈哈哈打电话");
            t.setContent4("wejqrjqklwejrlqkwerjklkasdfjlaksjdflkajdsfklajdflk");
            t.setContent5("可可可可可可可可可可可可可可可可可可可可可可可可");
            t.setContent6("夜夜夜夜夜夜夜夜夜夜夜夜夜夜卡萨丁复活卡士大夫");
            t.setContent7("积极急急急急急急急急急急急急急急急急急急急急急急急急急急");

            flag = iTestService.save(t);
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;

    }

    private void initThreadPoolExecutor(){

        log.info("线程池初始化");
        //cup内核数
        int cup = Runtime.getRuntime().availableProcessors();

        ArrayBlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<Runnable>(100);

        //拒绝策略: 默认 直接抛出RejectedExcutionException异常，阻止系统正常运行
        ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        threadPoolExecutor = new ThreadPoolExecutor(
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


}
