package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/13
 */
@Data
public class CategoryVO extends CategoryEntity {

  private List<CategoryEntity> subs;
}
