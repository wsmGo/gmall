package com.atguigu.gmall.wms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 530
 * @date 2019/12/10
 */
public interface GmallWmsApi {
  @GetMapping("/wms/waresku/{skuId}")
  public Resp<List<WareSkuEntity>> queryWareSkuInfo(@PathVariable("skuId") Long skuId);
}
