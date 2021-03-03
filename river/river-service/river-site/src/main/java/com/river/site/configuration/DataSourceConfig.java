package com.river.site.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 多数据源配置
 */
@Configuration
public class DataSourceConfig {

    /**
     * 主数据源配置 site数据源
     */
    @Primary //默认优先选择 同时不可以同时设置多个
    @Bean(name = "siteDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.site")
    public DataSourceProperties siteDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 主数据源 site数据源
     */
    @Primary //默认优先选择 同时不可以同时设置多个
    @Bean(name = "siteDataSource")
    public DataSource siteDataSource(@Qualifier("siteDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /**
     * quartz 数据源配置
     * @return
     */
    @Bean(name = "quartzDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.quartz")
    public DataSourceProperties quartzDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * quartz 数据源
     * @param dataSourceProperties
     * @return
     */
    @Bean("quartzDataSource")
    public DataSource quartzDataSource(@Qualifier("quartzDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

}