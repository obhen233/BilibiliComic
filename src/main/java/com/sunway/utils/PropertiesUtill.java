package com.sunway.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesUtill {
    
    private PropertiesUtill() {}
    
    
    private static Properties properties = null;
    
    static {
        properties = new Properties();
        try {
            //String dir =System.getProperty("user.dir");
            String dir = PropertiesUtill.class.getResource("/").getPath();
            properties.load(new FileInputStream(new File(dir + "/config.properties")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void main(String[] args) {
        System.out.println(PropertiesUtill.getProperty("test"));
    }
}
