package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 530
 * @date 2019/12/14
 */
@Component
@Aspect
public class CacheAspect {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private RedissonClient redissonClient;

  /**
   * 1.返回值object
   * 2.参数proceedingJoinPoint
   * 3.抛出异常Throwable
   * 4.proceedingJoinPoint.proceed(args)执行业务方法
   */
  @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache)")
  public Object cacheAround(ProceedingJoinPoint joinPoint) throws Throwable {
    //声明一个结果
    Object result = null;
    //获取连接点的签名  ===>有方法名和形参列表
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    //获取连接点的注解信息
    GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);
    //获取前缀
    String prefix = annotation.prefix();
    //获取方法参数列表
    Object[] args = joinPoint.getArgs();
    //组装成key
    String key = prefix + Arrays.asList(args).toString();
    //获取时间
    int timeout = annotation.timeout();
    int random = annotation.random();
    //引用封装方法
    result = cacheHit(signature, key);
    //如果result不是空的就直接返回
    if (result != null) {
      return result;
    }
    //是空的 就要从数据库中获取
    //初始化锁 并 加上锁
    RLock lock = this.redissonClient.getLock("lock");
    lock.lock();
    //在加锁的时候可能已经有其他线程返回缓存了 需要再判断
    if (result != null) {
      //释放锁并返回
      lock.unlock();
      return result;
    }
    //从数据库中获取
    result = joinPoint.proceed(args);
    //并存入缓存
    this.redisTemplate.opsForValue()
        .set(key, JSON.toJSONString(result), timeout + (int) Math.random() * random,
            TimeUnit.MINUTES);
    //释放锁
    lock.unlock();
    return result;
  }

  /**
   * 查询缓存
   */
  private Object cacheHit(MethodSignature signature, String key) {
    //查询缓存
    String cache = this.redisTemplate.opsForValue().get(key);
    //判断是否有缓存
    if (StringUtils.isNotBlank(cache)) {
      //不是空的 反序列化 不知道具体类型 先获取返回值类型
      Class returnType = signature.getReturnType();
      //反序列化后返回
      return JSON.parseObject(cache, returnType);
    }
    return null;
  }
}
