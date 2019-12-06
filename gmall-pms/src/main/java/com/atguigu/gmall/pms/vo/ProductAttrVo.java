package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author 530
 * @date 2019/12/4
 * 因为传输的参数名不同 需要重写set方法  传过来的是list集合
 */
@Data
public class ProductAttrVo extends ProductAttrValueEntity {
  //获取的集合可能为空 所以需要判断
    public  void setValueSelected(List<Object> valueSelected){
      if (CollectionUtils.isEmpty(valueSelected)){
        return;
      }
      //不为空 数据需要分割逗号保存
        this.setAttrValue(StringUtils.join(valueSelected,","));
    }
}
