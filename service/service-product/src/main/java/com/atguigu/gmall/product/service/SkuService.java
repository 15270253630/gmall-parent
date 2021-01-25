package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> getPageSkuInfo(Long page, Long limit);

    void onSale(Long skuId);

    void PathVariable(Long skuId);
}
