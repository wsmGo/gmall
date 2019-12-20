package com.atguigu.gmall.order.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.OrderSubmitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 530
 * @date 2019/12/18
 */
@RestController
@RequestMapping("order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  /**
   * 购物车订单
   * @return
   */
  @GetMapping("confirm")
  public Resp<OrderConfirmVO> confirm() {
    OrderConfirmVO confirmVO =  this.orderService.confirm();
    return Resp.ok(confirmVO);
  }

  @PostMapping("submit")
  public  Resp<Object> submit(@RequestBody OrderSubmitVO orderSubmitVO){
    this.orderService.submit(orderSubmitVO);
    return Resp.ok(null);
  }
}
