package com.atguigu.gmall.test.redission.controller;

import com.atguigu.gmall.test.redission.bean.Product;
import com.google.common.collect.Lists;
import jdk.internal.dynalink.beans.StaticClass;
import org.apache.tomcat.jni.Local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTest {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("Barbara", "James", "Mary", "John", "Patricia", "Patricia", "Robert", "Michael", "Linda");


        // 去重
//        List<String> collect = strings.stream().distinct().collect(Collectors.toList());
//        System.out.println(collect);

        // 截取前面多少个元素
//        List<String> collect = strings.stream().limit(3).collect(Collectors.toList());
//        System.out.println(collect);

        // skip ：是从流中去掉前面多少个元素
        // limit：是从流中截取多少个元素
        // 两个组合使用可以起到分页效果
//        Stream<String> limit = strings.stream().skip(2).limit(2);
//        List<String> collect1 = limit.collect(Collectors.toList());
//        System.out.println(collect1);


        // map：对集合每个元素进行操作
//        List<String> collect = strings.stream().map(str -> {
//            return str = "a";
//        }).collect(Collectors.toList());

        // 扁平化处理，将多层集合或数据拆成一个集合
        // [[a,b,c],[d,e,f],[g,h]]
        // [a,b,c,d,e,f,g,h]
//        List<Character> collect = strings.stream().flatMap(StreamTest::toCharArrayStream).collect(Collectors.toList());
//
//        strings.stream().forEach(System.out::println);

        // 排序
//        List<String> collect = strings.stream().sorted().collect(Collectors.toList());

//        boolean b = strings.stream().anyMatch(str -> str.length() >10);
//        System.out.println(b);


//        System.out.println(collect);

//        Integer.compare()

        Product prod1 = new Product(1L, 1, new BigDecimal("15.5"), "面包", "零食");
        Product prod2 = new Product(2L, 2, new BigDecimal("20"), "饼干", "零食");
        Product prod3 = new Product(3L, 3, new BigDecimal("30"), "月饼", "零食");
        Product prod4 = new Product(4L, 3, new BigDecimal("10"), "青岛啤酒", "啤酒");
        Product prod5 = new Product(5L, 10, new BigDecimal("15"), "百威啤酒", "啤酒");
        List<Product> products = Lists.newArrayList(prod1, prod2, prod3, prod4, prod5);

        Map<String, List<Product>> collect = products.stream().collect(Collectors.groupingBy(product -> {
            return product.getCategory();
        }));

        Map<String, List<Product>> collect1 = products.stream().collect(Collectors.groupingBy(Product::getCategory));

        Map<String, List<Product>> collect2 = products.stream().collect(Collectors.groupingBy(product -> {
            Integer num = product.getNum();
            if (num > 3) {
                return "3";
            } else {
                return "other";
            }
        }));
        System.out.println(collect2 + "-----------------------");

        System.out.println(collect.get("啤酒"));


    }


    private static Stream<Character> toCharArrayStream(String str){
        char[] chars = str.toCharArray();
        ArrayList<Character> characters = new ArrayList<>();
        for (char aChar : chars) {
            characters.add(aChar);
        }
        return characters.stream();
    }

    private static void filterMethod(List<String> strings) {
        // 过滤:对流里面每个元素进行判断。留下返回满足条件的
        // collect将流转换成集合
        List<String> a = strings.stream().filter(str -> str.contains("a")).collect(Collectors.toList());
        System.out.println(a);
    }
}


