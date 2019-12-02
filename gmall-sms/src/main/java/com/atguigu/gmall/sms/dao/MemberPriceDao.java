package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:14:37
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
