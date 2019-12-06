package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品属性
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 18:23:41
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);

  PageVo queryByCidAndType(QueryCondition queryCondition, Long cid, Integer type);

  void saveAttr(AttrVo attrVo);
}

