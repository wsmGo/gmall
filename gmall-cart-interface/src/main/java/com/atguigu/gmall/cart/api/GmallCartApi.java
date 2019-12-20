package com.atguigu.gmall.cart.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.pojo.Cart;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 530
 * @date 2019/12/18
 */
public interface GmallCartApi {
  @GetMapping("cart/{userId}")
  public Resp<List<Cart>> queryCheckCartByUserId(@PathVariable("userId") Long userId);
}
