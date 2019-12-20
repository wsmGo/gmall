package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 530
 * @date 2019/12/18
 */
@Component
public class CartListener {

  @Autowired
  private GmallPmsClient pmsClient;
  @Autowired
  private StringRedisTemplate redisTemplate;
  private static final String PRICE_PRIEFIX = "cart:price:";
  @RabbitListener(bindings = @QueueBinding(
      value = @Queue(value = "Cart-queue", durable = "true"),
      exchange = @Exchange(value = "PMS-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
      key = {"item.update"}
  ))
  public void cartListener(Long spuId) {
    Resp<List<SkuInfoEntity>> skusResp = this.pmsClient.querySkInfo(spuId);
    List<SkuInfoEntity> skuInfoEntityList = skusResp.getData();
    skuInfoEntityList.forEach(skuInfoEntity -> {
     //遍历存储新的价格
      this.redisTemplate.opsForValue().set(PRICE_PRIEFIX+skuInfoEntity.getSkuId(), skuInfoEntity.getPrice().toString());
    });
  }
}
