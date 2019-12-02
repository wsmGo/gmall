package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 会员收货地址
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:23:49
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageVo queryPage(QueryCondition params);
}

