package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParamVO;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import java.io.IOException;
import org.springframework.stereotype.Service;

/**
 * @author 530
 * @date 2019/12/10
 */

public interface SearchVoService {

  SearchResponseVO search(SearchParamVO searchParamVO) throws IOException;
}
