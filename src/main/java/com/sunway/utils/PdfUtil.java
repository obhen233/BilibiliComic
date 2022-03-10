package com.sunway.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfUtil {
    public static void mergePDF(List<File> files, String outputPath, String outputFileName)  throws Exception{
        String sep = File.separator;
        Document document = null;
        PdfCopy copy = null;    
        PdfReader reader = null;
        try {
            //首先验证输出文件是否存在，若不存在创建文件
            String outFilePath=outputPath + sep +outputFileName;
            //先验证是否存在输出文件夹，不存在则创建
            File fileDir = new File(outputPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            //验证 合并后的文件是否存在若 不存在则新建文件
            File outFile=new File(outFilePath);       
            if(outFile.exists()){
                outFile.createNewFile();
            }
            document = new Document(new PdfReader(files.get(0).getAbsolutePath()).getPageSize(1));
            copy = new PdfCopy(document, new FileOutputStream(outFilePath));
            document.open();
            //循环导入合并pdf文件
            for (int i = 0; i < files.size(); i++) {
                reader = new PdfReader(files.get(i).getAbsolutePath());
                int numberOfPages = reader.getNumberOfPages();
                for (int j = 1; j <= numberOfPages; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (document != null)
                document.close();
            if (reader != null)
                reader.close();
            if (copy != null)
                copy.close();
        }
    }
    
    
    public static void imagesToPdf(String outPdfFilepath, List<String> imageFilePaths) throws Exception {

        System.out.println("进入图片合成PDF工具方法");

         File file = new File(outPdfFilepath);
         // 第一步：创建一个document对象。
         Document document = new Document();
         document.setMargins(0, 0, 0, 0);
         // 第二步：
         // 创建一个PdfWriter实例，
         PdfWriter.getInstance(document, new FileOutputStream(file));
         // 第三步：打开文档。
         document.open();
         // 第四步：在文档中增加图片。
        

         for (int i = 0; i < imageFilePaths.size(); i++) {
             if(imageFilePaths.get(i) == null || "".equals(imageFilePaths.get(i))) continue;
             File imageFile = new File(imageFilePaths.get(i));
             if(!imageFile.exists()) continue;
             if (imageFile.getName().toLowerCase().endsWith(".bmp")
                     || imageFile.getName().toLowerCase().endsWith(".jpg")
                     || imageFile.getName().toLowerCase().endsWith(".jpeg")
                     || imageFile.getName().toLowerCase().endsWith(".gif")
                     || imageFile.getName().toLowerCase().endsWith(".png")) {
                 String temp = imageFile.getAbsolutePath();
                 Image img = Image.getInstance(temp);
                 img.setAlignment(Image.ALIGN_CENTER);
                 img.scaleAbsolute(750, 850);// 直接设定显示尺寸
                 // 根据图片大小设置页面，一定要先设置页面，再newPage（），否则无效
                 document.setPageSize(new Rectangle(img.getWidth(), img.getHeight()));
                 //document.setPageSize(new Rectangle(750, 850));
                 document.newPage();
                 document.add(img);
             }
         }
         // 第五步：关闭文档。
         document.close();
     }
}
