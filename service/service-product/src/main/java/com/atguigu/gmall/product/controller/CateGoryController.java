package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.CateGoryService;
import com.atguigu.gmall.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class CateGoryController {

    @Autowired
    private CateGoryService cateGoryService;

    @ApiOperation("获取一级分类")
    @GetMapping("/getCategory1")
    public Result<List<BaseCategory1>> getCategory1(){
       List<BaseCategory1> list = cateGoryService.getCategory1();

        return Result.ok(list);
    }

    @ApiOperation("获取二级分类")
    @GetMapping("/getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable("category1Id") Long category1Id){
        List<BaseCategory2> list = cateGoryService.getCategory2(category1Id);

        return Result.ok(list);
    }


    @ApiOperation("获取三级分类")
    @GetMapping("/getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable("category2Id") Long category2Id){
        List<BaseCategory3> list = cateGoryService.getCategory3(category2Id);
        return Result.ok(list);
    }

}
