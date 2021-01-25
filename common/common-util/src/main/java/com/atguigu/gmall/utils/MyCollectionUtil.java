package com.atguigu.gmall.utils;



import java.util.Collection;

public class MyCollectionUtil {

    /**
     * 集合判空
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection collection){
        return collection != null && collection.size() > 0;
    }
}
