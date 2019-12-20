package com.atguigu.gmall.cart.pojo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.SaleVO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/17
 */
@Data
public class Cart {
  private Long skuId;
  private String title;
  private String defaultImage;
  private BigDecimal price;
  private BigDecimal currentPrice; //当前价格
  private Integer count;
  private Boolean store;
  private List<SkuSaleAttrValueEntity> saleAttrValues;
  private List<SaleVO> sales;
  private Boolean check;
}
