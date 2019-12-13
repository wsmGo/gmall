package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author 530
 * @date 2019/12/9
 */
@Data
public class SearchAttr {

  @Field(type = FieldType.Long)
  private Long attrId;
  @Field(type = FieldType.Keyword)
  private String attrName;
  @Field(type = FieldType.Keyword)
  private String attrValue;

}
