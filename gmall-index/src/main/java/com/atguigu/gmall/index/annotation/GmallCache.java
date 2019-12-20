package com.atguigu.gmall.index.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.zip.DeflaterOutputStream;

/**
 * 自定义缓存切面注解
 * @author 530
 * @date 2019/12/14
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {

  /**
   * 定义一个前缀
   */
  String prefix() default "";

  /**
   * 默认过期时间
   * @return
   */
  int timeout() default 7200;

  /**
   * 随机时间
   * @return
   */
  int random() default 100;

}
