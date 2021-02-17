package com.atguigu.gmall.list.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.result.Result;
import com.baomidou.mybatisplus.extension.api.R;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/list")
public class ApiListController {

    @Autowired
    private ListService listService;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @RequestMapping("/getCategoryList")
    public List<JSONObject> getCategoryList(){
        List<JSONObject> list = listService.getCategoryList();
        return list;
    }

    @RequestMapping("/createIndex")
    public Result createIndex(){
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    @RequestMapping("/onSale/{skuId}")
    public void onSale(@PathVariable("skuId") Long skuId){
        listService.onSale(skuId);
    }
    @RequestMapping("/cancelSale/{skuId}")
    public void cancelSale(@PathVariable("skuId") Long skuId){
        listService.cancelSale(skuId);
    }

    @RequestMapping("/addHostScore/{skuId}")
    public void addHostScore(@PathVariable("skuId") Long skuId){
        listService.addHostScore(skuId);
    }


    @RequestMapping("/list")
    public SearchResponseVo list(@RequestBody SearchParam searchParam){
        SearchResponseVo searchResponseVo = listService.list(searchParam);
        return searchResponseVo;
    }

//    @RequestMapping("/searchTest")
//    public void searchTest(){
//        SearchRequest request = new SearchRequest();
//        request.indices("goods");
//        request.types("info");
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//
//        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//        boolQueryBuilder.filter(new TermQueryBuilder("category3Id",61));
//        boolQueryBuilder.must(new MatchQueryBuilder("title","华为"));
//        searchSourceBuilder.query(boolQueryBuilder);
//        request.source(searchSourceBuilder);
//        System.out.println(searchSourceBuilder.toString());
//        try {
//            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
//            SearchHits hits = response.getHits();
//            long totalHits = hits.getTotalHits();
//            System.out.println(totalHits);
//            SearchHit[] hits1 = hits.getHits();
//            for (SearchHit documentFields : hits1) {
//                String sourceAsString = documentFields.getSourceAsString();
//                System.out.println(sourceAsString);
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
