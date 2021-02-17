package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.CateGoryService;
import com.atguigu.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/api/product")
public class ApiProductController {
    @Autowired
    private SkuService skuService;

    @Autowired
    private CateGoryService cateGoryService;

    @Autowired
    private BaseTrademarkService baseTrademarkService;



    @GetMapping("/getSkuInfoById/{skuId}")
    public SkuInfo getSkuInfoById(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = skuService.getSkuInfoById(skuId);
        return skuInfo;
    }

    @GetMapping("/getSkuPriceById/{skuId}")
    public BigDecimal getSkuPriceById(@PathVariable("skuId") Long skuId){
        BigDecimal price = skuService.getSkuPriceById(skuId);
        return price;
    }

    @GetMapping("/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){

        BaseCategoryView baseCategoryView = cateGoryService.getCategoryView(category3Id);
        return baseCategoryView;
    }

    @GetMapping("/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId){

        List<SpuSaleAttr> spuSaleAttrList = skuService.getSpuSaleAttrListCheckBySku(spuId,skuId);
        return spuSaleAttrList;
    }

    @GetMapping("/getValuesAsSkuIdMaps/{spuId}")
    public List<Map<String, Object>> getValuesAsSkuIdMaps(@PathVariable("spuId") Long spuId){
        List<Map<String, Object>> valuesAsSkuIdMaps =  skuService.getValuesAsSkuIdMaps(spuId);
        return valuesAsSkuIdMaps;
    }

    @GetMapping("/getCategoryList")
    public List<JSONObject> getCategoryList(){
        List<JSONObject> list = cateGoryService.getCategoryList();
        return list;
    }

    @GetMapping("/getTrademarkById/{tmId}")
    BaseTrademark getTrademarkById(@PathVariable("tmId") Long tmId){
        return baseTrademarkService.getTrademarkById(tmId);
    }

    @GetMapping("/getSkuAttrListById/{skuId}")
    List<SearchAttr> getSkuAttrListById(@PathVariable("skuId") Long skuId){
        return skuService.getSkuAttrListById(skuId);
    }
}
