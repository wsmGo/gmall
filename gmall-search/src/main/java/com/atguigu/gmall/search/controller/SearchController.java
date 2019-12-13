package com.atguigu.gmall.search.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.pojo.SearchParamVO;

import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.atguigu.gmall.search.service.SearchVoService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 530
 * @date 2019/12/10
 */
@RestController
@RequestMapping("search")
public class SearchController {

  @Autowired
  private SearchVoService searchService;

  @GetMapping
public Resp<SearchResponseVO> search(SearchParamVO searchParamVO) throws IOException {

    SearchResponseVO search = this.searchService.search(searchParamVO);

    return  Resp.ok(search);
}
}
