package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/19
 */
@Data
public class OrderSubmitVO {
  private String orderToken;//防重
  private MemberReceiveAddressEntity address;
  private Integer payType; //支付方式
  private String deliveryCompany;//配送公司
  private List<OrderItemVO> items;//购物清单
  private Integer bounds;//积分
  private BigDecimal totalPrice;//总价
}
