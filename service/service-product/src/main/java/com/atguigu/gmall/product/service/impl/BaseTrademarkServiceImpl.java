package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseTrademarkServiceImpl implements BaseTrademarkService {
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Override
    public BaseTrademark getTrademarkById(Long tmId) {
        return baseTrademarkMapper.selectById(tmId);
    }
}
