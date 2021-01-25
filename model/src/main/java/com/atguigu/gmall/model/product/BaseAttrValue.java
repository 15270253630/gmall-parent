package com.atguigu.gmall.model.product;

import com.atguigu.gmall.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("base_attr_value")
public class BaseAttrValue extends BaseEntity {
    @TableField("value_name")
    private String valueName;

    @TableField("attr_id")
    private Long attrId;


}
