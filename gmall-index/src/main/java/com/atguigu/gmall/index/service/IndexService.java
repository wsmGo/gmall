package com.atguigu.gmall.index.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 530
 * @date 2019/12/13
 */
@Service
public class IndexService {

  @Autowired
  private GmallPmsClient gmallPmsClient;

  public List<CategoryEntity> queryLv1Categories() {
    Resp<List<CategoryEntity>> listResp = this.gmallPmsClient.queryCategoriesByPidOrLev(null, 1);
    return  listResp.getData();
  }


  public List<CategoryVO> querySubCategoriseByPid(Long pid) {

    Resp<List<CategoryVO>> listResp = this.gmallPmsClient.querySubCategories(pid);

    return listResp.getData();

  }
}
