package com.atguigu.gmall.product.client;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient("service-product")
public interface ProductFeignClient {

    @GetMapping("/api/product/getSkuInfoById/{skuId}")
    SkuInfo getSkuInfoById(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/getSkuPriceById/{skuId}")
    BigDecimal getSkuPriceById(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id);

    @GetMapping("/api/product/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/getValuesAsSkuIdMaps/{spuId}")
    List<Map<String, Object>> getValuesAsSkuIdMaps(@PathVariable("spuId") Long spuId);

    @GetMapping("/api/product/getCategoryList")
    List<JSONObject> getCategoryList();

    @GetMapping("/api/product/getTrademarkById/{tmId}")
    BaseTrademark getTrademarkById(@PathVariable("tmId") Long tmId);

    @GetMapping("/api/product/getSkuAttrListById/{skuId}")
    List<SearchAttr> getSkuAttrListById(@PathVariable("skuId") Long skuId);
}
