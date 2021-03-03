package com.river.site.quartz;

import com.river.api.entity.site.QuartzJobLogs;
import com.river.site.service.impl.QuartzJobLogsServiceImpl;
import com.zaxxer.hikari.HikariDataSource;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author river
 */

@Configuration
public class SchedulerConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    @Qualifier("quartzDataSource")
    private DataSource quartzDataSource;

    @Autowired
    private JobFactory jobFactory;

    @Autowired
    private Environment env;

    @Autowired
    private JobLogsListener jobLogsListener;

    /**
     * 当应用启动,或刷新时，启动我们的定时任务
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("应用重启或刷新启动定时任务..."+event.getSource());
    }

    /**
     * quartz 配置
     *
     * @return
     * @throws IOException
     */
    private Properties quartzProperties() throws IOException {
        Properties prop = new Properties();
        // 调度标识名 集群中每一个实例都必须使用相同的名称
        prop.put("quartz.scheduler.instanceName", "quartzInstanceName");
        // ID设置为自动获取 每一个必须不同
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        // 禁用quartz软件更新
        prop.put("org.quartz.scheduler.skipUpdateCheck", "true");
        prop.put("org.quartz.scheduler.jmx.export", "true");


        // 数据库代理类，一般org.quartz.impl.jdbcjobstore.StdJDBCDelegate可以满足大部分数据库
        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        // 数据保存方式为数据库持久化
        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        // 数据库别名 随便取
        prop.put("org.quartz.jobStore.dataSource", "quartzDataSource");
        // 表的前缀，默认QRTZ_
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        // 是否加入集群
        prop.put("org.quartz.jobStore.isClustered", "true");

        // 调度实例失效的检查时间间隔
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
        // 信息保存时间 ms 默认值60秒
        prop.put("org.quartz.jobStore.misfireThreshold", "120000");
        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");
        prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS WHERE LOCK_NAME = ? FOR UPDATE");

        // 程池的实现类（一般使用SimpleThreadPool即可满足几乎所有用户的需求）
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        // 定线程数，至少为1（无默认值）(一般设置为1-100之间的整数合适)
        prop.put("org.quartz.threadPool.threadCount", "20");
        // 设置线程的优先级（最大为java.lang.Thread.MAX_PRIORITY 10，最小为Thread.MIN_PRIORITY 1，默认为5）
        prop.put("org.quartz.threadPool.threadPriority", "5");
        prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        prop.put("org.quartz.plugin.triggHistory.class", "org.quartz.plugins.history.LoggingJobHistoryPlugin");
        prop.put("org.quartz.plugin.shutdownhook.class", "org.quartz.plugins.management.ShutdownHookPlugin");
        prop.put("org.quartz.plugin.shutdownhook.cleanShutdown", "true");

        //#自定义连接池
        //org.quartz.dataSource.myDS.connectionProvider.class=com.poly.pay.schedule.DruidConnectionProvider

        return prop;
    }

    @Bean
    public JobLogsListener jobLogsListener(QuartzJobLogsServiceImpl quartzJobLogsService) {
        return new JobLogsListener(quartzJobLogsService);
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {

       /* //获取配置属性
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        //在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        Properties quartzProperties = propertiesFactoryBean.getObject();*/

        //创建SchedulerFactoryBean
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setQuartzProperties(quartzProperties());

        //使用数据源，自定义数据源
        factory.setDataSource(quartzDataSource);
        //QuartzScheduler 延时启动，应用启动完1秒后 QuartzScheduler 再启动
        factory.setStartupDelay(1);
        //用于quartz集群,加载quartz数据源配置 自动启动
        factory.setAutoStartup(true);
        //用于quartz集群,QuartzScheduler 覆盖已存在的job
        factory.setOverwriteExistingJobs(true);

        factory.setJobFactory(jobFactory);
        //这样当spring关闭时，会等待所有已经启动的quartz job结束后spring才能完全shutdown。
        factory.setWaitForJobsToCompleteOnShutdown(true);

        // 任务执行日志监听
        factory.setGlobalJobListeners(jobLogsListener);

        return factory;

    }

    /**
     * 数据源
     *
     * @return
     * @throws PropertyVetoException
     */
    /*@Bean(name = "quartzDataSource")
    public HikariDataSource quartzDataSource(){

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getProperty("quartz.datasource.url"));
        dataSource.setDriverClassName(env.getProperty("quartz.datasource.driver-class-name"));
        dataSource.setUsername(env.getProperty("quartz.datasource.username"));
        dataSource.setPassword(env.getProperty("quartz.datasource.password"));
        dataSource.setMaximumPoolSize(Integer.parseInt(env.getProperty("quartz.datasource.maximum-pool-size")));
        return dataSource;

    }*/

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     * @return
     * @throws IOException
     */
    @Bean(name="scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }


    @Bean
    public QuartzInitializerListener executorListener() {
        return new QuartzInitializerListener();
    }

}
