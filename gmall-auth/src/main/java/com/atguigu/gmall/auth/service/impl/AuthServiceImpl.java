package com.atguigu.gmall.auth.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author 530
 * @date 2019/12/16
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthServiceImpl implements AuthService {

  @Autowired
  private GmallUmsClient umsClient;
  @Autowired
  private  JwtProperties jwtProperties;
  @Override
  public String accredit(String username, String password) {

    //校验是否有用户
    Resp<MemberEntity> memberEntityResp = this.umsClient.queryUser(username, password);
    MemberEntity memberEntity = memberEntityResp.getData();
    if (memberEntity == null){
      return null;
    }

    //制作jwt
    try {
      Map<String, Object> map = new HashMap<>();
      map.put("id", memberEntity.getId());
      map.put("username", memberEntity.getUsername());
     return JwtUtils.generateToken(map, this.jwtProperties.getPrivateKey(),this.jwtProperties.getExpire());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
