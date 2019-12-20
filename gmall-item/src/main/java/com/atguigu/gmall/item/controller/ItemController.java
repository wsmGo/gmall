package com.atguigu.gmall.item.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 530
 * @date 2019/12/15
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;
  @GetMapping("{skuId}")
  public Resp<ItemVO> queryItemVo(@PathVariable("skuId")Long skuId){
    ItemVO itemVO = itemService.queryItemVo(skuId);
    return Resp.ok(itemVO);
  }
}
