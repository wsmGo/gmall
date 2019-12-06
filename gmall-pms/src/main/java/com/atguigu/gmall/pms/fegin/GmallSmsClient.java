package com.atguigu.gmall.pms.fegin;

import api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 530
 * @date 2019/12/5
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
