package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.SpuService;
import com.atguigu.gmall.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/admin/product")
public class SpuController {
    @Autowired
    private SpuService spuService;

    @ApiOperation("根据3级分类id分页查询所有spu信息")
    @GetMapping("/{page}/{limit}")
    public Result<IPage<SpuInfo>> pageSpuByCategory3Id(@PathVariable("page") Long page,
                                                    @PathVariable("limit") Long limit,
                                                    Long category3Id){
        IPage<SpuInfo> iPage =  spuService.pageSpuByCategory3Id(page,limit,category3Id);
        return Result.ok(iPage);
    }


    @ApiOperation("获取平台所有销售属性")
    @GetMapping("/baseSaleAttrList")
    public Result<List<BaseSaleAttr>> baseSaleAttrList(){
        List<BaseSaleAttr> spuSaleAttrList =  spuService.baseSaleAttrList();
        return Result.ok(spuSaleAttrList);
    }

    @ApiOperation("获取平台所有品牌")
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result<List<BaseTrademark>> getTrademarkList(){
        List<BaseTrademark> baseTrademarkList =  spuService.getTrademarkList();
        return Result.ok(baseTrademarkList);
    }

    @ApiOperation("保存spu信息")
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuService.saveSpuInfo(spuInfo);
        return Result.ok();
    }


    @ApiOperation("根据spuId获取图片列表")
    @GetMapping("/spuImageList/{spuId}")
    public Result<List<SpuImage>> spuImageList(@PathVariable Long spuId){

        List<SpuImage> spuImageList = spuService.spuImageList(spuId);

        return Result.ok(spuImageList);
    }

    @ApiOperation("根据spuId获取销售属性")
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttr>> spuSaleAttrList(@PathVariable Long spuId){

        List<SpuSaleAttr> spuSaleAttrList = spuService.spuSaleAttrList(spuId);

        return Result.ok(spuSaleAttrList);
    }
}
