package com.atguigu.gmall.ums.service;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 会员
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:23:49
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);

  Boolean checkData(String data, Integer type);

  void register(MemberEntity memberEntity, String code);

  MemberEntity queryUser(String username, String password);
}

