package com.atguigu.gmall.annotation;

import com.atguigu.gmall.enums.CacheTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    // 为那类数据进行缓存，如sku
    String prefix() default "sku:";
    // 以什么类型进行缓存
    CacheTypeEnum cacheType() default CacheTypeEnum.STR;
    // 缓存的是那个字段
    String suffix() default "info";
}
