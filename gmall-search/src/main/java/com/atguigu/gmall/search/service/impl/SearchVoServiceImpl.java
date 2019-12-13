package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchParamVO;
import com.atguigu.gmall.search.pojo.SearchResponseAttrVO;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.atguigu.gmall.search.service.SearchVoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author 530
 * @date 2019/12/10
 */
@Service
public class SearchVoServiceImpl implements SearchVoService {

  @Autowired
  private RestHighLevelClient restHighLevelClient;

  private static final ObjectMapper jackSon = new ObjectMapper();

  @Override
  public SearchResponseVO search(SearchParamVO searchParamVO) throws IOException {
    //构建dsl语句
    SearchRequest searchRequest = this.buildQueryDsl(searchParamVO);
    SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//    System.out.println(searchRequest);
    //构建响应的SearchResponseVo  将searchResponse 解析成searchResponseVO
    SearchResponseVO searchResponseVO = this.parseSearchResult(searchResponse);
    System.out.println(searchResponse);
    searchResponseVO.setPageNum(searchParamVO.getPageNum());
    searchResponseVO.setPageSize(searchParamVO.getPageSize());

    return searchResponseVO;

  }

  private SearchResponseVO parseSearchResult(SearchResponse searchResponse)
      throws JsonProcessingException {

    SearchResponseVO responseVO = new SearchResponseVO();

    //设置参数
    //获取总命中数
    responseVO.setTotal(searchResponse.getHits().totalHits);
    //解析品牌的聚合结果集
    //需要这种对象来装
    SearchResponseAttrVO brand = new SearchResponseAttrVO();
    brand.setName("品牌");
    //获取总的属性聚合结果集
    Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
    //获取品牌分类的聚合
    ParsedLongTerms brandIdAgg = (ParsedLongTerms)aggregationMap.get("brandIdAgg");
    //将桶转换成List<String> json字符串 id:.. name:..
    List<String> brandValues = brandIdAgg.getBuckets().stream().map(brandbucket -> {
      HashMap<String, String> map = new HashMap<>();
      //设置品牌的id
      map.put("id",brandbucket.getKeyAsString());
      //从子聚合中获取品牌名
      Map<String, Aggregation> subAggMap = brandbucket.getAggregations().asMap();
      ParsedStringTerms brandNameAgg = (ParsedStringTerms)subAggMap.get("brandNameAgg");
      String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
      map.put("name", brandName);
      //得到了map 将其转换成json字符串
      try {
        return jackSon.writeValueAsString(map);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      return null;
    }).collect(Collectors.toList());

    brand.setValue(brandValues);
    responseVO.setBrand(brand);
    //获取分类的属性聚合结果集
    ParsedLongTerms categoryIdAgg = (ParsedLongTerms)aggregationMap.get("categoryIdAgg");
    //通过聚合转换成需要的list对象将分类也转换成id:.. name:...
    List<String> categoryValues = categoryIdAgg.getBuckets().stream().map(category -> {
      HashMap<String, String> map = new HashMap<>();
      //设置分类id
      map.put("id", category.getKeyAsString());
      //从子聚合中获取分类的value
      Map<String, Aggregation> subAggMap = category.getAggregations().asMap();
      ParsedStringTerms categoryNameAgg = (ParsedStringTerms) subAggMap.get("categoryNameAgg");
      String categoryName = categoryNameAgg.getBuckets().get(0).getKeyAsString();
      map.put("name", categoryName);
      //将map转成json字符串

      try {
        return jackSon.writeValueAsString(map);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      return null;
    }).collect(Collectors.toList());
    SearchResponseAttrVO category = new SearchResponseAttrVO();
    category.setName("分类");
    category.setValue(categoryValues);
    responseVO.setCatelog(category);

    //商品展示列表  就是_source里面的内容
    SearchHits hits = searchResponse.getHits();
    SearchHit[] subHits = hits.getHits();
    List<Goods> goodsArrayList = new ArrayList<>();
    for (SearchHit subHit : subHits) {
      Goods goods = jackSon.readValue(subHit.getSourceAsString(), new TypeReference<Goods>() {
      });
      //优化1:注入高亮
      goods.setTitle(subHit.getHighlightFields().get("title").getFragments()[0].toString());
      goodsArrayList.add(goods);
    }
    responseVO.setProducts(goodsArrayList);

    //规格属性聚合解析
    //这是嵌套聚合 使用ParsedNested解析
    ParsedNested attrAgg = (ParsedNested)aggregationMap.get("attrAgg");
    ParsedLongTerms attrIdAgg = (ParsedLongTerms)attrAgg.getAggregations().get("attrIdAgg");
    //attrIdAgg获取桶
    List<? extends Bucket> buckets = attrIdAgg.getBuckets();
    //判断桶是否为空
    if(!CollectionUtils.isEmpty(buckets)){
    //不为空转换成SearchResponseAttrVO的新集合
      List<SearchResponseAttrVO> searchResponseAttrVOs = buckets.stream().map(bucket -> {
        SearchResponseAttrVO searchResponseAttrVO = new SearchResponseAttrVO();
        //设置规格参数ID
        searchResponseAttrVO.setProductAttributeId((bucket.getKeyAsNumber().longValue()));
        //设置规格参数名
        //获取自聚合
        ParsedStringTerms attrNameAgg = (ParsedStringTerms) bucket.getAggregations()
            .get("attrNameAgg");
        List<? extends Bucket> attrNameBuckets = attrNameAgg.getBuckets();
        searchResponseAttrVO.setName(attrNameBuckets.get(0).getKeyAsString());
        //设置规格参数列表
        ParsedStringTerms attrValueAgg = (ParsedStringTerms) bucket.getAggregations()
            .get("attrValueAgg");
        List<? extends Bucket> attrValueBuckets = attrValueAgg.getBuckets();
        List<String> attrValues = attrValueBuckets.stream().map(Bucket::getKeyAsString)
            .collect(Collectors.toList());
        searchResponseAttrVO.setValue(attrValues);
        return searchResponseAttrVO;
      }).collect(Collectors.toList());
      responseVO.setAttrs(searchResponseAttrVOs);
    }
    return  responseVO;
  }



  private SearchRequest buildQueryDsl(SearchParamVO searchParamVO) {
    //获取查询关键字
    String keyword = searchParamVO.getKeyword();
    if (StringUtils.isEmpty(keyword)) {
      return null;
    }
    //创建查询条件构建器
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    //构建查询条件和过滤条件
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    boolQueryBuilder.must(QueryBuilders.matchQuery("title", keyword).operator(Operator.AND));
    //构建过滤条件
    //构建品牌过滤 需要判断
    String[] brandid = searchParamVO.getBrand();
    if (brandid != null && brandid.length != 0) {
      boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandid));
    }
    //构建分类过滤 需要判断
    String[] catelog3 = searchParamVO.getCatelog3();
    if (catelog3 != null && catelog3.length != 0) {
      boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId", catelog3));
    }
    //构建规格属性的嵌套过滤
    String[] props = searchParamVO.getProps();
    // 1)判断规格 是否为空
    if (props != null && props.length != 0) {
      for (String prop : props) {
        String[] split = StringUtils.split(prop, ":");
        //判断切割后的字符串是否合法 不合法就直接跳下一个继续循环
        if (split == null || split.length != 2) {
          continue;
        }
        String[] attrValue = StringUtils.split(split[1], "-");
        //构建嵌套查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //构建子查询
        BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();
        //构建子查询中的过滤条件
        subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
        subBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
        //将嵌套查询放入过滤
        boolQuery.must(QueryBuilders.nestedQuery("attrs", subBoolQuery, ScoreMode.None));

        boolQueryBuilder.filter(boolQuery);
      }
    }
    //价格区间过滤
    RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
    Integer priceFrom = searchParamVO.getPriceFrom();
    Integer priceTo = searchParamVO.getPriceTo();
    if (priceFrom != null) {
      rangeQuery.gte(priceFrom);
    }
    if (priceTo != null) {
      rangeQuery.lte(priceTo);
    }
    boolQueryBuilder.filter(rangeQuery);
    sourceBuilder.query(boolQueryBuilder);
    //构建分页查询
    Integer pageNum = searchParamVO.getPageNum();
    Integer pageSize = searchParamVO.getPageSize();
    sourceBuilder.from((pageNum - 1) * pageSize);
    sourceBuilder.size(pageSize);
    //构建排序
    String order = searchParamVO.getOrder();
    if (!StringUtils.isEmpty(order)) {
      String[] split = StringUtils.split(order, ":");
      if (split != null && split.length == 2) {
        String filed = null;
        switch (split[0]) {
          case "1":
            filed = "sale";
            break;
          case "2":
            filed = "price";
            break;
            default:
              break;
        }
        sourceBuilder
            .sort(filed, StringUtils.equals("asc", split[1]) ? SortOrder.ASC : SortOrder.DESC);
      }
    }
    //构建高亮显示
    sourceBuilder
        .highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"));
    //构建聚合
    //使用聚合构建器
    //品牌聚合
    sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
        .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName")));
    //分类聚合
    sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
        .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName")));
    //规格嵌套聚合
    sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg", "attrs")
        .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
            .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
            .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))));

    //优化二:过滤不需要的数据 _source
    sourceBuilder.fetchSource(new String[]{"skuId","pic","title","price"}, null);

    //设置索引库
    SearchRequest searchRequest = new SearchRequest("goods");
    //设置索引库类型
    searchRequest.types("info");
    searchRequest.source(sourceBuilder);
   // System.out.println(sourceBuilder.toString());

    return searchRequest;
  }
}
