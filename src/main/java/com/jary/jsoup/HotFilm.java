package com.jary.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Jary on 2016/4/1.
 * Jsoup的简单爬虫 获取对象 筛选信息。
 */
public class HotFilm {
    public static void main(String args[]) throws IOException{
        getURLElement();
    }

    public static void getURLElement() throws IOException {
        float num = 0;
        int a=1;
        String URL= "https://movie.douban.com/top250?start=";
        for(int i=0;i<=250;i=i+25){
            Document doc = Jsoup.connect(URL + i).get();
            Elements element=doc.getElementsByClass("rating_num");

            for(Element ele : element){
                if(a<166){
                    num = add1(num,Float.parseFloat(ele.text().trim()));
                    add1(num,Float.parseFloat(ele.text()));
                    System.out.println(add1(num,Float.parseFloat(ele.text())));

                }else{
                    break;
                }
                a++;
            }
        }
        System.out.println(a);
    }
    public static float add1(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.add(b2).floatValue();
    }
}
