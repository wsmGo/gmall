package com.atguigu.gmall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.oms.entity.RefundInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 退款信息
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:10:03
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageVo queryPage(QueryCondition params);
}

