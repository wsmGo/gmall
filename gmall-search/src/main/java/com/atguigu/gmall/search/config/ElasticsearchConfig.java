package com.atguigu.gmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 530
 * @date 2019/12/11
 */
@Configuration
public class ElasticsearchConfig {

  @Bean
  public RestHighLevelClient restHighLevelClient(){

   return new RestHighLevelClient(RestClient.builder(HttpHost.create("192.168.229.129:9200")));
  }
}
