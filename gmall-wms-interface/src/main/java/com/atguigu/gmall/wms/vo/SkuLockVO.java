package com.atguigu.gmall.wms.vo;

import lombok.Data;

/**
 * @author 530
 * @date 2019/12/19
 */
@Data
public class SkuLockVO {

  private Long skuId;
  private Integer count;
  private Long wareSkuId;
  private Boolean lockStore;

}
