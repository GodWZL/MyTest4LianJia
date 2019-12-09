package com.lianjia.controller;


import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HouseController {
    //初始页面url
    private static String hzLianJiaUrl = "https://hz.lianjia.com/ershoufang/";
    //下一页url
    private static String nextPageUrl;
    //总页面数
    private static int totalPage;
    //当前页面数
    private static int nowPage;
    //存放包含当前页面数目信息的母串
    private static String pageData;
    //当前页面的html文件对象
    private static Document document;

    public static void main(String[] args) throws IOException {
        System.setProperty("HADOOP_USER_NAME", "root");
        //文件存储位置
        OutputStreamWriter saveFileStream = getFile();
        //获得首页面的字符串
        String htmlSource = getSource(hzLianJiaUrl);
        //获得首页面的document对象
        document = getDocument(htmlSource);
        //通过class获得<div class="page-box house-lst-page-box">对象数组
        Elements elements = document.getElementsByClass("page-box house-lst-page-box");
        //获得包含页面信息的element对象
        //因为这里的class具有唯一性，数组中仅有唯一的element对象
        Element elementPage = elements.first();
        //获得总页面数
        pageData = elementPage.attr("page-data");
        String[] totalPages1 = pageData.split(",")[0].split(":");
        totalPage = Integer.valueOf(totalPages1[1]);
        //初始化当前页面数目
        nowPage = 1;
        //获得每个页面的所有在售房源的信息
        while (nowPage < totalPage) {
            //最终返回json串的map
            HashMap<String, Object> resText = new HashMap<String, Object>();
            Elements sellList = document.getElementsByClass("clear LOGVIEWDATA LOGCLICKDATA");
            for (Element eles : sellList) {

                //获得每个房源的url
                Element sellHouse = eles.getElementsByClass("noresultRecommend img LOGCLICKDATA").first();
                String sellHouseUrl = sellHouse.attr("href");
                Document houseDocument = getDocument(getSource(sellHouseUrl));
                //获取房屋总价
                Elements priceDocuments = houseDocument.getElementsByClass("price");
                Element priceDocument = priceDocuments.first();
                String 房屋总价 = priceDocument.getElementsByClass("total").text();
                resText.put("房屋总价", 房屋总价);
                //每平米均价
                String averagePrice = priceDocument.getElementsByClass("text").first().getElementsByClass("unitPrice").text();
                averagePrice = averagePrice.substring(0, averagePrice.indexOf("元"));
                resText.put("每平米均价", averagePrice);
                //房屋位置
                String hourseSite = "";
                //小区名称
                Element houseLocalNameDocument = houseDocument.getElementsByClass("communityName").first();
                String communityName = houseLocalNameDocument.getElementsByClass("info").text();
                //所在区域
                Element houseLocalAreaDocument = houseDocument.getElementsByClass("areaName").first();
                Elements areas = houseLocalAreaDocument.getElementsByClass("info");
                for (Element ele : areas) {
                    //所属城区 + 区域
                    hourseSite = hourseSite + ele.text() + " ";
                }
                hourseSite = hourseSite + communityName;
                resText.put("房屋位置", hourseSite);
                //房屋的基本属性
                Element baseinform = houseDocument.getElementsByClass("introContent").first();
                Elements baseinforms = baseinform.getElementsByClass("content").first().children().first().children();
                //所在楼层
                resText.put("所在楼层", baseinforms.select("li").get(1).text().trim().replaceAll("所在楼层",""));
                //建筑面积
                resText.put("建筑面积", baseinforms.select("li").get(2).text().trim().replaceAll("建筑面积","").replaceAll("㎡",""));
                //房屋朝向
                resText.put("房屋朝向", baseinforms.select("li").get(6).text().trim().replaceAll("房屋朝向",""));
                //挂牌时间
                Element transaction = houseDocument.getElementsByClass("transaction").first();
                Element transactions = transaction.getElementsByClass("content").first().children().first();
                resText.put("挂牌时间", transactions.select("li").get(0).text().trim().replaceAll("挂牌时间",""));
                //生成返回json串
                JSONObject resJson = new JSONObject(resText);
                saveFileStream.write(resJson.toJSONString() + "\n");
                saveFileStream.flush();
                System.out.println(resJson.toJSONString());
            }
            nowPage++;
//            //线程休眠，降低服务器访问压力
//            try {
//                Thread.sleep(60);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //Test
//            if(nowPage >= 1){
//                break;
//            }
            //生成下一页的url
            System.out.println();
            System.out.println("nowPage:"+nowPage);
            System.out.println();
            nextPageUrl = hzLianJiaUrl + "pg" + nowPage + "/";
            //跳转到下一个页面
            document = getDocument(getSource(nextPageUrl));
        }
        saveFileStream.close();
    }

    /**
     * 通过httpClient获得对应的url页面信息
     */
    public static String getSource(String url) throws IOException {
        String html = new String();
        //创建Http请求实例
        HttpGet httpget = new HttpGet(url);
        // 模拟浏览器，防止网站的反爬虫检测
        httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36");
        CloseableHttpResponse response = null;
        // 使用默认的HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            response = httpClient.execute(httpget);
            //  当服务器成过响应客户端请求时（返回200表示成功）
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 获取服务器响应实体的内容
                html = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return html;
    }

    /**
     * 通过Jsoup获得页面的document对象
     *
     * @param htmlSource
     */
    public static Document getDocument(String htmlSource) {
        Document document = Jsoup.parse(htmlSource);
        return document;
    }
    /**
     * 创建新文件
     *
     */
    public static OutputStreamWriter getFile(){
        //根据日期创建一个文件输入流
        String outputPath = "source/output";
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String FileName = sdf.format(nowDate);
        File file = new File(outputPath, FileName);
        OutputStreamWriter saveFileStream = null;
        try {
            saveFileStream = new OutputStreamWriter(new FileOutputStream(file,true),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return saveFileStream;
    }
}
