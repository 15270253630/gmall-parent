package com.atguigu.gmall.model.product;

import com.atguigu.gmall.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("base_category2")
public class BaseCategory2 extends BaseEntity {

    @TableField("name")
    private String name;

    @TableField("category1_id")
    private Long category1Id;
}
