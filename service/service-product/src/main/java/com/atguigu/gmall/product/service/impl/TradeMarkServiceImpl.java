package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.TradeMarkMapper;
import com.atguigu.gmall.product.service.TradeMarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeMarkServiceImpl implements TradeMarkService {
    @Autowired
    private TradeMarkMapper tradeMarkMapper;

    @Override
    public IPage<BaseTrademark> baseTrademark(Long page, Long limit) {
        IPage<BaseTrademark> iPage = new Page<>(page,limit);
        tradeMarkMapper.selectPage(iPage,null);
        return iPage;
    }
}
