package com.atguigu.gmall.sms.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/5
 */
@Data
public class SkuSaleVO {

  //sms_spu_bounds 积分表字段
  private BigDecimal growBounds;
  /**
   * 购物积分
   */
  private BigDecimal buyBounds;
  /**
   * 优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
   */
  private List<Integer> work;
  //sms_sku_ladder 打折字段
  private Integer fullCount;
  /**
   * 打几折
   */
  private BigDecimal discount;
  /**
   * 是否叠加其他优惠[0-不可叠加，1-可叠加]
   */
  private Integer ladderAddOther;
  //sms_sku_full_reduction优惠
  private BigDecimal fullPrice;
  /**
   * 减多少
   */
  private BigDecimal reducePrice;
  /**
   * 是否参与其他优惠
   */
  private Integer fullAddOther;
  //skuid
  private Long skuId;

}
