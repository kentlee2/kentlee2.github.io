package com.haoqi.yungou.Activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * 后台下单工具类
 * 
 * */
public class HttpUtil {

	private static final String TAG = HttpUtil.class.getSimpleName();
	
    
	public static String doPost(String pathUrl , String param) {
		String result = "";
		try {
	        result = new String(sendPostRequestByForm(pathUrl, param), "gbk");
	        Log.d(TAG, "请求结果:" + result);
	        return result;
		} catch (Exception e) {
			Log.d(TAG , e.toString());
			return "网络连接错误";
		}
		
	}
	
	
	
	/**
     * 通过HttpURLConnection模拟post表单提交
     * 
     * @param path
     * @param params 例如"name=zhangsan&age=21"
     * @return
     * @throws Exception
     */
    public static byte[] sendPostRequestByForm(String path, String params) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");// 提交模式
        conn.setConnectTimeout(20000);//连接超时 单位毫秒
        conn.setReadTimeout(20000);//读取超时 单位毫秒
        conn.setDoOutput(true);// 是否输入参数
        byte[] bypes = params.toString().getBytes();
        conn.getOutputStream().write(bypes);// 输入参数
        InputStream inStream=conn.getInputStream();
        return readInputStream(inStream);
    }
	
    
    /**
     * 从输入流中读取数据
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len = inStream.read(buffer)) !=-1 ){
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//网页的二进制数据
        outStream.close();
        inStream.close();
        return data;
    }
}
