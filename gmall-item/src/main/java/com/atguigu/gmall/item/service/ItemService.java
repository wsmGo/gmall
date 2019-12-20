package com.atguigu.gmall.item.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.BaseGroupVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


/**
 * @author 530
 * @date 2019/12/15
 */
@Service
public class ItemService {

  @Autowired
  private GmallPmsClient pmsClient;
  @Autowired
  private GmallSmsClient smsClient;
  @Autowired
  private GmallWmsClient wmsClient;
  @Autowired
  private ThreadPoolExecutor threadPoolExecutor;

  public ItemVO queryItemVo(Long skuId) {
    ItemVO itemVO = new ItemVO();

    itemVO.setSkuId(skuId);
    //采用异步编排方法
    //sku线程
    CompletableFuture<SkuInfoEntity> skuCompletableFuture = CompletableFuture
        .supplyAsync(() -> {
          //根据skuId查询相关信息
          Resp<SkuInfoEntity> skuInfoEntityResp = this.pmsClient.querySkuInfoBySkuId(skuId);
          SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
          if (skuInfoEntity == null) {
            return null;
          }
          itemVO.setSkuTitle(skuInfoEntity.getSkuTitle());
          itemVO.setSubTitle(skuInfoEntity.getSkuSubtitle());
          itemVO.setWeight(skuInfoEntity.getWeight());
          itemVO.setPrice(skuInfoEntity.getPrice());
          //根据spuId查询
          itemVO.setSpuId(skuInfoEntity.getSpuId());
          return skuInfoEntity;
        }, threadPoolExecutor);
    //根据sku线程 接下来的子线程
    CompletableFuture<Void> spuCompletableFuture = skuCompletableFuture.thenAcceptAsync(sku -> {
      Resp<SpuInfoEntity> spuResp = this.pmsClient.querySpuById(sku.getSpuId());
      SpuInfoEntity spuInfoEntity = spuResp.getData();
      if (spuInfoEntity != null) {
        itemVO.setSpuName(spuInfoEntity.getSpuName());
      }
    }, threadPoolExecutor);
    //根据skuid设置图片
    //根据skuid就可以 所以新开辟线程
    CompletableFuture<Void> imageCompletableFuture = CompletableFuture.runAsync(() -> {
      Resp<List<SkuImagesEntity>> imagesResp = this.pmsClient.queryImagesBySkuId(skuId);
      List<SkuImagesEntity> skuImagesEntities = imagesResp.getData();
      if (!CollectionUtils.isEmpty(skuImagesEntities)) {
        itemVO.setPics(skuImagesEntities);
      }
    }, threadPoolExecutor);

    //根据sku中的brandid和cateid查询
    //并行线程
    CompletableFuture<Void> brandCompletableFuture = skuCompletableFuture.thenAcceptAsync(sku -> {
      Resp<BrandEntity> brandResp = this.pmsClient.queryByBrandId(sku.getBrandId());
      BrandEntity brandEntity = brandResp.getData();
      if (brandEntity != null) {
        itemVO.setBrandEntity(brandEntity);
      }
    }, threadPoolExecutor);
    //并行线程
    CompletableFuture<Void> cateCompletableFuture = skuCompletableFuture.thenAcceptAsync(sku -> {
      Resp<CategoryEntity> categoryResp = this.pmsClient.queryByCatId(sku.getCatalogId());
      CategoryEntity categoryEntity = categoryResp.getData();
      if (categoryEntity != null) {
        itemVO.setCategoryEntity(categoryEntity);
      }
    }, threadPoolExecutor);
    //根据spuid设置详情信息
    //并行线程
    CompletableFuture<Void> descCompletableFuture = skuCompletableFuture.thenAcceptAsync(sku -> {
      Resp<SpuInfoDescEntity> spuDescResp = this.pmsClient.querySpuDesc(sku.getSpuId());
      SpuInfoDescEntity spuInfoDescEntity = spuDescResp.getData();
      if (spuInfoDescEntity != null) {
        String[] split = StringUtils.split(spuInfoDescEntity.getDecript(), ",");
        //海报
        itemVO.setImages(Arrays.asList(split));
      }
    }, threadPoolExecutor);

    //根据skuid查询营销信息  三张表
    CompletableFuture<Void> salesCompletableFuture = CompletableFuture.runAsync(() -> {
      Resp<List<SaleVO>> salesResp = this.smsClient.querySalsVosBySkuid(skuId);
      List<SaleVO> saleVOS = salesResp.getData();
      if (!CollectionUtils.isEmpty(saleVOS)) {
        itemVO.setSales(saleVOS);
      }
    }, threadPoolExecutor);

    //库存
    CompletableFuture<Void> wareCompletableFuture = CompletableFuture.runAsync(() -> {
      Resp<List<WareSkuEntity>> wareResp = this.wmsClient.queryWareSkuInfo(skuId);
      List<WareSkuEntity> wareSkuEntities = wareResp.getData();
      itemVO.setStore(
          wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
    }, threadPoolExecutor);

    //根据spuId查所有的skuids 再查询销售属性 这是为了展示同种但是不同属性的 比如 华为 红色,绿色等
    //并行线程
    CompletableFuture<Void> attrsCompletableFuture = skuCompletableFuture.thenAcceptAsync(sku -> {
      Resp<List<SkuSaleAttrValueEntity>> saleAttrsResp = this.pmsClient
          .querySaleAttrsBySpuId(sku.getSpuId());
      List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrsResp.getData();
      if (!CollectionUtils.isEmpty(skuSaleAttrValueEntities)) {
        itemVO.setSaleAttrs(skuSaleAttrValueEntities);
      }
    }, threadPoolExecutor);

    //根据spuid和cateid查询
    //并行线程
    CompletableFuture<Void> groupCompletableFuture = skuCompletableFuture.thenAcceptAsync(sku -> {
      Resp<List<BaseGroupVO>> groupResp = this.pmsClient
          .queryGroupVoByCidAndSpuid(sku.getCatalogId(), sku.getSpuId());
      List<BaseGroupVO> groupVOS = groupResp.getData();
      if (!CollectionUtils.isEmpty(groupVOS)) {
        itemVO.setAttrGroups(groupVOS);
      }
    }, threadPoolExecutor);

    //等全部执行完再返回
    CompletableFuture.allOf(spuCompletableFuture,imageCompletableFuture,brandCompletableFuture,cateCompletableFuture
        ,descCompletableFuture,salesCompletableFuture,wareCompletableFuture,attrsCompletableFuture,groupCompletableFuture).join();
    return itemVO;
  }
}
