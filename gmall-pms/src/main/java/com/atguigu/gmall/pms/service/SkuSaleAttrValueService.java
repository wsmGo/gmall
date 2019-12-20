package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import java.util.List;


/**
 * sku销售属性&值
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 18:23:41
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageVo queryPage(QueryCondition params);

  List<SkuSaleAttrValueEntity> querySaleAttrsBySpuId(Long spuId);
}

