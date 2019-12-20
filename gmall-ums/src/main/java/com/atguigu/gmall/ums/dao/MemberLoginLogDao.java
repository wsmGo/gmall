package com.atguigu.gmall.ums.dao;

import com.atguigu.gmall.ums.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-16 21:19:31
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
