package com.atguigu.gmall.pms.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置
 *
 * @author HelloWoodes
 */
@Configuration
public class DataSourceConfig {

    /**
     *
     * @param jdbcUrl
     * @param username
     * @param password
     * @param driverClassName
     * @return
     */
    @Primary
    @Bean("dataSource")
    public DataSource dataSource(@Value("${spring.datasource.url}") String jdbcUrl,@Value("${spring.datasource.username}") String username,
        @Value("${spring.datasource.password}") String password,  @Value("${spring.datasource.driver-class-name}") String driverClassName) {

        HikariDataSource hikariDataSource =new HikariDataSource();
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setDriverClassName(driverClassName);
        return new DataSourceProxy(hikariDataSource);
    }
}
