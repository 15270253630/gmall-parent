package com.atguigu.gmall.test.redission.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.result.Result;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TestController {
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/test01")
    public Integer test01() throws InterruptedException {
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", 1);
        Integer store = null;
        if (lock) {
            store = (Integer) redisTemplate.opsForValue().get("store");
            if (store > 0) {

                System.out.println("当前库存：" + store-- + "减完库存之后" + store);
                redisTemplate.opsForValue().set("store", store);

                redisTemplate.delete("lock");
                return store;
            }
        } else {
            // 每抢到锁的自旋去
            TimeUnit.SECONDS.sleep(3);
            store = test01();
        }
//        JSONObject

        System.out.println("库存已空");
        return store;
    }
}
