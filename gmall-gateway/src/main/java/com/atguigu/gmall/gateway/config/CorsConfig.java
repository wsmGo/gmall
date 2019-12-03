package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author 530
 * @date 2019/12/3
 */
@Configuration
public class CorsConfig {

    @Bean
  public CorsWebFilter corsWebFilter(){
    //初始化cors配置镜像
      CorsConfiguration corsConfiguration = new CorsConfiguration();
      //允许的域 不能写* 否则无法用cookie
      corsConfiguration.addAllowedOrigin("http://localhost:1000");
      corsConfiguration.addAllowedOrigin("http://127.0.0.1:1000");
      //允许的头信息
      corsConfiguration.addAllowedHeader("*");
      //允许的请求方式
      corsConfiguration.addAllowedMethod("*");
      //是否允许携带cookie
      corsConfiguration.setAllowCredentials(true);
      //添加映射,拦截一切请求
      UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
      configurationSource.registerCorsConfiguration("/**", corsConfiguration);

      return new CorsWebFilter(configurationSource);
    }

}
