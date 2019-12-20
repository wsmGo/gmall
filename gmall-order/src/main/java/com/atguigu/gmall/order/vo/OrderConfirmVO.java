package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/18
 */
@Data
public class OrderConfirmVO {
  private  List<MemberReceiveAddressEntity> addressEntities; //用户地址信息
  private List<OrderItemVO> orderItems; //购物清单，根据购物车页面传递过来的skuIds查询
  // 可用积分，ums_member表中的integration字段
  private Integer bounds;
  // 订单令牌，防止重复提交
  private String orderToken;
}
