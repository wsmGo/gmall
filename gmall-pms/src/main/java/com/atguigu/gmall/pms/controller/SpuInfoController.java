package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.pms.vo.SpuInfoVo;
import java.util.Arrays;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.service.SpuInfoService;




/**
 * spu信息
 *
 * @author 530
 * @email 529014751@qq.com
 * @date 2019-12-02 18:23:41
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${item.rabbitmq.exchange}")
    private String GMALLPMS_EXCHANGE;


    /**
     * 根据cid分类spu
     * @param condition
     * @param catId
     * @return
     */
    @GetMapping
    public Resp<PageVo> querySpuInfo(QueryCondition condition,@RequestParam("catId")Long catId){

        PageVo pageVo = this.spuInfoService.querySpuInfo(condition,catId);
        return Resp.ok(pageVo);
    }

    @PostMapping("page")
    public Resp<List<SpuInfoEntity>> querySpuByPage(@RequestBody QueryCondition queryCondition) {
        PageVo pageVo = spuInfoService.queryPage(queryCondition);
        List<SpuInfoEntity> spuList = (List<SpuInfoEntity>)pageVo.getList();
        return Resp.ok(spuList);
    }



    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:spuinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = spuInfoService.queryPage(queryCondition);
        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('pms:spuinfo:info')")
    public Resp<SpuInfoEntity> info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return Resp.ok(spuInfo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:spuinfo:save')")
    public Resp<Object> bigSave(@RequestBody SpuInfoVo spuInfoVo){
	//	spuInfoService.save(spuInfo);
    this.spuInfoService.bigSave(spuInfoVo);
        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:spuinfo:update')")
    public Resp<Object> update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);
        //使用消息队列给购物车工程的监听器传送spuid
        this.amqpTemplate.convertAndSend(GMALLPMS_EXCHANGE, "item.update", spuInfo.getId());
        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:spuinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
