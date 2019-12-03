package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.AttrgroupVo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;
import springfox.documentation.schema.Collections;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );
        return new PageVo(page);
    }

    @Override
    public PageVo queryGruopByCatId(QueryCondition queryCondition, Long catId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (catId != null) {
            wrapper.eq("catelog_id", catId);
        }
        IPage<AttrGroupEntity> page = this.page(
            new Query<AttrGroupEntity>().getPage(queryCondition),
            wrapper
        );
        return new PageVo(page);
    }

    @Override
    public AttrgroupVo queryAttrgroupVo(Long gid) {
        AttrgroupVo attrgroupVo = new AttrgroupVo();
        //根据组id先查询出对应的分组
        AttrGroupEntity attrGroupEntity = this.attrGroupDao.selectById(gid);
        //将查询出来的属性赋值给vo
        BeanUtils.copyProperties(attrGroupEntity, attrgroupVo);
        //再根据git查询AttrEntity集合/ 查询分组下的关联关系
        List<AttrAttrgroupRelationEntity> relationEntity = attrAttrgroupRelationDao
            .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));
        //判断集合是否为空 如果是空 直接返回对象
        if(CollectionUtils.isEmpty(relationEntity)){
            return attrgroupVo;
        }
        //不为空 则添加到vo咯
        attrgroupVo.setRelations(relationEntity);
        //relationEntity 通过stream流获取所有的属性的id
        List<Long> ids = relationEntity.stream().map(rEntity -> rEntity.getAttrId())
            .collect(Collectors.toList());
        //获取后装入vo
        List<AttrEntity> attrEntities = this.attrDao.selectBatchIds(ids);
        attrgroupVo.setAttrEntities(attrEntities);
        return attrgroupVo;
    }

}