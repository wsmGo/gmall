package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.vo.AttrgroupVo;
import com.atguigu.gmall.pms.vo.BaseGroupVO;
import java.util.List;
import java.util.stream.Collectors;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private ProductAttrValueDao attrValueDao;

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

    @Override
    public List<AttrgroupVo> getWithattrs(Long catId) {
        //根据catid查询出attrgroup的集合
        List<AttrGroupEntity> attrGroupEntities = this
            .list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        //再将attrGroupEntities集合通过stream转换成AttrgroupVo集合 里面还有attrEntity参数 通过上面的方法通过传入gid获取
        return attrGroupEntities.stream().map(t->this.queryAttrgroupVo(t.getAttrGroupId())).collect(Collectors.toList());
    }

    @Override
    public List<BaseGroupVO> queryGroupVoByCidAndSpuid(Long cid, Long spuId) {

        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));

      return  attrGroupEntities.stream().map(group->{
            BaseGroupVO groupVO = new BaseGroupVO();
            groupVO.setName(group.getAttrGroupName());
            //求attrid
            List<AttrAttrgroupRelationEntity> relationEntities = this.attrAttrgroupRelationDao
                .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", group.getAttrGroupId()));
            List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            List<ProductAttrValueEntity> attrValueEntities = this.attrValueDao
                .selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attrIds));
            groupVO.setBaseAttrs(attrValueEntities);
            return groupVO;
        }).collect(Collectors.toList());
    }

}