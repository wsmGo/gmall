package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/14
 */
@Data
public class BaseGroupVO {
  /**
   * 基本属性分组及组下的规格参数
   */
  private String name;//分组的名字
  private List<ProductAttrValueEntity> baseAttrs;
}
