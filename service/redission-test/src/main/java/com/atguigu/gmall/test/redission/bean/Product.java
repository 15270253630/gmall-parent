package com.atguigu.gmall.test.redission.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    private Long id;
    private Integer num;
    private BigDecimal price;
    private String name;
    private String category;

    public Product(Long id, Integer num, BigDecimal price, String name, String category) {
        this.id = id;
        this.num = num;
        this.price = price;
        this.name = name;
        this.category = category;
    }
}
