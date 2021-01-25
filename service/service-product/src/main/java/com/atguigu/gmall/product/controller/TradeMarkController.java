package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.TradeMarkService;
import com.atguigu.gmall.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/admin/product")
public class TradeMarkController {
    @Autowired
    private TradeMarkService tradeMarkService;

    @ApiOperation("分页查询商标")
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result<IPage<BaseTrademark>> baseTrademark(@PathVariable("page") Long page,
                                               @PathVariable("limit") Long limit){
        IPage<BaseTrademark> iPage =  tradeMarkService.baseTrademark(page,limit);
        return Result.ok(iPage);
    }


}
