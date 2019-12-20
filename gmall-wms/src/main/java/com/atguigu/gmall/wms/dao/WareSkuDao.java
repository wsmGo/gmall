package com.atguigu.gmall.wms.dao;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:26:36
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

  List<WareSkuEntity> checkStore(@Param("skuId") Long skuId,@Param("count") Integer count);

  int lockStore(@Param("id") Long id, @Param("count") Integer count);

  int unLockStore(@Param("wareSkuId") Long wareSkuId, @Param("count") Integer count);
}
