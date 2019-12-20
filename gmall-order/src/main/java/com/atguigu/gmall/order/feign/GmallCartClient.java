package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cart.api.GmallCartApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 530
 * @date 2019/12/18
 */
@FeignClient("cart-service")
public interface GmallCartClient extends GmallCartApi {

}
