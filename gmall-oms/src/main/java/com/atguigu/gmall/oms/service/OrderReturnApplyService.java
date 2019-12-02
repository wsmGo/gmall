package com.atguigu.gmall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.oms.entity.OrderReturnApplyEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 订单退货申请
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:10:03
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageVo queryPage(QueryCondition params);
}

