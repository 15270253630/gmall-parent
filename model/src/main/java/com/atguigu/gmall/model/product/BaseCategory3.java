package com.atguigu.gmall.model.product;

import com.atguigu.gmall.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("base_category3")
public class BaseCategory3 extends BaseEntity {

    @TableField("name")
    private String name;

    @TableField("category2_id")
    private Long category1Id;
}
