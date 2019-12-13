package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;

import com.atguigu.gmall.pms.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void deleteAttrs(List<AttrAttrgroupRelationEntity> relationEntities) {
       //遍历集合 根据id删除 stream流
        relationEntities.forEach(attrAttrgroupRelationEntity -> {
            this.remove(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attrAttrgroupRelationEntity.getAttrId())
            .eq("attr_group_id", attrAttrgroupRelationEntity.getAttrGroupId()));
        });
    }

}