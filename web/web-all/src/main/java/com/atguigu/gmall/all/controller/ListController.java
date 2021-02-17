package com.atguigu.gmall.all.controller;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ListController {

    @Autowired
    private ListFeignClient listFeignClient;


    @RequestMapping("/index.html")
    public String index(Model model, HttpServletRequest servletRequest) {
        List<JSONObject> list = listFeignClient.getCategoryList();

        String categoryJson = JSONObject.toJSONString(list);
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        try {
            String path = resourceLoader.getResource("classpath:/static/json").getURL().getPath();
            File file = new File(path, "category.json");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(categoryJson.getBytes("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println(path);
        // 模拟静态化，真实项目必定不是在查询时候静态化。
//        File file = new File(path + "");
//        if (file.)
//        FileOutputStream os = new FileOutputStream()


        model.addAttribute("list", list);
        return "/index/index";
    }

    @RequestMapping(value = {"/list.html", "/search.html"})
    public String list(SearchParam searchParam, Model model) {

        SearchResponseVo searchResponseVo = listFeignClient.list(searchParam);

        model.addAttribute("goodsList", searchResponseVo.getGoodsList());
        model.addAttribute("trademarkList", searchResponseVo.getTrademarkList());
        model.addAttribute("attrsList", searchResponseVo.getAttrsList());
        String urlParam = getUrlParam(searchParam);
        model.addAttribute("urlParam", urlParam);

        // 品牌面包屑
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)) {
            String[] split = trademark.split(":");
            String trademarkParam = split[1];
            model.addAttribute("trademarkParam", trademarkParam);
        }

        // 销售属性面包屑
        String[] props = searchParam.getProps();
        if (ArrayUtil.isNotEmpty(props)) {

            ArrayList<SearchAttr> propsParamList = new ArrayList<>();
            for (String prop : props) {
                String[] strings = prop.split(":");
                Long propId = Long.parseLong(strings[0]);
                String propValue = strings[1];
                String propName = strings[2];

                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(propId);
                searchAttr.setAttrName(propName);
                searchAttr.setAttrValue(propValue);
                propsParamList.add(searchAttr);
            }
            model.addAttribute("propsParamList", propsParamList);
        }

        // 处理排序
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)) {
            Map<String,Object> orderMap = this.dealOrder(order);
            model.addAttribute("orderMap",orderMap);
        }

        return "/list/index.html";
    }

    /**
     * 处理排序
     * @param order
     * @return
     */
    private Map<String, Object> dealOrder(String order) {
        String type = order.split(":")[0];
        String sort = order.split(":")[1];
        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("type",type);
        orderMap.put("sort",sort);

        return orderMap;
    }

    private String getUrlParam(SearchParam searchParam) {
        String urlParam = "list.html?";
        Long category3Id = searchParam.getCategory3Id();

        String keyword = searchParam.getKeyword();

        String trademark = searchParam.getTrademark();

        String[] props = searchParam.getProps();

        String order = searchParam.getOrder();

        if (category3Id != null) {
            urlParam += "category3Id=" + category3Id;
        }

        if (!StringUtils.isEmpty(keyword)) {
            urlParam += "keyword=" + keyword;
        }

        if (!StringUtils.isEmpty(trademark)) {
            urlParam += "&trademark=" + trademark;
        }

        if (ArrayUtil.isNotEmpty(props)) {
            for (String prop : props) {
                urlParam += "&props=" + prop;
            }
        }


        return urlParam;
    }

}
