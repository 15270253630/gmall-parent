package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/item")
public class ApiItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/{skuId}")
    public Map<String,Object> getItem(@PathVariable("skuId") Long skuId){
        Map<String,Object> map = itemService.getItem(skuId);
        return map;
    }
}
