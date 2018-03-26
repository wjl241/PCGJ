package util;


import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
 
import org.apache.commons.codec.binary.Hex;  
 
public class MD5Util {  
   static MessageDigest MD5 = null;  
     
   static{  
       try{  
           MD5 = MessageDigest.getInstance("MD5");  
       }catch(NoSuchAlgorithmException e){  
           e.printStackTrace();  
       }  
   }  
     
   public static void main(String[] args){  
	   //String filePath = args[0];  
           File file = new File("d:\\1.png");  
         //  File file = new File(filePath);  
           if(!file.exists()){  
               System.out.println("incorrect file path!");  
           }else{  
               String md5 = getMD5(file);  
               System.out.println("===============MD5===============");  
               System.out.println(md5);  
               System.out.println("===============MD5===============");  
                 
           }  
             
   }  
     
   /** 
    * 对一个文件获取md5值 
    * @return md5串 
    */  
   public static String getMD5(File file) {  
       FileInputStream fileInputStream = null;  
       try {  
       fileInputStream = new FileInputStream(file);  
           byte[] buffer = new byte[8192];  
           int length;  
           while ((length = fileInputStream.read(buffer)) != -1) {  
               MD5.update(buffer, 0, length);  
           }  
 
           return new String(Hex.encodeHex(MD5.digest()));  
       } catch (FileNotFoundException e) {  
       e.printStackTrace();  
           return null;  
       } catch (IOException e) {  
       e.printStackTrace();  
           return null;  
       } finally {  
           try {  
               if (fileInputStream != null)  
               fileInputStream.close();  
           } catch (IOException e) {  
               e.printStackTrace();  
           }  
       }  
   }  
}  