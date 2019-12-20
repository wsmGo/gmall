package com.atguigu.gmall.order.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.exception.CartException;
import com.atguigu.gmall.cart.api.GmallCartApi;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.order.feign.GmallCartClient;
import com.atguigu.gmall.order.feign.GmallPmsClient;
import com.atguigu.gmall.order.feign.GmallSmsClient;
import com.atguigu.gmall.order.feign.GmallUmsClient;
import com.atguigu.gmall.order.feign.GmallWmsClient;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.OrderItemVO;
import com.atguigu.gmall.order.vo.OrderSubmitVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.ums.api.GmallUmsApi;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author 530
 * @date 2019/12/18
 */
@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private GmallPmsClient pmsClient;

  @Autowired
  private GmallUmsClient umsClient;

  @Autowired
  private GmallWmsClient wmsClient;

  @Autowired
  private GmallSmsClient smsClient;

  @Autowired
  private GmallCartClient cartClient;

  @Autowired
  private ThreadPoolExecutor threadPoolExecutor;

  private static final String KEY_PRIEFIX = "gmall:cart:";
  private static final String ORDER_PRIEFIX = "order:token:";


  @Override
  public OrderConfirmVO confirm() {
    OrderConfirmVO confirmVO = new OrderConfirmVO();
    //获取登录状态 可以等到userId
    UserInfo userInfo = LoginInterceptor.getUserInfo();
    Long userId = userInfo.getId();
    //判断是否有登录
    if (userId == null) {
      return null;
    }
    //获取购物车中选中的商品信息 ===>补充一个根据memId查询已选择的购物车的方法skuId count
    //大线程
    CompletableFuture<Void> bigCompletableFuture = CompletableFuture.supplyAsync(() -> {
      Resp<List<Cart>> cartResp = this.cartClient.queryCheckCartByUserId(userId);
      List<Cart> cartList = cartResp.getData();
      if (CollectionUtils.isEmpty(cartList)) {
        throw new CartException("请勾选购物车中的商品");
      }
      return cartList;
    }, threadPoolExecutor).thenAcceptAsync(cartList -> {

      List<OrderItemVO> orderItemVOS = cartList.stream().map(cart -> {
        OrderItemVO orderItemVO = new OrderItemVO();
        Long skuId = cart.getSkuId();
        //小线程1
        CompletableFuture<Void> skuCompletableFuture = CompletableFuture.runAsync(() -> {
          Resp<SkuInfoEntity> skuResp = this.pmsClient.querySkuInfoBySkuId(skuId);
          SkuInfoEntity skuInfo = skuResp.getData();
          if (skuInfo != null) {
            orderItemVO.setTitle(skuInfo.getSkuTitle());
            orderItemVO.setWeight(skuInfo.getWeight());
            orderItemVO.setPrice(skuInfo.getPrice());
            orderItemVO.setDefaultImage(skuInfo.getSkuDefaultImg());
            orderItemVO.setSkuId(skuId);
            orderItemVO.setCount(cart.getCount());
          }
        }, threadPoolExecutor);
        //小线程2
        CompletableFuture<Void> saleCompletableFuture = CompletableFuture.runAsync(() -> {
          Resp<List<SaleVO>> saleResp = this.smsClient.querySalsVosBySkuid(skuId);
          List<SaleVO> saleVOS = saleResp.getData();
          if (!CollectionUtils.isEmpty(saleVOS)) {
            orderItemVO.setSales(saleVOS);
          }
        }, threadPoolExecutor);
        //小线程3
        CompletableFuture<Void> attrValuesCompletableFuture = CompletableFuture.runAsync(() -> {
          Resp<List<SkuSaleAttrValueEntity>> attrValuesResp = this.pmsClient
              .querySaleAttrsByskuId(skuId);
          List<SkuSaleAttrValueEntity> attrValueEntities = attrValuesResp.getData();
          if (!CollectionUtils.isEmpty(attrValueEntities)) {
            orderItemVO.setSkuAttrValues(attrValueEntities);
          }
        }, threadPoolExecutor);
        //小线程4
        CompletableFuture<Void> wareCompletableFuture = CompletableFuture.runAsync(() -> {
          Resp<List<WareSkuEntity>> wareResp = this.wmsClient.queryWareSkuInfo(skuId);
          List<WareSkuEntity> wareSkuEntityList = wareResp.getData();
          if (!CollectionUtils.isEmpty(wareSkuEntityList)) {
            orderItemVO.setStore(
                wareSkuEntityList.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
          }
        }, threadPoolExecutor);
        //集合全部小线程
        CompletableFuture
            .allOf(skuCompletableFuture, saleCompletableFuture, attrValuesCompletableFuture,
                wareCompletableFuture).join();
        return orderItemVO;
      }).collect(Collectors.toList());
      confirmVO.setOrderItems(orderItemVOS);
    });

    //获取用户的收货地址列表 ===>在ums中补充一个依据userId查询地址的方法
    CompletableFuture<Void> addressCompletableFuture = CompletableFuture.runAsync(() -> {
      Resp<List<MemberReceiveAddressEntity>> addressResp = this.umsClient
          .queryAddressByUserId(userId);
      List<MemberReceiveAddressEntity> addressEntities = addressResp.getData();
      confirmVO.setAddressEntities(addressEntities);
    }, threadPoolExecutor);
    //查询用户信息,获取积分===>补充根据id 查用户信息 在接口工程中
    CompletableFuture<Void> memberCompletableFuture = CompletableFuture.runAsync(() -> {
      Resp<MemberEntity> memberEntityResp = this.umsClient.queryMemberEntityById(userId);
      MemberEntity memberEntity = memberEntityResp.getData();
      confirmVO.setBounds(memberEntity.getIntegration());
    }, threadPoolExecutor);
    //生成一个唯一标志,防止重复提交(响应到页面一份,有一份保存到redis)
    CompletableFuture<Void> tokenCompletableFuture = CompletableFuture.runAsync(() -> {
      String orderToken = IdWorker.getIdStr();
      this.redisTemplate.opsForValue().set(ORDER_PRIEFIX + orderToken, orderToken);
      confirmVO.setOrderToken(orderToken);
    }, threadPoolExecutor);

    CompletableFuture.allOf(bigCompletableFuture, addressCompletableFuture, memberCompletableFuture,
        tokenCompletableFuture).join();

    return confirmVO;
  }

  @Override
  public void submit(OrderSubmitVO orderSubmitVO) {
//    1>防重==>查询redis中有没有ordertoken信息,有就是第一次提交,放行并删除redis的ordertoken

//    2>检验价格,总价一致放行

//    3>检验库存并且上锁,一次性提示所有库存不够的商品信息(开发远程接口)

//    4>创建订单(待支付) 下单

//    5>删除购物车

  }
}
