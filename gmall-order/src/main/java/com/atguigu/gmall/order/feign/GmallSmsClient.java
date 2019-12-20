package com.atguigu.gmall.order.feign;

import api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 530
 * @date 2019/12/15
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {

}
