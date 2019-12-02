package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.CouponSpuCategoryRelationEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 优惠券分类关联
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:14:37
 */
public interface CouponSpuCategoryRelationService extends IService<CouponSpuCategoryRelationEntity> {

    PageVo queryPage(QueryCondition params);
}

