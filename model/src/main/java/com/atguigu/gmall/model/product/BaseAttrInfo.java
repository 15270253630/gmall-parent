package com.atguigu.gmall.model.product;

import com.atguigu.gmall.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;
import java.util.concurrent.locks.Lock;

@Data
@TableName("base_attr_info")
public class BaseAttrInfo extends BaseEntity {

    @TableField("attr_name")
    private String attrName;

    @TableField("category_id")
    private Long categoryId;

    @TableField("category_level")
    private Integer categoryLevel;

    @TableField(exist = false)
    private List<BaseAttrValue> attrValueList;

}
