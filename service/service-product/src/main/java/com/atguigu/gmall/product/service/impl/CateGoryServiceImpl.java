package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.mapper.CateGory1Mapper;
import com.atguigu.gmall.product.mapper.CateGory2Mapper;
import com.atguigu.gmall.product.mapper.CateGory3Mapper;
import com.atguigu.gmall.product.service.CateGoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CateGoryServiceImpl implements CateGoryService {

    @Autowired
    private CateGory1Mapper cateGory1Mapper;

    @Autowired
    private CateGory2Mapper cateGory2Mapper;

    @Autowired
    private CateGory3Mapper cateGory3Mapper;

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
}
