package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.vo.BaseGroupVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 商品详情页
 *
 * @author 530
 * @date 2019/12/14
 */
@Data
public class ItemVO {
  //1、当前sku的基本信息
  private Long skuId;
  private Long spuId;
  private CategoryEntity categoryEntity;
  private BrandEntity brandEntity;
  private String spuName;
  //标题
  private String skuTitle;
  //副标题
  private String subTitle;
  //价格
  private BigDecimal price;
  //重量
  private BigDecimal weight;
  //是否有货
  private Boolean store;
  //spu的海报
  private List<String> images;
  //2、sku的所有图片
  private List<SkuImagesEntity> pics;
  //3、sku的所有营销信息
  private List<SaleVO> sales;
  //4、sku的所有销售属性组合
  private List<SkuSaleAttrValueEntity> saleAttrs;
  //5、spu的所有基本属性
  private List<BaseGroupVO> attrGroups;
}
