package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 530
 * @date 2019/12/18
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {

}
