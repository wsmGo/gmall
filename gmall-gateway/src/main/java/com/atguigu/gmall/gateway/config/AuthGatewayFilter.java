package com.atguigu.gmall.gateway.config;

import com.atguigu.core.utils.JwtUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义过滤器
 *
 * @author 530
 * @date 2019/12/17
 */

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AuthGatewayFilter implements GatewayFilter {
  @Autowired
  private JwtProperties properties;
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    //获取token  ====>得现有cookie
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();
    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
    //判断cookies是否为空
    if (CollectionUtils.isEmpty(cookies)) {
      //没有cookies直接返回响应  拦截
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return response.setComplete();
    }
    //获取jwt的cookies
    HttpCookie httpCookie = cookies.getFirst(this.properties.getCookieName());
    //判断token是否为空
    if (  httpCookie == null){
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return response.setComplete();
    }
    //解析jwt  正常就放行
    try {
      JwtUtils.getInfoFromToken(httpCookie.getValue(), this.properties.getPublicKey());
    } catch (Exception e) {
      e.printStackTrace();
      //拦截
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return response.setComplete();
    }
    //放行
    return chain.filter(exchange);
  }
}
