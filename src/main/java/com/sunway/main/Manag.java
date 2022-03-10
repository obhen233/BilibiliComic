package com.sunway.main;

import com.sunway.spider.ComicSpider;

public class Manag {
    public static String SESSDATA = "";
    public static String COMICID = "";
    
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("请至少输入comicID");
            return;
        }
        ComicSpider.run();
    }
   
}
