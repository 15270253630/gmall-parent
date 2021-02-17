package com.atguigu.gmall.all.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;


@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    @RequestMapping("/{skuId}.html")
    public String getItem(@PathVariable("skuId") Long skuId, Model model) {
        if (skuId == null || skuId <= 0){
            return "error";
        }

        // 远程调用Service-item获取Item的数据
        Map<String, Object> map = itemFeignClient.getItem(skuId);
        model.addAllAttributes(map);

        return "item/index";
    }


}
