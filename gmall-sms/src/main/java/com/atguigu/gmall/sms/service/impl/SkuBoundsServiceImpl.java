package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;



@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuLadderDao skuLadderDao;

    @Autowired
    private SkuFullReductionDao skuFullReductionDao;



    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    @Transactional
    public void saveSkuSale(SkuSaleVO skuSaleVO) {
        Long skuId = skuSaleVO.getSkuId();
        //1.bounds 用service设置属性 对word字段进行一个转换(二进制)
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        skuBoundsEntity.setBuyBounds(skuSaleVO.getBuyBounds());
        skuBoundsEntity.setSkuId(skuId);
        skuBoundsEntity.setGrowBounds(skuSaleVO.getGrowBounds());
        List<Integer> work = skuSaleVO.getWork();
        skuBoundsEntity.setWork(work.get(3)*1+work.get(2)*2+work.get(1)*4+work.get(0)*8);
        this.save(skuBoundsEntity);
        //2.ladder 调用dao设置属性
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuId);
        skuLadderEntity.setFullCount(skuSaleVO.getFullCount());
        skuLadderEntity.setDiscount(skuSaleVO.getDiscount());
        skuLadderEntity.setAddOther(skuSaleVO.getLadderAddOther());
        this.skuLadderDao.insert(skuLadderEntity);
        //3.3.fullreduction 调用dao设置属性
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setSkuId(skuId);
        skuFullReductionEntity.setFullPrice(skuSaleVO.getFullPrice());
        skuFullReductionEntity.setReducePrice(skuSaleVO.getReducePrice());
        skuFullReductionEntity.setAddOther(skuSaleVO.getFullAddOther());
        this.skuFullReductionDao.insert(skuFullReductionEntity);

    }

    @Override
    public List<SaleVO> querySalsVosBySkuid(Long skuId) {
        ArrayList<SaleVO> saleVOS = new ArrayList<>();
        //查询积分
        SkuBoundsEntity boundsEntity = this.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (boundsEntity != null) {
        SaleVO boundVO = new SaleVO();
            boundVO.setType("积分");
            StringBuffer sb = new StringBuffer();
            if (boundsEntity.getGrowBounds() != null && boundsEntity.getGrowBounds().intValue() >=0 ) {
                    sb.append("成长积分送:" + boundsEntity.getGrowBounds());
            }
            if (boundsEntity.getBuyBounds() != null && boundsEntity.getBuyBounds().intValue() >=0 ) {
                if(StringUtils.isNotBlank(sb)){
                    sb.append(",  ");
                }
                sb.append("购买积分送:" + boundsEntity.getBuyBounds());
            }
            boundVO.setDesc(sb.toString());
        saleVOS.add(boundVO);
        }

        //查询满减
        SkuFullReductionEntity reductionEntity = this.skuFullReductionDao
            .selectOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (reductionEntity != null) {
            SaleVO reduceVO = new SaleVO();
            reduceVO.setType("满减");
            reduceVO.setDesc("满 " + reductionEntity.getFullPrice() + "减 " + reductionEntity.getReducePrice());
            saleVOS.add(reduceVO);
        }
        //查询打折
        SkuLadderEntity skuLadderEntity = this.skuLadderDao.selectOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (skuLadderEntity != null) {
            SaleVO ladderVO = new SaleVO();
            ladderVO.setType("打折");
            ladderVO.setDesc("满" + skuLadderEntity.getFullCount() + "件" + "打" + skuLadderEntity.getDiscount().divide(new BigDecimal(10))+ "折");
        saleVOS.add(ladderVO);
        }

        return saleVOS;
    }

}