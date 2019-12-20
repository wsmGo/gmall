package com.atguigu.gmall.pms;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallPmsApplicationTests {

  @Test
  void contextLoads() {

    String a = "asdadada";
    String[] split = StringUtils.split(a, ",");
    System.out.println(Arrays.asList(split));
  }

}
