package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.AttrInfoMapper;
import com.atguigu.gmall.product.mapper.AttrValueMapper;
import com.atguigu.gmall.product.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private AttrInfoMapper attrInfoMapper;
    @Autowired
    private AttrValueMapper attrValueMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        QueryWrapper<BaseAttrInfo> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("category_level", 3);
        infoQueryWrapper.eq("category_id", category3Id);
        List<BaseAttrInfo> baseAttrInfoList = attrInfoMapper.selectList(infoQueryWrapper);

        if (baseAttrInfoList != null && baseAttrInfoList.size() > 0) {
            for (BaseAttrInfo baseAttrInfo : baseAttrInfoList) {
                QueryWrapper<BaseAttrValue> valueQueryWrapper = new QueryWrapper<>();
                valueQueryWrapper.eq("attr_id", baseAttrInfo.getId());
                List<BaseAttrValue> baseAttrValueList = attrValueMapper.selectList(valueQueryWrapper);
                baseAttrInfo.setAttrValueList(baseAttrValueList);

            }
        }
        return baseAttrInfoList;
    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Long id = baseAttrInfo.getId();
        if (id != null && id > 0) {
            // 修改操作
            attrInfoMapper.updateById(baseAttrInfo);
            UpdateWrapper<BaseAttrValue> attrValueUpdateWrapper = new UpdateWrapper<>();

            for (BaseAttrValue baseAttrValue : baseAttrInfo.getAttrValueList()) {
                // 如果有id就是修改，如果没有就是新增
                if(baseAttrValue.getId() == null || baseAttrValue.getId() == 0){
                    baseAttrValue.setAttrId(baseAttrInfo.getId());
                    attrValueMapper.insert(baseAttrValue);
                } else {
                    attrValueMapper.updateById(baseAttrValue);
                }
            }
        } else {
            attrInfoMapper.insert(baseAttrInfo);
            Long attrInfoId = baseAttrInfo.getId();
            for (BaseAttrValue baseAttrValue : baseAttrInfo.getAttrValueList()) {
                baseAttrValue.setAttrId(attrInfoId);
                attrValueMapper.insert(baseAttrValue);
            }
        }

    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {

        return attrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attr_id", attrId));
    }

}
