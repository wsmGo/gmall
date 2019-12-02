package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 18:23:41
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
