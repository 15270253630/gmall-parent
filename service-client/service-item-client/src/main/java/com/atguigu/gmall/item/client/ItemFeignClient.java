package com.atguigu.gmall.item.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("service-item")
public interface ItemFeignClient {

    @GetMapping("/api/item/{skuId}")
    Map<String,Object> getItem(@PathVariable("skuId") Long skuId);

}
