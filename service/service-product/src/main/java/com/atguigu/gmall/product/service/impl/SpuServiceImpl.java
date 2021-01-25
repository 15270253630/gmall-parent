package com.atguigu.gmall.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SpuService;
import com.atguigu.gmall.utils.MyCollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;


    @Override
    public IPage<SpuInfo> pageSpuByCategory3Id(Long page, Long limit, Long category3Id) {
        // 根据category3Id分页查询pageInfo
        IPage<SpuInfo> iPage = new Page<>(page,limit);
        spuInfoMapper.selectPage(iPage,new QueryWrapper<SpuInfo>().eq("category3_id",category3Id));
        // 通过pageInfo的id查询所有image
        for (SpuInfo spuInfo : iPage.getRecords()) {
            List<SpuImage> spuImageList = spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuInfo.getId()));
            spuInfo.setSpuImageList(spuImageList);

        }
        // 通过pageInfo的id查询所有SaleAttr
        for (SpuInfo spuInfo : iPage.getRecords()) {
            List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectList(new QueryWrapper<SpuSaleAttr>().eq("spu_id", spuInfo.getId()));
            spuInfo.setSpuSaleAttrList(spuSaleAttrList);
        }

        return iPage;
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {

        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        // 保存SpuInfo信息
        spuInfoMapper.insert(spuInfo);
        Long spuInfoId = spuInfo.getId();

        // 保存SpuImage信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(CollectionUtil.isNotEmpty(spuImageList)){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfoId);
                spuImageMapper.insert(spuImage);
            }
        }
        // 保存SpuSaleAttr信息
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (CollectionUtil.isNotEmpty(spuSaleAttrList)) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfoId);
                spuSaleAttrMapper.insert(spuSaleAttr);
                String saleAttrName = spuSaleAttr.getSaleAttrName();

                // 保存SpuSaleAttrValue信息
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (CollectionUtil.isNotEmpty(spuSaleAttrValueList)) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfoId);
                        spuSaleAttrValue.setSaleAttrName(saleAttrName);
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }

        }
    }

    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectList(new QueryWrapper<SpuSaleAttr>()
                .eq("spu_id", spuId));

        if (CollectionUtil.isNotEmpty(spuSaleAttrList)) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                QueryWrapper<SpuSaleAttrValue> spuSaleAttrValueQueryWrapper = new QueryWrapper<>();
                spuSaleAttrValueQueryWrapper.eq("spu_id", spuId)
                        .eq("base_sale_attr_id",spuSaleAttr.getBaseSaleAttrId());
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueMapper.selectList(spuSaleAttrValueQueryWrapper);
                spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
            }
        }
        return spuSaleAttrList;
    }


}

