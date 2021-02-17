package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> getPageSkuInfo(Long page, Long limit);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SkuInfo getSkuInfoById(Long skuId);

    BigDecimal getSkuPriceById(Long skuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long spuId, Long skuId);

    List<Map<String, Object>> getValuesAsSkuIdMaps(Long spuId);

    List<SearchAttr> getSkuAttrListById(Long skuId);
}
