package api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 530
 * @date 2019/12/5
 */

public interface GmallSmsApi {
  @PostMapping("sms/skubounds/saveSkuSale/SaleVo")
  public Resp<Object> saveSkuSale(@RequestBody SkuSaleVO skuSaleVO);
}
