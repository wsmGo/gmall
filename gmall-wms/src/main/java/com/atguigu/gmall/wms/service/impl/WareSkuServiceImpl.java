package com.atguigu.gmall.wms.service.impl;

import com.atguigu.gmall.wms.vo.SkuLockVO;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.ws.Action;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements
    WareSkuService {

  @Autowired
  private RedissonClient redissonClient;
  @Autowired
  private WareSkuDao skuDao;

  @Override
  public PageVo queryPage(QueryCondition params) {
    IPage<WareSkuEntity> page = this.page(
        new Query<WareSkuEntity>().getPage(params),
        new QueryWrapper<WareSkuEntity>()
    );

    return new PageVo(page);
  }

  @Override
  public String checkAndLockStore(List<SkuLockVO> skuLockVOS) {
    if (CollectionUtils.isEmpty(skuLockVOS)) {
      return "没有选中的商品";
    }
    //遍历这个订单集合 (校验库存够不够 并且锁上 )
    skuLockVOS.forEach(skuLockVO -> {
      lockStore(skuLockVO);
    });
    //遍历出没有锁定的库存
    List<SkuLockVO> unLockSku = skuLockVOS.stream().filter(skuLockVO -> skuLockVO.getLockStore() == false).collect(Collectors.toList());
    if(!CollectionUtils.isEmpty(unLockSku)){
      //解锁
      //得到上锁的商品并遍历上锁依次解锁
      List<SkuLockVO> lockVOList = skuLockVOS.stream().filter(SkuLockVO::getLockStore).collect(Collectors.toList());
      lockVOList.forEach(skuLockVO -> {
        this.skuDao.unLockStore(skuLockVO.getWareSkuId(),skuLockVO.getCount());
      });

      List<Long> skuIds = unLockSku.stream().map(SkuLockVO::getSkuId).collect(Collectors.toList());
      return "下单失败,商品库存不足" + skuIds.toString();
    }


    return null;
  }

  //封装校验库存够不够 并且锁上
  private void lockStore(SkuLockVO skuLockVO) {
    //使用分布锁
    //得到锁 锁住当前商品
    RLock lock = this.redissonClient.getLock("store:" + skuLockVO.getSkuId());
    lock.lock();
    //查询库存中的商品够不够
    List<WareSkuEntity> wareSkuEntities = this.skuDao.checkStore(skuLockVO.getSkuId(), skuLockVO.getCount());
    //判断是否有满足要求的库存 有的话上锁
    if(!CollectionUtils.isEmpty(wareSkuEntities)){
      //这里的库是根据大数据选中的库 但是没有大数据 所以传第一个
      Long id = wareSkuEntities.get(0).getId();
      this.skuDao.lockStore(id,skuLockVO.getCount());
      skuLockVO.setLockStore(true);//代表锁住了
      skuLockVO.setWareSkuId(id);
    }else {
      skuLockVO.setLockStore(false);
    }
    lock.unlock();

  }

}