package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 530
 * @date 2019/12/15
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
