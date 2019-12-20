package com.atguigu.gmall.order.config;

import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 530
 * @date 2019/12/17
 */
@Configuration
public class GmallWebMvcConfig implements WebMvcConfigurer {

  @Autowired
  private LoginInterceptor loginInterceptor;
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
  }
}
