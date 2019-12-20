package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 530
 * @date 2019/12/15
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {

}
