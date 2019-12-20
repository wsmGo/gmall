package com.atguigu.gmall.index.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 530
 * @date 2019/12/13
 */
@Configuration
public class RedissonConfig {

  @Bean
  public RedissonClient redissonClient(){
    Config config = new Config();
      config.useSingleServer().setAddress("redis://192.168.229.129:6379");
    return Redisson.create(config);
  }
}
