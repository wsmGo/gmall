package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SeckillPromotionEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 秒杀活动
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:14:37
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageVo queryPage(QueryCondition params);
}

