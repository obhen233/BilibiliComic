package com.sunway.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sunway.main.Manag;
import com.sunway.utils.MD5Util;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BilibiliApi {
    private BilibiliApi() {}
    private static Map<String,String> header = null;
    private static Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Logger log =  LoggerFactory.getLogger(BilibiliApi.class);
    static{
        header = new HashMap<String,String>();
        header.put("Host", "manga.bilibili.com");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:97.0) Gecko/20100101 Firefox/97.0");
        header.put("Accept", "application/json, text/plain, */*");
        header.put("Content-Type", "application/json;charset=utf-8");
        header.put("Origin", "https://manga.bilibili.com");
        header.put("Connection", "keep-alive");
        if(!"".equals(Manag.SESSDATA)) {
            header.put("Cookie", "SESSDATA=" + Manag.SESSDATA);
        }
    }
    
    
    private static String post(String uri,Map<String,String> param){
        String json = gson.toJson(param);
        RequestBody entity = RequestBody.create(JSON, json);
       
        try {
            header.put("Content-Length", entity.contentLength()+"");
            Request request = new Request.Builder()
                    .url(uri).headers(Headers.of(header))
                    .post(entity)
                    .build();
            Response response = null;
            ResponseBody responseBody = null;
            
            OkHttpClient httpClient = new OkHttpClient();
            response = httpClient.newCall(request).execute();
            responseBody = response.body();
            if (responseBody != null && response.isSuccessful())
                try {
                    byte[] result = responseBody.bytes();
                    String str = new String(result,"UTF-8");
                    return str;
                } catch (IOException e) {
                    log.error("post",e);
                }
            else {
                log.info("请求失败,请检查"); 
            }
        } catch (IOException e) {
            log.error("post",e);
        }
        return "{}";
    }
    
   
    
  
    public static List<Map<String,Object>> getImgToken(String epId,List<String> imgPathList) {
        String apiUrl = "https://manga.bilibili.com/twirp/comic.v1.Comic/ImageToken?device=pc&platform=web";
        String refererUrl = "https://manga.bilibili.com/mc%s/%s?from=manga_detail";
        header.put("Referer", String.format(refererUrl, Manag.COMICID,epId));
        Map<String,String> param = new HashMap<String,String>();
        param.put("urls", gson.toJson(imgPathList));
        String result = post(apiUrl,param);
        
        Map<String,Object> res = gson.fromJson(result,HashMap.class);
        Double code = (Double)res.get("code");
        if(new Double(0).equals(code)) {
            ArrayList data = (ArrayList)res.get("data");
            return data;
        }else {
            return new ArrayList<Map<String,Object>>();
        }
    }
    
    public static List<Map<String,Object>> getImageIndex(String epId) {
        String apiUrl = "https://manga.bilibili.com/twirp/comic.v1.Comic/GetImageIndex?device=pc&platform=web";
        String refererUrl = "https://manga.bilibili.com/mc%s/%s?from=manga_detail";
        header.put("Referer", String.format(refererUrl, Manag.COMICID,epId));
        Map<String,String> param = new HashMap<String,String>();
        param.put("ep_id", epId);
        String result = post(apiUrl,param);
        Map<String,Object> res = gson.fromJson(result,HashMap.class);
        Double code = (Double)res.get("code");
        if(new Double(0).equals(code)) {
            LinkedTreeMap data = ((LinkedTreeMap)res.get("data"));
            ArrayList images = (ArrayList)data.get("images");
            return images;
            
        }else {
            return new ArrayList<Map<String,Object>>();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public static List<Map<String,Object>> comicDetail() {
        String apiUrl = "https://manga.bilibili.com/twirp/comic.v1.Comic/ComicDetail?device=pc&platform=web";
        String refererUrl = "https://manga.bilibili.com/detail/mc%s?from=manga_homepage";
        header.put("Referer", String.format(refererUrl, Manag.COMICID));
        Map<String,String> param = new HashMap<String,String>();
        param.put("comic_id", Manag.COMICID);
        String result = post(apiUrl,param);
        System.out.println(result);
        Map<String,Object> res = gson.fromJson(result,HashMap.class);
        Double code = (Double)res.get("code");
        if(new Double(0).equals(code)) {
            LinkedTreeMap data = ((LinkedTreeMap)res.get("data"));
            ArrayList ep_list = (ArrayList)data.get("ep_list");
            return ep_list;
        }else {
            return new ArrayList<Map<String,Object>>();
        }
    }
    
    public static String downloadImg(String imageUri,String token,String saveDirPath) {
        Request request = new Request.Builder()
                .url(imageUri + "?token=" + token)
                .get()
                .build();
        Response response = null;
        ResponseBody responseBody = null;
        OkHttpClient httpClient = new OkHttpClient();
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e1) {
            log.error("downloadImg",e1);
        }
        responseBody = response.body();
        if (responseBody != null && response.isSuccessful())
            try {
                byte[] result = responseBody.bytes();
                String etag = response.header("Etag");
                String md5 = MD5Util.MD5(result);
                log.info("etag=" + etag +", md5=" + md5);
                return getFileByBytes(result, saveDirPath, imageUri.substring(imageUri.lastIndexOf("/")+1,  imageUri.contains("@") ? imageUri.lastIndexOf("@"):imageUri.length()));
            } catch (IOException e) {
                log.error("downloadImg",e);
            }
        else {
            log.info("请求失败,请检查"); 
        }
        return "";
    }
    
  //将Byte数组转换成文件
    private static String getFileByBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            return filePath + File.separator + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

}
