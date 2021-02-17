package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.annotation.GmallCache;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.CateGory1Mapper;
import com.atguigu.gmall.product.mapper.CateGory2Mapper;
import com.atguigu.gmall.product.mapper.CateGory3Mapper;
import com.atguigu.gmall.product.mapper.CateGoryViewMapper;
import com.atguigu.gmall.product.service.CateGoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CateGoryServiceImpl implements CateGoryService {

    @Autowired
    private CateGory1Mapper cateGory1Mapper;

    @Autowired
    private CateGory2Mapper cateGory2Mapper;

    @Autowired
    private CateGory3Mapper cateGory3Mapper;

    @Autowired
    private CateGoryViewMapper cateGoryViewMapper;

    @Override
    public List<BaseCategory1> getCategory1() {
        return cateGory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        wrapper.eq("category1_id",category1Id);
        return cateGory2Mapper.selectList(wrapper);
    }

    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id",category2Id);
        return cateGory3Mapper.selectList(wrapper);
    }

    @GmallCache(suffix = "baseCategoryView")
    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        QueryWrapper<BaseCategoryView> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        BaseCategoryView baseCategoryView = cateGoryViewMapper.selectOne(wrapper);
        return baseCategoryView;
    }

    @Override
    public List<JSONObject> getCategoryList() {

        List<BaseCategoryView> baseCategoryViewList = cateGoryViewMapper.selectList(null);
        List<JSONObject> jsonObjectList = new ArrayList<>();

        // 封装一级分类
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        Set<Map.Entry<Long, List<BaseCategoryView>>> category1EntrySet = category1Map.entrySet();
        for (Map.Entry<Long, List<BaseCategoryView>> category1Entry : category1EntrySet) {
            JSONObject category1 = new JSONObject();
            Long category1Id = category1Entry.getKey();
            String category1Name = category1Entry.getValue().get(0).getCategory1Name();
            List<JSONObject> category2List = new ArrayList<>();

            // 封装装二级分类
            Map<Long, List<BaseCategoryView>> category2Map = category1Entry.getValue().stream()
                    .collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> category2Entry : category2Map.entrySet()) {
                JSONObject category2 = new JSONObject();
                Long category2Id = category2Entry.getKey();
                String category2Name = category2Entry.getValue().get(0).getCategory2Name();
                List<JSONObject> category3List = new ArrayList<>();

                // 封装三级分类
                Map<Long, List<BaseCategoryView>> category3Map = category2Entry.getValue().stream()
                        .collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> category3Entry : category3Map.entrySet()) {
                    JSONObject category3 = new JSONObject();
                    Long category3Id = category3Entry.getKey();
                    String category3Name = category3Entry.getValue().get(0).getCategory3Name();

                    category3.put("categoryId",category3Id);
                    category3.put("categoryName",category3Name);
                    category3List.add(category3);
                }

                category2.put("categoryId",category2Id);
                category2.put("categoryName",category2Name);
                category2.put("categoryChild",category3List);
                category2List.add(category2);
            }

            category1.put("categoryId",category1Id);
            category1.put("categoryName",category1Name);
            category1.put("categoryChild",category2List);
            jsonObjectList.add(category1);
        }

        return jsonObjectList;
    }
}
