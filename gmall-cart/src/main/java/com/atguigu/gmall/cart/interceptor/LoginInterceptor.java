package com.atguigu.gmall.cart.interceptor;

import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.cart.config.JwtProperties;
import com.atguigu.core.bean.UserInfo;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author 530
 * @date 2019/12/17
 */
@EnableConfigurationProperties(JwtProperties.class)
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
  @Autowired
  private JwtProperties properties;
  @Autowired
  private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    UserInfo userInfo = new UserInfo();
    //使用cookies工具获取token及userkey
    String token = CookieUtils.getCookieValue(request, properties.getCookieName());
    String userKey = CookieUtils.getCookieValue(request, properties.getUserKey());
    //判断userKey是否为空 如果为空则制作一个放入
    if (StringUtils.isEmpty(userKey)) {
      userKey = UUID.randomUUID().toString();
      CookieUtils.setCookie(request, response, this.properties.getUserKey(), userKey, 6 * 30 * 24);
    }
    //这步就有了userKey
    userInfo.setUserKey(userKey);
    //判断是否有token
    if (StringUtils.isNotBlank(token)) {
      //解析token
      Map<String, Object> infoFromToken = JwtUtils.getInfoFromToken(token, this.properties.getPublicKey());
      if (!CollectionUtils.isEmpty(infoFromToken)){
        userInfo.setId(new Long(infoFromToken.get("id").toString()));
      }
    }
    THREAD_LOCAL.set(userInfo);
    return super.preHandle(request, response, handler);
  }

  /**
   * 获取userinfo的方法
   * @return
   */
  public static UserInfo getUserInfo(){
    return  THREAD_LOCAL.get();
  }
  //需要停掉
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
      THREAD_LOCAL.remove();
  }
}
