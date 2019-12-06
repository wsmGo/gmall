package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import java.util.List;
import lombok.Data;

/**
 * @author 530
 * @date 2019/12/4
 *  spuInfo扩展对象
 *  包含：spuInfo基本信息、spuImages图片信息、baseAttrs基本属性信息、skus信息
 *  属性名要和网页传输的一致
 */
@Data
public class SpuInfoVo extends SpuInfoEntity {
    private List<String> spuImages;
    private List<ProductAttrVo> baseAttrs;
    private  List<SkuInfoVo> skus;
}
