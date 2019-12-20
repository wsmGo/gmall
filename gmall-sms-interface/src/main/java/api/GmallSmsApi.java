package api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 530
 * @date 2019/12/5
 */

public interface GmallSmsApi {
  @PostMapping("sms/skubounds/saveSkuSale/SaleVo")
  public Resp<Object> saveSkuSale(@RequestBody SkuSaleVO skuSaleVO);

  @GetMapping("sms/skubounds/item/{skuId}")
  public Resp<List<SaleVO>> querySalsVosBySkuid(@PathVariable("skuId")Long skuId);
}
