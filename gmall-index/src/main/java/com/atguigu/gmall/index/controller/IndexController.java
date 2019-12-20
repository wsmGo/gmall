package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 530
 * @date 2019/12/13
 */
@RestController
@RequestMapping("index")
public class IndexController {


  @Autowired
  private IndexService indexService;

  /**
   * 一级菜单
   * @return
   */
  @GetMapping("cates")
  public Resp<List<CategoryEntity>> queryLv1Categories(){
    List<CategoryEntity> categoryEntities = this.indexService.queryLv1Categories();
    return Resp.ok(categoryEntities);
  }

  /**
   * 二级,三级菜单
   */
  @GetMapping("cates/{pid}")
  public  Resp<List<CategoryVO>> querySubCategorise(@PathVariable("pid") Long pid){

    List<CategoryVO> categoryVOS =indexService.querySubCategoriseByPid(pid);

    return Resp.ok(categoryVOS);
  }

}
