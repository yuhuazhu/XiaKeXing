package com.xkx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;

public class BitmapUtils {
	/** 
	    * 加载本地图片 
	    * @param url 
	    * @return 
	    */  
	    public static Bitmap getLoacalBitmapByAssets(Context c, String url)  
	    {  
	        Bitmap bitmap = null;  
	        InputStream in = null;  
	        try  
	        {  
	            in = c.getResources().getAssets().open(url);  
	            bitmap = BitmapFactory.decodeStream(in);  
	            
	            
//	          //通过openRawResource获取一个inputStream对象  
//	            InputStream inputStream = getResources().openRawResource("file:/"+imageUriArray[position]);  
//	            //通过一个InputStream创建一个BitmapDrawable对象  
//	            BitmapDrawable drawable = new BitmapDrawable(inputStream);  
//	             //通过BitmapDrawable对象获得Bitmap对象  
//	             Bitmap bitmap = drawable.getBitmap();  
	            //利用Bitmap对象创建缩略图  
	              bitmap = ThumbnailUtils.extractThumbnail(bitmap, 51, 108);  
	            //imageView 显示缩略图的ImageView  
	              //imageView.setImageBitmap(bitmap);  
	        }  
	        catch (IOException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        finally  
	        {  
	            closeStream(in, null);  
	        }  
	        return bitmap;  
	    }  
	  
	    /** 
	    * 从服务器取图片 
	    * @param url 
	    * @return 
	    */  
	    public static Bitmap getHttpBitmap(String url)  
	    {  
	        URL myFileUrl = null;  
	        Bitmap bitmap = null;  
	        InputStream in = null;  
	        try  
	        {  
	            myFileUrl = new URL(url);  
	            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();  
	            conn.setConnectTimeout(0);  
	            conn.setDoInput(true);  
	            conn.connect();  
	            in = conn.getInputStream();  
	            bitmap = BitmapFactory.decodeStream(in);  
	        }  
	        catch (IOException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        finally  
	        {  
	            closeStream(in, null);  
	        }  
	        return bitmap;  
	    }  
	  
	    /**  
	     * 关闭输入输出流 
	     * @param in 
	     * @param out 
	     */  
	    public static void closeStream(InputStream in, OutputStream out)  
	    {  
	        try  
	        {  
	            if (null != in)  
	            {  
	                in.close();  
	            }  
	            if (null != out)  
	            {  
	                out.close();  
	            }  
	        }  
	        catch (IOException e)  
	        {  
	            e.printStackTrace();  
	        }  
	    }  
}
