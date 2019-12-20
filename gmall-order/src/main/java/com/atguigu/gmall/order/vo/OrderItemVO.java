package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/18
 */
@Data
public class OrderItemVO {

  private Long skuId;// 商品id
  private String title;// 标题
  private String defaultImage;// 图片
  private BigDecimal price;// 加入购物车时的价格
  private Integer count;// 购买数量
  private List<SkuSaleAttrValueEntity> skuAttrValues;// 商品规格参数
  private List<SaleVO> sales;
  private  BigDecimal weight;
  private  Boolean store;

}
