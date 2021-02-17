package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ListFeignClient listFeignClient;

    @Override
    public Map<String, Object> getItem(Long skuId) {
        long beginTime = System.currentTimeMillis();

        Map<String, Object> map = new ConcurrentHashMap<>();

        CompletableFuture<SkuInfo> getSkuInfoFuture = CompletableFuture.supplyAsync(()-> {
                // 查询skuInfo信息包括所有图片
                SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);
                map.put("skuInfo", skuInfo);
                return skuInfo;
        },threadPoolExecutor);

        CompletableFuture<Void> getPriceFuture = CompletableFuture.runAsync(() -> {
            // 查询价格
            BigDecimal price = productFeignClient.getSkuPriceById(skuId);
            map.put("price", price);
        },threadPoolExecutor);


        CompletableFuture<Void> getCategoryViewFuture = getSkuInfoFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                // 查询当前选中sku的一 ~ 三级分类
                BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                map.put("categoryView", categoryView);
            }
        },threadPoolExecutor);

        CompletableFuture<Void> getSpuSaleAttrListFuture = getSkuInfoFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                // 查询当前sku对应的spu所有销售属性，以及当前sku所选中的销售属性
                List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getSpuId(), skuId);
                map.put("spuSaleAttrList", spuSaleAttrList);
            }
        },threadPoolExecutor);

        CompletableFuture<Void> getValuesSkuJsonFuture = getSkuInfoFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                // 查询当前spu所有的sku_sale_attr_value组合对应的sku 返回 values:33|44 skuId:13 形式的Map形式的json  ： valuesSkuJson
                Map valuesSkuMap = new HashMap<>();
                List<Map<String, Object>> valuesAsSkuIdMaps = productFeignClient.getValuesAsSkuIdMaps(skuInfo.getSpuId());
                for (Map<String, Object> valuesAsSkuIdMap : valuesAsSkuIdMaps) {
                    valuesSkuMap.put(valuesAsSkuIdMap.get("values"), valuesAsSkuIdMap.get("sku_id"));
                }
                String valuesSkuJson = JSON.toJSONString(valuesSkuMap);

                map.put("valuesSkuJson", valuesSkuJson);
            }
        },threadPoolExecutor);

        CompletableFuture.allOf(getSkuInfoFuture,
                getPriceFuture,
                getCategoryViewFuture,
                getSpuSaleAttrListFuture,
                getValuesSkuJsonFuture).join();

        long endTime = System.currentTimeMillis();
        System.out.println("SingleThread总共花了:" + (endTime - beginTime) + "ms");

        listFeignClient.addHostScore(skuId);

        return map;
    }


}
