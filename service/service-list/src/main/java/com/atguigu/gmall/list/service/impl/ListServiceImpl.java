package com.atguigu.gmall.list.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.list.repository.GoodsRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.omg.CORBA.portable.ValueBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ListServiceImpl implements ListService {

    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public List<JSONObject> getCategoryList() {

        List<JSONObject> list = productFeignClient.getCategoryList();

        return list;
    }

    @Override
    public void onSale(Long skuId) {
        // 封装Goods
        Goods goods = new Goods();
        // 查询skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);

        // 查询品牌信息
        BaseTrademark baseTrademark = productFeignClient.getTrademarkById(skuInfo.getTmId());
        // 查询1/2/3级分类
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        // 查询对应的平台属性
        List<SearchAttr> searchAttrs = productFeignClient.getSkuAttrListById(skuId);

        goods.setTitle(skuInfo.getSkuName())
                .setPrice(skuInfo.getPrice().doubleValue())
                .setId(skuInfo.getId())
                .setHotScore(0l)
                .setCreateTime(new Date())
                .setDefaultImg(skuInfo.getSkuDefaultImg())
                .setTmId(baseTrademark.getId())
                .setTmName(baseTrademark.getTmName())
                .setTmLogoUrl(baseTrademark.getLogoUrl())
                .setCategory1Id(categoryView.getCategory1Id())
                .setCategory2Id(categoryView.getCategory2Id())
                .setCategory3Id(categoryView.getCategory3Id())
                .setCategory1Name(categoryView.getCategory1Name())
                .setCategory2Name(categoryView.getCategory2Name())
                .setCategory3Name(categoryView.getCategory3Name())
                .setAttrs(searchAttrs);

        goodsRepository.save(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Override
    public void addHostScore(Long skuId) {
        // 向Redis中更新商品热度值，并返回
        Long hostScore = redisTemplate.opsForValue().increment(RedisConst.SKUKEY_PREFIX + skuId + RedisConst.HOST_SCORE, 1L);
        // 当热度值达到1000的倍数就向ES中修改
        if (hostScore % 10 == 0) {
            Optional<Goods> o = goodsRepository.findById(skuId);
            Goods goods = o.get();
            goods.setHotScore(hostScore);
            goodsRepository.save(goods);
        }
    }

    @Override
    public SearchResponseVo list(SearchParam searchParam) {
        SearchResponseVo searchResponseVo = null;
        // 封装Request
        SearchRequest searchRequest = getSearchRequest(searchParam);
        // 执行检索
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析结果
            searchResponseVo = parseResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return searchResponseVo;
    }


    /**
     * 解析ES的响应结果
     *
     * @param response list页面商品结果
     * @return
     */
    private SearchResponseVo parseResponse(SearchResponse response) {
        List<Goods> goodsList = new ArrayList<>();
        SearchResponseVo searchResponseVo = new SearchResponseVo();

        // 解析Goods集合
        SearchHits hits = response.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] hitsResult = hits.getHits();

        if (ArrayUtil.isNotEmpty(hitsResult)) {
            for (SearchHit fields : hitsResult) {
                String sourceAsString = fields.getSourceAsString();
                Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);

                // 解析高亮，赋值给name
                Map<String, HighlightField> highlightFields = fields.getHighlightFields();
                if (MapUtil.isNotEmpty(highlightFields)) {
                    HighlightField highlightField = highlightFields.get("title");
                    Text[] titles = highlightField.getFragments();
                    Text titleText = titles[0];
                    String title = titleText.string();
                    goods.setTitle(title);
                }
                goodsList.add(goods);
            }
        }

        // 解析tm集合
        List<SearchResponseTmVo> trademarkList = null;

        Aggregations aggregations = response.getAggregations();
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) aggregations.get("tmIdAgg");
        List<? extends Terms.Bucket> buckets = tmIdAgg.getBuckets();
        if (CollectionUtil.isNotEmpty(buckets)) {
            trademarkList = buckets.stream().map(bucket -> {
                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
                Long tmId = (Long) ((Terms.Bucket) bucket).getKey();

                ParsedStringTerms tmNameAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().get("tmNameAgg");
                List<? extends Terms.Bucket> tmNameAggBuckets = tmNameAgg.getBuckets();
                String tmName = (String) tmNameAggBuckets.get(0).getKey();

                List<? extends Terms.Bucket> tmLogoUrlBuckets = ((ParsedStringTerms) ((Terms.Bucket) bucket)
                        .getAggregations().get("tmLogoUrlAgg")).getBuckets();
                String tmLogoUrl = (String) tmLogoUrlBuckets.get(0).getKey();

                searchResponseTmVo.setTmId(tmId);
                searchResponseTmVo.setTmName(tmName);
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
                return searchResponseTmVo;
            }).collect(Collectors.toList());
        }


        // 解析平台属性集合
        List<SearchResponseAttrVo> attrsList = null;
        ParsedNested attrsAgg = (ParsedNested) response.getAggregations().get("attrsAgg");
        List<? extends Terms.Bucket> attrIdAggBuckets = ((ParsedLongTerms) attrsAgg.getAggregations().get("attrIdAgg")).getBuckets();
        if (CollectionUtil.isNotEmpty(attrIdAggBuckets)) {
            attrsList = attrIdAggBuckets.stream().map(bucket -> {
                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                Long attrId = (Long) ((Terms.Bucket) bucket).getKey();

                ParsedStringTerms attrNameAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().get("attrNameAgg");
                List<? extends Terms.Bucket> attrNameAggBuckets = attrNameAgg.getBuckets();
                String attrName = (String) attrNameAggBuckets.get(0).getKey();

                ParsedStringTerms attrValueAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().get("attrValueAgg");
                List<? extends Terms.Bucket> attrValueAggBuckets = attrValueAgg.getBuckets();
                List<String> values = attrValueAggBuckets.stream().map(valueBucket -> {
                    String keyAsString = ((Terms.Bucket) valueBucket).getKeyAsString();
                    return keyAsString;
                }).collect(Collectors.toList());

                searchResponseAttrVo.setAttrId(attrId);
                searchResponseAttrVo.setAttrName(attrName);
                searchResponseAttrVo.setAttrValueList(values);
                return searchResponseAttrVo;
            }).collect(Collectors.toList());
        }


        searchResponseVo.setTrademarkList(trademarkList);
        searchResponseVo.setTotal(totalHits);
        searchResponseVo.setGoodsList(goodsList);
        searchResponseVo.setAttrsList(attrsList);
        return searchResponseVo;
    }

    /**
     * 封装检索请求，DSL语句
     *
     * @param searchParam 前端封装请求检索参数
     * @return 检索的请求对象
     */
    private SearchRequest getSearchRequest(SearchParam searchParam) {

        // 解析前端参数
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        Integer pageNo = searchParam.getPageNo();
        Integer pageSize = searchParam.getPageSize();
        String trademark = searchParam.getTrademark();
        String[] props = searchParam.getProps();
        String order = searchParam.getOrder();

        SearchRequest request = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 根据3级分类检索商品
        if (null != category3Id && category3Id != 0) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", category3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 根据关键字检索商品
        if (!StringUtils.isEmpty(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", keyword);
            boolQueryBuilder.must(matchQueryBuilder);

            // 高亮显示
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.preTags("<span style=\"color:red\">");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }


        // 添加商标筛选条件
        if (!StringUtils.isEmpty(trademark)) {
            String[] trademarkNames = trademark.split(":");
            if (ArrayUtil.isNotEmpty(trademarkNames)) {
                String trademarkId = trademarkNames[0];
                String trademarkName = trademarkNames[1];
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("tmId", trademarkId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        // 添加属性筛选条件
        if (ArrayUtil.isNotEmpty(props)) {
            for (String prop : props) {
                String[] propNames = prop.split(":");
                Long propId = Long.parseLong(propNames[0]);
                String propValue = propNames[1];
                String propName = propNames[2];

                TermQueryBuilder termQueryBuilderId = new TermQueryBuilder("attrs.attrId", propId);
                TermQueryBuilder termQueryBuilderName = new TermQueryBuilder("attrs.attrName", propName);
                TermQueryBuilder termQueryBuilderValue = new TermQueryBuilder("attrs.attrValue", propValue);

                BoolQueryBuilder boolQueryBuilderinner = new BoolQueryBuilder();
                boolQueryBuilderinner.filter(termQueryBuilderId).filter(termQueryBuilderName).filter(termQueryBuilderValue);

                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", boolQueryBuilderinner, ScoreMode.None);
                boolQueryBuilder.must(nestedQueryBuilder);
            }
        }


        // 添加排序条件
        if (!StringUtils.isEmpty(order)) {
            String sortName = "";
            String[] split = order.split(":");

            if (split != null && split.length == 2) {
                String type = split[0];
                String sort = split[1];
                switch (type) {
                    case "1":
                        sortName = "hotScore";
                        break;
                    case "2":
                        sortName = "price";
                        break;
                }
                searchSourceBuilder.sort(sortName, "asc".equalsIgnoreCase(sort)?SortOrder.ASC:SortOrder.DESC);
            }
        } else {
            // 如果没有选择排序则默认按热度降序
            searchSourceBuilder.sort("hotScore",SortOrder.DESC);
        }


        // 抽取商标
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId"); // 聚合Id
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName")); // 子聚合Name
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl")); // 子聚合URL
        searchSourceBuilder.aggregation(tmIdAgg);

        // 抽取平台属性
        NestedAggregationBuilder attrsAgg = AggregationBuilders.nested("attrsAgg", "attrs"); // 内嵌聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");// 子聚合Id
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")); // 子子聚合value
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName")); // 子子聚合Name


        searchSourceBuilder.query(boolQueryBuilder);
        attrsAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(attrsAgg);


        log.info(searchSourceBuilder.toString());
        request.source(searchSourceBuilder);

        return request;
    }
}
