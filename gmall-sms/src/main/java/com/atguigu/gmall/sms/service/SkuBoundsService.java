package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import java.util.List;


/**
 * 商品sku积分设置
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:14:37
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

  void saveSkuSale(SkuSaleVO skuSaleVO);

  List<SaleVO> querySalsVosBySkuid(Long skuId);

}

