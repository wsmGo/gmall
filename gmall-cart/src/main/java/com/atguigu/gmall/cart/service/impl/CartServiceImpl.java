package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.feign.GmallWmsClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author 530
 * @date 2019/12/17
 */
@Service
public class CartServiceImpl implements CartService {

  @Autowired
  private StringRedisTemplate redisTemplate;
  @Autowired
  private GmallPmsClient pmsClient;
  @Autowired
  private GmallWmsClient wmsClient;
  @Autowired
  private GmallSmsClient smsClient;

  private static final String KEY_PRIEFIX = "gmall:cart:";

    private static final String PRICE_PRIEFIX = "cart:price:";

  /**
   * 新增
   * @param cart
   */
  @Override
  public void addCart(Cart cart) {
    String key = getLoginState();
//    3>获取购物车
    BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
    String skuId = cart.getSkuId().toString();
    //得到购物车内的商品数
    Integer count = cart.getCount();
//    4>判断是否有购物车记录
    if (hashOps.hasKey(skuId)) {
//    5>有则更新
      //获取相对应的购物车的商品的json字符串
      String cartJson = hashOps.get(skuId).toString();
      //反序列化成cart对象
      cart = JSON.parseObject(cartJson, Cart.class);
      cart.setCount(cart.getCount() + count);
    } else {
//    6>没有则新增(一些属性远程调用设置).
      Resp<SkuInfoEntity> skuInResp = this.pmsClient.querySkuInfoBySkuId(cart.getSkuId());
      //sku的信息
      SkuInfoEntity skuInfoEntity = skuInResp.getData();
      if (skuInfoEntity == null) {
        return;
      }
      cart.setTitle(skuInfoEntity.getSkuTitle());
      cart.setPrice(skuInfoEntity.getPrice());
      cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
      //商品属性
      Resp<List<SkuSaleAttrValueEntity>> attrResp = this.pmsClient.querySaleAttrsByskuId(cart.getSkuId());
      List<SkuSaleAttrValueEntity> attrValueEntityList = attrResp.getData();
      cart.setSaleAttrValues(attrValueEntityList);
      //是否有货
      Resp<List<WareSkuEntity>> wareResp = this.wmsClient.queryWareSkuInfo(cart.getSkuId());
      List<WareSkuEntity> wareSkuEntities = wareResp.getData();
      if (!CollectionUtils.isEmpty(wareSkuEntities)) {
        cart.setStore(
            wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
      }
      cart.setCheck(true);
      //营销信息
      Resp<List<SaleVO>> saleResp = this.smsClient.querySalsVosBySkuid(cart.getSkuId());
      List<SaleVO> saleVOs = saleResp.getData();
      cart.setSales(saleVOs);
      //新增购物车时 需要额外保存一份购物车的价格 用来更新价格时对比
      this.redisTemplate.opsForValue().set(PRICE_PRIEFIX +skuId, skuInfoEntity.getPrice().toString());
    }
    // 序列化放入redis
    hashOps.put(skuId, JSON.toJSONString(cart));
  }

  /***
   * 获取登录状态
   * @return
   */
  private String getLoginState() {
    String key = KEY_PRIEFIX;
//    1>获取登录状态
    UserInfo userInfo = LoginInterceptor.getUserInfo();
//    2>判断是否有登入
    if (userInfo.getId() != null) {
      key += userInfo.getId();
    } else {
      key += userInfo.getUserKey();
    }
    return key;
  }

  @Override
  public List<Cart> queryCarts() {
//    1>获取登录状态
    UserInfo userInfo = LoginInterceptor.getUserInfo();
//    2>查询未登录的购物车
    String unLoginKey = KEY_PRIEFIX + userInfo.getUserKey();
    BoundHashOperations<String, Object, Object> unLoginHashOps = this.redisTemplate
        .boundHashOps(unLoginKey);
    List<Object> cartJsonList = unLoginHashOps.values();
    List<Cart> unLoginCarts = null;
    if (!CollectionUtils.isEmpty(cartJsonList)) {
      //反序列化
      unLoginCarts = cartJsonList.stream().map(cartjson -> {
            Cart cart = JSON.parseObject(cartjson.toString(), Cart.class);
            //查询最新的价格 放入cart对象中
            String price = this.redisTemplate.opsForValue().get(PRICE_PRIEFIX + cart.getSkuId());
            cart.setCurrentPrice(new BigDecimal(price));
            return  cart;
          }).collect(Collectors.toList());
    }
//    3>判断是否登入 未登录直接返回
    if (userInfo.getId() == null) {
      return unLoginCarts;
    }
//    4>登录就合并购物车===>同步完后要删除未登录的购物车
    String loginKey = KEY_PRIEFIX + userInfo.getId();
    BoundHashOperations<String, Object, Object> loginHashOps = this.redisTemplate.boundHashOps(loginKey);
    //再判断未登录的还有没有 有的话遍历添加
    if (!CollectionUtils.isEmpty(unLoginCarts)) {
      unLoginCarts.forEach(cart -> {
        Integer count = cart.getCount();
        if (loginHashOps.hasKey(cart.getSkuId().toString())) {
          String cartJson = loginHashOps.get(cart.getSkuId().toString()).toString();
          //反序列化
          cart = JSON.parseObject(cartJson, Cart.class);
          cart.setCount(cart.getCount() + count);
        }
        loginHashOps.put(cart.getSkuId().toString(), JSON.toJSONString(cart));
      });
      //删除未登录状态的购物车
      this.redisTemplate.delete(unLoginKey);
    }

//    5>查询登录状态的购物车
    List<Object> loginCartJson = loginHashOps.values();
    return loginCartJson.stream().map(cartjson -> {
      Cart cart = JSON.parseObject(cartjson.toString(), Cart.class);
      //获取新的价格存入对象
      String price = this.redisTemplate.opsForValue().get(PRICE_PRIEFIX + cart.getSkuId());
      cart.setCurrentPrice(new BigDecimal(price));
      return cart;
    }).collect(Collectors.toList());

  }

  @Override
  public void updateCart(Cart cart) {
    String key = this.getLoginState();
    BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
    //判断是否有这个购物车
    Integer count = cart.getCount();
    if (hashOps.hasKey(cart.getSkuId().toString())) {
      //如果有就更新
      String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
      cart = JSON.parseObject(cartJson, Cart.class);
      cart.setCount(count);
      //存进去
      hashOps.put(cart.getSkuId().toString(), JSON.toJSONString(cart));
    }
  }

  @Override
  public void deleteCart(Long skuId) {
    String key = this.getLoginState();
    BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate
        .boundHashOps(key);
    boundHashOps.delete(skuId.toString());
  }

  @Override
  public List<Cart> queryCheckCartByUserId(Long userId) {
    BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PRIEFIX + userId);
    List<Object> cartJsonList = hashOps.values();
    //反序列化 并过滤已选中
    return cartJsonList.stream().map(cartJson -> JSON.parseObject(cartJson.toString(),Cart.class))
        .filter(Cart::getCheck).collect(Collectors.toList());
  }
}
