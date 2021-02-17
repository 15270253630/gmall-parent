package com.atguigu.gmall.list.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

import java.util.List;

public interface ListService {

    List<JSONObject> getCategoryList();

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    void addHostScore(Long skuId);

    SearchResponseVo list(SearchParam searchParam);
}
