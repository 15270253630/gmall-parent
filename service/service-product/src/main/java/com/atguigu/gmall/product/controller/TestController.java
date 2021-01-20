package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.result.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class TestController {

    @GetMapping("/getCategory1")
    public Result<String> getCategory1(){


        return Result.ok(new String("ssss"));
    }

}
