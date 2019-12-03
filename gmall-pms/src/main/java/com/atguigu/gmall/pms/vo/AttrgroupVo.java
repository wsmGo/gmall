package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/3
 * 分组vo
 *
 */

@Data
public class AttrgroupVo extends AttrGroupEntity {
  private List<AttrEntity> attrEntities;
  private List<AttrAttrgroupRelationEntity> relations;
}
