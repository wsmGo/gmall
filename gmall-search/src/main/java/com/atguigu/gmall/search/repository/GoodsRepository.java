package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 530
 * @date 2019/12/10
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {

}
