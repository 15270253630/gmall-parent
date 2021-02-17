package com.atguigu.gmall.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.annotation.GmallCache;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private ListFeignClient listFeignClient;

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        // 保存sku
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();
        // 保存图片

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (CollectionUtil.isNotEmpty(skuImageList)) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuId);
                skuImageMapper.insert(skuImage);
            }
        }

        // 保存skuAttrValue

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (CollectionUtil.isNotEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }

        // 保存skuSaleAttrValue
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (CollectionUtil.isNotEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    @Override
    public IPage<SkuInfo> getPageSkuInfo(Long page, Long limit) {
        IPage<SkuInfo> skuInfoPage = new Page<>(page, limit);
        skuInfoMapper.selectPage(skuInfoPage, null);

        return skuInfoPage;
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
        listFeignClient.onSale(skuId);

    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
        listFeignClient.cancelSale(skuId);
    }

//    @Override
//    public SkuInfo getSkuInfoById(Long skuId) {
//        String skuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
//        String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
//
//        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
//        if (skuInfo == null) {
//            // 加入分布式锁
//            String lockTag = UUID.randomUUID().toString();
//            Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey,
//                    lockTag,
//                    RedisConst.SKULOCK_EXPIRE_PX2,
//                    TimeUnit.SECONDS);
//
//
//            if (lock){
//                log.info("拿到锁");
//                log.info("redis中没有从数据库中查询");
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                // 数据库中查询
//                skuInfo = getSkuInfoFromDB(skuId);
//                if (skuInfo != null) {
//                    redisTemplate.opsForValue().set(skuKey, skuInfo);
//                } else {
//                    // 防止缓存击穿
//                    redisTemplate.opsForValue().set(skuKey, skuInfo, RedisConst.SKU_VALUE_TEMPORARY_TIMEOUT, TimeUnit.MINUTES);
//                }
//                // 释放锁
//                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";// 脚本查询是否存在存在则删除否则返回0
//                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//                redisScript.setResultType(Long.class);
//                redisScript.setScriptText(script);
//                redisTemplate.execute(redisScript, Arrays.asList(lockKey),lockTag);
////                String delTag = (String) redisTemplate.opsForValue().get(lockKey);
////                if (lockTag.equals(delTag)){
////                    log.info("删除锁");
////                    redisTemplate.delete(lockKey);
////                }
//            } else {
//                log.info("没抢到锁开始自旋");
//                return getSkuInfoById(skuId);
//            }
//
//        } else {
//            log.info("从缓存中拿值");
//        }
//        return skuInfo;
//    }

//    private SkuInfo getSkuInfoFromDB(Long skuId) {
    @GmallCache
    @Override
    public SkuInfo getSkuInfoById(Long skuId) {
        // 查询所有skuInfo
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        // 查询所有skuImage
        QueryWrapper<SkuImage> imageQueryWrapper = new QueryWrapper<>();
        imageQueryWrapper.eq("sku_id", skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(imageQueryWrapper);
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }

    @GmallCache(suffix = "price")
    @Override
    public BigDecimal getSkuPriceById(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        BigDecimal price = skuInfo.getPrice();
        return price;
    }


    @GmallCache(suffix = "spuSaleAttrList")
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long spuId, Long skuId) {

        List<SpuSaleAttr> spuSaleAttrList = skuSaleAttrValueMapper.getSpuSaleAttrListCheckBySku(spuId, skuId);

        return spuSaleAttrList;
    }

    @GmallCache(suffix = "valuesAsSkuIdMaps")
    @Override
    public List<Map<String, Object>> getValuesAsSkuIdMaps(Long spuId) {
        List<Map<String, Object>> valuesAsSkuIdMaps = skuSaleAttrValueMapper.selectValuesAsSkuIdMaps(spuId);
        return valuesAsSkuIdMaps;
    }

    @Override
    public List<SearchAttr> getSkuAttrListById(Long skuId) {
        return skuAttrValueMapper.getSkuAttrListById(skuId);
    }


}
