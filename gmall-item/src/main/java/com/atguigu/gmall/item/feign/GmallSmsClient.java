package com.atguigu.gmall.item.feign;

import api.GmallSmsApi;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 530
 * @date 2019/12/15
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {

}
