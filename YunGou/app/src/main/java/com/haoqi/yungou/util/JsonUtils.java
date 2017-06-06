package com.haoqi.yungou.util;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * json管理
 * 
 * @author Administrator
 * 
 */
public class JsonUtils {
	private static Gson gson = new Gson();

	public static <T> T object(String json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}
	public static <T> String toJson(Class<T> param) {
		return gson.toJson(param);
	}
	public static  String toJson(ArrayList<Map<String,Object>> list){
		return gson.toJson(list);
	}
}
