package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.IntegrationChangeHistoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 积分变化历史记录
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:23:49
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageVo queryPage(QueryCondition params);
}

