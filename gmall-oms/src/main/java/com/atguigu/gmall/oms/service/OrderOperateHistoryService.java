package com.atguigu.gmall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.oms.entity.OrderOperateHistoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 订单操作历史记录
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:10:03
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageVo queryPage(QueryCondition params);
}

