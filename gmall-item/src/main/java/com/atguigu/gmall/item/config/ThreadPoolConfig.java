package com.atguigu.gmall.item.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 530
 * @date 2019/12/16
 */
@Configuration
public class ThreadPoolConfig {
  @Bean
  public ThreadPoolExecutor threadPoolExecutor(){
    return new ThreadPoolExecutor(50, 500, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
  }
}
