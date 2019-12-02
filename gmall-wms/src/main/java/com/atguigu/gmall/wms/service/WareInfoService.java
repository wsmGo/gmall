package com.atguigu.gmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.wms.entity.WareInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 仓库信息
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 19:26:36
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageVo queryPage(QueryCondition params);
}

