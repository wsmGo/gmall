package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 530
 * @date 2019/12/10
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
