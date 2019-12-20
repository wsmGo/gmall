package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class GmallAuthApplicationTests {

  @Test
  void contextLoads() {
  }
  private static final String pubKeyPath = "F:\\ideadevelop\\rsa.pub";

  private static final String priKeyPath = "F:\\ideadevelop\\rsa.pri";

  private PublicKey publicKey;

  private PrivateKey privateKey;

  @Test
  public void testRsa() throws Exception {
    RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
  }

 // @Before
  public void testGetRsa() throws Exception {
    this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
  }

  @Test
  public void testGenerateToken() throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put("id", "11");
    map.put("username", "liuyan");
    // 生成token
    String token = JwtUtils.generateToken(map, privateKey, 5);
    System.out.println("token = " + token);
  }

  @Test
  public void testParseToken() throws Exception {
    String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1NzAxMjEyODZ9.GioCiqMt_ZcN6_RAuDBcOzcHQ5WdqdhA9QYu-2IqCQqnAef1VyXczEInj1Ef1xo7AvcjxnkIMuZK48OoczUy1iqtPQPDchUzTl03b8h_J3xMBaxOAaKSwMpm20DH25VrTgBExUafyxHwxfOa-PVHW0Kk41KrWDncayzXbZ_lYLoa9Cuvacr8eAFz-ckriIiZ9bRzFkhX-wYHSHFlym2IJRjBRhFtpkN5GLAVsmsdm-yD4eiJXqioWspqXiBSdROsjrTRiFe511yujR0y2ngL9OnZ1QH6bHDQ2WmhPTrswKjjy-HWIxk1FQ7uXtSpPa5diymmPVTWA0clys7R1MK9oQ";

    // 解析token
    Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
    System.out.println("id: " + map.get("id"));
    System.out.println("userName: " + map.get("username"));
  }
}
