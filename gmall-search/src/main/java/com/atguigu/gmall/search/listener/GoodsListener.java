package com.atguigu.gmall.search.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttr;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author 530
 * @date 2019/12/12
 */
@Component
public class GoodsListener {

  @Autowired
  private GmallPmsClient pmsClient;

  @Autowired
  private GmallWmsClient wmsClient;

  @Autowired
  private GoodsRepository goodsRepository;

  @RabbitListener(bindings = @QueueBinding(
      value = @Queue(value = "Search-queue",durable = "true"),
      exchange = @Exchange(value = "PMS-EXCHANGE" ,type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions = "true"),
      key = {"item.insert","item.update"}
  ))
  public void listener(Long spuId){
    Resp<List<SkuInfoEntity>> skuResp = this.pmsClient.querySkInfo(spuId);
    List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
    if (!CollectionUtils.isEmpty(skuInfoEntities)) {
      //把sku转化成goods对象
      List<Goods> goodsList = skuInfoEntities.stream().map(skuInfoEntity -> {
        Goods goods = new Goods();
        goods.setSkuId(skuInfoEntity.getSkuId());
        //做到了向goods添加数据 搜索属性
        Resp<List<ProductAttrValueEntity>> attrsResp = this.pmsClient
            .queryAttrvBySpuId(spuId);
        //得到规格参数 需要将规格参数转换成SearchAttr集合
        List<ProductAttrValueEntity> attrValueEntities = attrsResp.getData();
        if (!CollectionUtils.isEmpty(attrValueEntities)) {
          List<SearchAttr> searchAttrList = attrValueEntities.stream()
              .map(productAttrValueEntity -> {
                //通过stream重新赋值
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(productAttrValueEntity.getAttrId());
                searchAttr.setAttrName(productAttrValueEntity.getAttrName());
                searchAttr.setAttrValue(productAttrValueEntity.getAttrValue());
                return searchAttr;
              }).collect(Collectors.toList());
          goods.setAttrs(searchAttrList);
          //查询品牌
          Resp<BrandEntity> brandEntityResp = this.pmsClient
              .queryByBrandId(skuInfoEntity.getBrandId());
          BrandEntity brandEntity = brandEntityResp.getData();
          //判断 不空才存
          if (brandEntity != null) {
            goods.setBrandId(skuInfoEntity.getBrandId());
            goods.setBrandName(brandEntity.getName());
          }
          //查询分类
          Resp<CategoryEntity> categoryEntityResp = this.pmsClient
              .queryByCatId(skuInfoEntity.getCatalogId());
          CategoryEntity categoryEntity = categoryEntityResp.getData();
          if (categoryEntity != null) {
            goods.setCategoryId(skuInfoEntity.getCatalogId());
            goods.setCategoryName(categoryEntity.getName());
          }
          //默认图片地址
          goods.setPic(skuInfoEntity.getSkuDefaultImg());
          //上新时间
          Resp<SpuInfoEntity> spuInfoEntityResp = this.pmsClient.querySpuById(spuId);
          SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
          goods.setCreateTime(spuInfoEntity.getCreateTime());
          //价格
          goods.setPrice(skuInfoEntity.getPrice().doubleValue());
          goods.setSale(0L);//销量没设置
          //获取是否有库存
          Resp<List<WareSkuEntity>> wareSkuResp = this.wmsClient
              .queryWareSkuInfo(skuInfoEntity.getSkuId());
          List<WareSkuEntity> wareSkuEntityList = wareSkuResp.getData();
          //anyMatch 取一个
          boolean flag = wareSkuEntityList.stream()
              .anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
          goods.setStore(flag);
          goods.setTitle(skuInfoEntity.getSkuTitle());
        }
        return goods;
      }).collect(Collectors.toList());
      //批量加入到索引库
      this.goodsRepository.saveAll(goodsList);
    }


  }
}
