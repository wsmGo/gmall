package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.GrowthChangeHistoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 成长值变化历史记录
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-16 21:19:31
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageVo queryPage(QueryCondition params);
}

