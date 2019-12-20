package com.atguigu.gmall.cart.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 530
 * @date 2019/12/17
 */
@RestController
@RequestMapping("cart")
public class CartController {


  @Autowired
  private CartService cartService;

  /**
   * 根据用户id查询已选中的购物车
   * @param userId
   * @return
   */
  @GetMapping("{userId}")
  public Resp<List<Cart>> queryCheckCartByUserId(@PathVariable("userId") Long userId){
    List<Cart> carts =   this.cartService.queryCheckCartByUserId(userId);
    return Resp.ok(carts);
  }
  /**
   * 新增购物车
   */
  @PostMapping
  public Resp<Object> addCart(@RequestBody Cart cart) {
    this.cartService.addCart(cart);
    return Resp.ok(null);
  }

  /**
   * 查询购物车
   */
  @GetMapping
  public Resp<List<Cart>> queryCarts() {
    List<Cart> carts = this.cartService.queryCarts();
    return Resp.ok(carts);
  }

  /**
   * 更新购物车中的数量
   */
  @PostMapping("update")
  public Resp<Object> updateCart(@RequestBody Cart cart) {
    this.cartService.updateCart(cart);
    return Resp.ok(null);
  }

  /**
   * 删除购物车
   * @param skuId
   * @return
   */
  @PostMapping("{skuId}")
  public Resp<Object> deleteCart(@PathVariable("skuId") Long skuId) {
    this.cartService.deleteCart(skuId);
    return Resp.ok(null);
  }

//  @GetMapping
//  public String test(){
//
//    UserInfo userInfo = LoginInterceptor.getUserInfo();
//
//    return "hello";
//  }
}
