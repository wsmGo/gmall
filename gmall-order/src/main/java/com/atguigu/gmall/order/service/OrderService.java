package com.atguigu.gmall.order.service;

import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.OrderSubmitVO;

/**
 * @author 530
 * @date 2019/12/18
 */
public interface OrderService {

  OrderConfirmVO confirm();

  void submit(OrderSubmitVO orderSubmitVO);
}
