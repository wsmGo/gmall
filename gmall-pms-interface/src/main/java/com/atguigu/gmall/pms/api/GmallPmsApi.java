package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 530
 * @date 2019/12/9
 */
public interface GmallPmsApi {

  /**
   * 1)分页查询"pms/spuInfo/page
   * @param queryCondition
   * @return
   */
  @PostMapping("pms/spuinfo/page")
  public Resp<List<SpuInfoEntity>> querySpuByPage(@RequestBody QueryCondition queryCondition);

  /**
   * 2)"pms/skuInfo/{spuId}"根据spuId查询spu下的sku
   * @param spuId
   * @return
   */
  @GetMapping("pms/skuinfo/{spuId}")
  public Resp<List<SkuInfoEntity>> querySkInfo(@PathVariable("spuId")Long spuId);
  /**
   *3)根据品牌id查询品牌(brandName)
   * @param brandId
   * @return
   */
  @GetMapping("pms/brand/info/{brandId}")
  public Resp<BrandEntity> queryByBrandId(@PathVariable("brandId") Long brandId);

  /**
   *
   * @param catId
   * @return
   */
  @GetMapping("pms/category/info/{catId}")
  public Resp<CategoryEntity> queryByCatId(@PathVariable("catId") Long catId);

  /**
   * 分类 一级
   * @param parentId
   * @param level
   * @return
   */
  @GetMapping("pms/category")
  public Resp<List<CategoryEntity>> queryCategoriesByPidOrLev(@RequestParam(value = "parentCid",required = false)Long parentId,
      @RequestParam(value = "level",defaultValue = "0")Integer level);

  /**
   * 分2.3级
   * @param pid
   * @return
   */
  @GetMapping("pms/category/{pid}")
  public Resp<List<CategoryVO>> querySubCategories(@PathVariable("pid") Long pid);
  /**
   *
   * @param spuId
   * @return
   */
  @GetMapping("pms/productattrvalue/{spuId}")
  public Resp<List<ProductAttrValueEntity>> queryAttrvBySpuId(@PathVariable("spuId")Long spuId);


  @GetMapping("pms/spuinfo/info/{id}")
  public Resp<SpuInfoEntity> querySpuById(@PathVariable("id") Long id);
}
