package com.atguigu.gmall.test;

import com.atguigu.gmall.all.controller.ListController;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ClassPathTest {
        public static void main(String[] args) {
            ClassLoader classLoader = ListController.class.getClassLoader();
            URL path = classLoader.getResource("/static/json/a.text");

//            File upload = new File(path, "static/json/a.txt");
//            if (!upload.exists()) {
//                try {
//                    upload.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

            System.out.println(path);
        }

}
