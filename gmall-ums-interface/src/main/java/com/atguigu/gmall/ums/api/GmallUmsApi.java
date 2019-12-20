package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 530
 * @date 2019/12/16
 */
public interface GmallUmsApi {

  @GetMapping("ums/member/info/{id}")
  public Resp<MemberEntity> queryMemberEntityById(@PathVariable("id") Long id);

  @GetMapping("ums/member/query")
  public Resp<MemberEntity> queryUser(@RequestParam("username") String username , @RequestParam("password")String password);

  @GetMapping("ums/memberreceiveaddress/{userId}")
  public Resp<List<MemberReceiveAddressEntity>> queryAddressByUserId(@PathVariable("userId")Long userId);
}
