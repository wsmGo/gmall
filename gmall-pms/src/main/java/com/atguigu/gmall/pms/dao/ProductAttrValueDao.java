package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu属性值
 * 
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 18:23:41
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {


  List<ProductAttrValueEntity> queryAttrvBySpuId(Long spuId);
}
