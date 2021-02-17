package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SkuService;
import com.atguigu.gmall.product.service.SpuService;
import com.atguigu.gmall.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    private SkuService skuService;

    @ApiOperation("添加sku")
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        skuService.saveSkuInfo(skuInfo);

        return Result.ok();
    }
    @ApiOperation("分页查询所有sku")
    @GetMapping("/list/{page}/{limit}")
    public Result<IPage<SkuInfo>> getPageSkuInfo(@PathVariable("page") Long page,
                                              @PathVariable("limit") Long limit){
        IPage<SkuInfo> skuInfoPage = skuService.getPageSkuInfo(page,limit);
        return Result.ok(skuInfoPage);
    }

    @ApiOperation("上架")
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        skuService.onSale(skuId);
        return Result.ok();
    }

    @ApiOperation("下架")
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        skuService.cancelSale(skuId);
        return Result.ok();
    }

}
