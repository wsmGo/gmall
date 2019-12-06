package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import java.util.List;


/**
 * 属性&属性分组关联
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 18:23:41
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageVo queryPage(QueryCondition params);

  void deleteAttrs(List<AttrAttrgroupRelationEntity> relationEntities);
}

