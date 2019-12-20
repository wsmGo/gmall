package com.atguigu.gmall.order.config;

import com.atguigu.core.utils.RsaUtils;
import java.io.File;
import java.security.PublicKey;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 530
 * @date 2019/12/16
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "gmall.jwt")
public class JwtProperties {

  private String pubKeyPath;// 公钥

  private PublicKey publicKey; // 公钥

  private String cookieName; // cookie名称

  /**
   * @PostContruct：在构造方法执行之后执行该方法
   */
  @PostConstruct
  public void init() {
    try {
      File pubKey = new File(pubKeyPath);
      // 获取公钥和私钥
      this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    } catch (Exception e) {
      log.error("初始化公钥和私钥失败！", e);
      throw new RuntimeException();
    }
  }

}
