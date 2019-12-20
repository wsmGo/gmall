package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 530
 * @date 2019/12/13
 */
@Service
public class IndexService {

  @Autowired
  private GmallPmsClient gmallPmsClient;

  @Autowired
  private StringRedisTemplate redisTemplate;

  private static final String KEY_CACHE = "index:cates:";

  @Autowired
  private RedissonClient redissonClient;

  public List<CategoryEntity> queryLv1Categories() {
    Resp<List<CategoryEntity>> listResp = this.gmallPmsClient.queryCategoriesByPidOrLev(null, 1);
    return  listResp.getData();
  }


  @GmallCache(prefix = "index:cates:",timeout = 7200,random = 100)
  public List<CategoryVO> querySubCategoriseByPid(Long pid) {

//    //先从缓存中获取数据
//    String categories = this.redisTemplate.opsForValue().get(KEY_CACHE + pid);
//    //判断redis中是否有数据 有就直接返回
//    if(StringUtils.isNoneBlank(categories)){
//      return JSON.parseArray(categories, CategoryVO.class);
//    }
//
//      //获取锁
//    RLock lock = redissonClient.getLock("lock" + pid);
//    lock.lock();
//
//    //再从缓存中获取数据
//    String categories2 = this.redisTemplate.opsForValue().get(KEY_CACHE + pid);
//    //再判断redis中是否有数据 有就直接返回
//    if(StringUtils.isNoneBlank(categories2)){
//      lock.unlock();
//      return JSON.parseArray(categories2, CategoryVO.class);
//    }

    //如果没有数据就从数据库中读取
    Resp<List<CategoryVO>> listResp = this.gmallPmsClient.querySubCategories(pid);
    List<CategoryVO> categoryVOList = listResp.getData();//空也传入 解决穿透
    //读取后存入redis  添加时间解决雪崩 n
//    this.redisTemplate.opsForValue().set(KEY_CACHE+pid, JSON.toJSONString(categoryVOList),7+new Random().nextInt(5),
//        TimeUnit.DAYS);
//    lock.unlock();
    return categoryVOList;

  }
}
