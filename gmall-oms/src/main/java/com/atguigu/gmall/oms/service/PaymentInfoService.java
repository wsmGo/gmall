package com.atguigu.gmall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.oms.entity.PaymentInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 支付信息表
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:10:03
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageVo queryPage(QueryCondition params);
}

