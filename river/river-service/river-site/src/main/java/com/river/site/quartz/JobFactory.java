package com.river.site.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * 创建job 实例工厂，解决spring注入问题，如果使用默认会导致spring的@Autowired 无法注入问题
 * @author river
 */
@Slf4j
@Component
public class JobFactory extends SpringBeanJobFactory {

    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception{

        //调用父类的方法
        Object job = super.createJobInstance(bundle);
        //进行注入
        capableBeanFactory.autowireBean(job);


        log.info("quartz 成功创建JOB实例，并注入autowire依赖对象");
        return job;
    }


}
