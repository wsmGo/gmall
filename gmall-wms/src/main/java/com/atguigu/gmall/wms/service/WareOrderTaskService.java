package com.atguigu.gmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.wms.entity.WareOrderTaskEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 库存工作单
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:26:36
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageVo queryPage(QueryCondition params);
}

