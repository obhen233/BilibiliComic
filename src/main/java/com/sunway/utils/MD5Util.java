package com.sunway.utils;

import java.security.MessageDigest;


public class MD5Util {
   
       
        /** 字符串，计算MD5值 */
        public static String MD5(String data)
        {
            return MD5(data.getBytes());
        }
        
        /** 字节数组，计算MD5值 */
        public static String MD5(byte[] data)
        {
            try
            {
                // 获取data的MD5摘要
                MessageDigest digest = MessageDigest.getInstance("MD5");
                // mdInst.update(content.getBytes());
                digest.update(data);
                byte[] array = digest.digest();
                
                // 转换为十六进制的字符串形式
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < array.length; i++)
                {
                    String shaHex = Integer.toHexString(array[i] & 0xFF);
                    if (shaHex.length() < 2)
                    {
                        buf.append(0);
                    }
                    buf.append(shaHex);
                }
                return buf.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "";
            }
        }
     
        /** 加密解密算法 执行一次加密，两次解密 */
        public static String convertMD5(String inStr)
        {
            
            char[] a = inStr.toCharArray();
            for (int i = 0; i < a.length; i++)
            {
                a[i] = (char) (a[i] ^ 't');
            }
            String s = new String(a);
            return s;
            
        }
        


}
