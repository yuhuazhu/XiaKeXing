package com.ql.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * json解释器，将json转换为Jsonable实体对象
 * @author dream
 */
@SuppressWarnings("unchecked")
public class JsonParser {
	public static <T extends Jsonable> T parse(Class<T> classOfT, String json) {
		T result = null;
		
		if (json == null)
			return result;
		
		try {
			T entity = classOfT.newInstance();
			result = (T)entity.from(json);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	private JSONObject jsonObject;
	
	public JsonParser(String json) {
		if (json == null || (!json.startsWith("{")) || (!json.endsWith("}")))
				return;
		
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	 
	
	public String getString(String key) {
		if (jsonObject == null)
			return "";
		
		String value = null;
		try {
			if (jsonObject.has(key)) {
				value = jsonObject.getString(key);
				if ("null".equalsIgnoreCase(value)) {
					value = "";
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return value;
	}
	public Object getobject(String key) {
		if (jsonObject == null)
			return "";
		
		String value = null;
		try {
			if (jsonObject.has(key)) {
				value = jsonObject.getString(key);
				if ("null".equalsIgnoreCase(value)) {
					value = "";
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return value;
	}
	public Integer getInteger(String key) {
		if (jsonObject == null)
			return 0;
		
		Integer value = 0;
		try {
			value = jsonObject.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	
	public <T extends Jsonable> List<T> getList(String key, Class<T> classOfT) {
		if (jsonObject == null)
			return new ArrayList<T>();
		
		ArrayList<T> entityList = new ArrayList<T>();
		try {
			String data = jsonObject.getString(key);
			if (data == null || (!data.startsWith("[")) || (!data.endsWith("]"))) {
				return entityList;
			}
			
			JSONArray jsonArray = new JSONArray(data);
			String json = null;
			T entity = null;
			for (int i = 0, len = jsonArray.length(); i < len; i++) {
				json = jsonArray.getString(i);
				entity = JsonParser.parse(classOfT, json);
				entityList.add(entity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return entityList;
	}
}
