package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gmall.pms.fegin.GmallSmsClient;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.service.SpuInfoDescService;
import com.atguigu.gmall.pms.vo.ProductAttrVo;
import com.atguigu.gmall.pms.vo.SkuInfoVo;
import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import io.seata.spring.annotation.GlobalTransactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements
    SpuInfoService {

  @Autowired
  private ProductAttrValueService productAttrValueService;

  @Autowired
  private SkuInfoDao skuInfoDao;

  @Autowired
  private SkuImagesService skuImagesService;

  @Autowired
  private AttrDao attrDao;

  @Autowired
  private SkuSaleAttrValueService skuSaleAttrValueService;

  @Autowired
  private GmallSmsClient gmallSmsClient;

  @Autowired
  private SpuInfoDescService spuInfoDescService;

  @Autowired
  private AmqpTemplate amqpTemplate;

  @Value("${item.rabbitmq.exchange}")
  private String GMALLPMS_EXCHANGE;


  @Override
  public PageVo queryPage(QueryCondition params) {
    IPage<SpuInfoEntity> page = this.page(
        new Query<SpuInfoEntity>().getPage(params),
        new QueryWrapper<SpuInfoEntity>()
    );
    return new PageVo(page);
  }

  @Override
  public PageVo querySpuInfo(QueryCondition condition, Long catId) {
    //分装分页条件
    IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(condition);
    //分装查询条件
    QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<SpuInfoEntity>();

    //判断是否全部查询 如果不为0 则查询则根据catid查询
    if (catId != 0) {
      wrapper.eq("catalog_id", catId);
    }
    //为0 查询所有 如果用户输入了检索条件则要添加对应的条件
    ////通用分页参数key就是spuid,spuname的检索条件
    String key = condition.getKey();
    if (!StringUtils.isBlank(key)) {
      wrapper.and(t -> t.like("spu_name", key).or().eq("id", key));
    }
    return new PageVo(this.page(page, wrapper));
  }

  @Override
  @GlobalTransactional
  public void bigSave(SpuInfoVo spuInfoVo) {
    //spu相关信息
    //1.pms_spu_info 默认上架 添加创建时间和更新时间 并获取spuid
    Long spuId = saveSpuInfo(spuInfoVo);
    //2..pms_spu_info_desc 描述就是图片的字符串形式
    this.spuInfoDescService.saveSpuInfoDesc(spuInfoVo, spuId);
    //3.pms_product_attr_value
    this.saveProductAttrValue(spuInfoVo, spuId);
    //4.保存sku信息
    this.saveSkuInfo(spuInfoVo, spuId);
    this.sendMsg("insert", spuId);
    // int i = 1/0;
  }

  private void sendMsg(String type, Long spuId) {
    this.amqpTemplate.convertAndSend(GMALLPMS_EXCHANGE, "item." + type, spuId);
  }


  private void saveSkuInfo(SpuInfoVo spuInfoVo, Long spuId) {
    //1.先判断sku是否为空
    List<SkuInfoVo> skus = spuInfoVo.getSkus();
    //判断是否为空 不为空继续遍历保存
    if (CollectionUtils.isEmpty(skus)) {
      return;
    }
    skus.forEach(skuInfoVo -> {
      skuInfoVo.setSpuId(spuId);
      skuInfoVo.setBrandId(spuInfoVo.getBrandId());
      skuInfoVo.setSkuCode(UUID.randomUUID().toString());//生成二维码
      skuInfoVo.setCatalogId(spuInfoVo.getCatalogId());
      List<String> images = skuInfoVo.getImages();
      //先看有没有图片
      if (!CollectionUtils.isEmpty(images)) {
        //判断默认图片是否为空 不为空设置默认图片 为空就是图片集合第一张为默认图片
        skuInfoVo.setSkuDefaultImg(
            StringUtils.isNotBlank(skuInfoVo.getSkuDefaultImg()) ? skuInfoVo.getSkuDefaultImg()
                : images.get(0));
      }
      this.skuInfoDao.insert(skuInfoVo);
      Long skuId = skuInfoVo.getSkuId();

      //2.pms_sku_images
      //判断是否有图片 转换集合对象 存入supid,imgUrl,DefaultImg(根据地址判断) 批量保存
      if (!CollectionUtils.isEmpty(images)) {
        List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
          SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
          skuImagesEntity.setImgUrl(image);
          skuImagesEntity.setSkuId(skuId);
          skuImagesEntity
              .setDefaultImg(StringUtils.equals(skuInfoVo.getSkuDefaultImg(), image) ? 1 : 0);
          return skuImagesEntity;
        }).collect(Collectors.toList());
        this.skuImagesService.saveBatch(skuImagesEntities);
      }
      //3.pms_sku_sale_attr_value判断是否为空 不空 批量保存
      List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVo.getSaleAttrs();
      if (!CollectionUtils.isEmpty(saleAttrs)) {
        saleAttrs.forEach(skuSaleAttrValueEntity -> {
          skuSaleAttrValueEntity.setSkuId(skuId);
        });
        this.skuSaleAttrValueService.saveBatch(saleAttrs);
      }
      //保存营销信息  远程调用
      //拷贝参数 加上skuid 传入参数
      SkuSaleVO skuSaleVO = new SkuSaleVO();
      BeanUtils.copyProperties(skuInfoVo, skuSaleVO);
      skuSaleVO.setSkuId(skuId);
      gmallSmsClient.saveSkuSale(skuSaleVO);

    });
  }

  private void saveProductAttrValue(SpuInfoVo spuInfoVo, Long spuId) {
    //3.pms_product_attr_value
    List<ProductAttrVo> baseAttrs = spuInfoVo.getBaseAttrs();
    //判断是否有规格 有则强转并保存 并加上spuid
    if (!CollectionUtils.isEmpty(baseAttrs)) {
      List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream()
          .map(productAttrVo -> {
                ProductAttrValueEntity productAttrValueEntity = productAttrVo;
                productAttrValueEntity.setSpuId(spuId);
                return productAttrValueEntity;
              }
          ).collect(Collectors.toList());
      productAttrValueService.saveBatch(productAttrValueEntities);
    }
  }


  private Long saveSpuInfo(SpuInfoVo spuInfoVo) {
    //1.pms_spu_info 默认上架 添加创建时间和更新时间 并获取spuid
    spuInfoVo.setPublishStatus(1);
    spuInfoVo.setCreateTime(new Date());
    spuInfoVo.setUodateTime(spuInfoVo.getCreateTime());
    this.save(spuInfoVo);
    return spuInfoVo.getId();
  }
}

