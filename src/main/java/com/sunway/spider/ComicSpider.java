package com.sunway.spider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sunway.api.BilibiliApi;
import com.sunway.utils.PdfUtil;

public class ComicSpider {
    
    private static final String DIR = "D:\\Download";
    
    public static void run() {
        List<Map<String,Object>> eplist = BilibiliApi.comicDetail();
        
        if(eplist != null && eplist.size() > 0) {
           
           for(int i = eplist.size()-1; i >= 0; i--) {
               List<String> picPathList = new ArrayList<String>();
               Map<String,Object> ep = eplist.get(i);
                String title = (String)ep.get("short_title");
                File chapterPdf = new File(DIR + File.separator + title + ".pdf");
                if(chapterPdf.exists()) {
                    continue;
                }
                String epId = ep.get("id").toString();
                List<Map<String,Object>> images = BilibiliApi.getImageIndex(epId);
                for(Map<String,Object> image :images) {
                    String path = (String)image.get("path");
                    String x = image.get("x").toString();
                    List<String> srcs = new ArrayList<String>();
                    //srcs.add(path + "@" + x + "w" + path.substring(path.lastIndexOf("."), path.length()));
                    srcs.add(path);
                    List<Map<String,Object>> imgMaps = BilibiliApi.getImgToken(epId, srcs);
                    for(Map<String,Object> map :imgMaps) {
                        picPathList.add(BilibiliApi.downloadImg((String)map.get("url"), (String)map.get("token"), DIR));
                    }
                }
                
                try {
                    PdfUtil.imagesToPdf(DIR + File.separator + title + ".pdf", picPathList);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
}
