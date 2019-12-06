package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrgroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import java.util.List;


/**
 * 属性分组
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 18:23:41
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

  PageVo queryGruopByCatId(QueryCondition queryCondition, Long catId);

  AttrgroupVo queryAttrgroupVo(Long gid);

  List<AttrgroupVo> getWithattrs(Long catId);
}

