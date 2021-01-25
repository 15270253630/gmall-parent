package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.AttrService;
import com.atguigu.gmall.result.Result;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class AttrController {

    @Autowired
    private AttrService attrService;

    @ApiOperation("分类id获取平台属性")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> attrInfoList(@PathVariable("category1Id") Long category1Id,
                                             @PathVariable("category2Id") Long category2Id,
                                             @PathVariable("category3Id") Long category3Id){
        List<BaseAttrInfo> list = attrService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(list);
    }

    @ApiOperation("编辑平台属性")
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        attrService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @ApiOperation("通过id获取平台属性")
    @GetMapping("/getAttrValueList/{attrId}")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable("attrId") String attrId){
        List<BaseAttrValue> attrValueList = attrService.getAttrValueList(attrId);
        return Result.ok(attrValueList);
    }



}
