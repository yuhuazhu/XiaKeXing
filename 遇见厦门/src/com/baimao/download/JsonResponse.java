package com.baimao.download;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ql.json.JsonParser;
import com.ql.json.Jsonable;


/**
 * 接口返回结果
 * @author dream
 */
public class JsonResponse {
	private String code;
	private String message;
	private String result;
	
	
//	public JsonResponse(String json) {
//		if (json == null || (!json.startsWith("{")) || (!json.endsWith("}")))
//			return;
//		
//		try {
//			JSONObject  object = new JSONObject(json);
//			this.code = object.getString("code");
//			this.message = object.getString("message");
//			this.result = object.getString("result");
//		} catch (JSONException e) {
//			System.out.println(json);
//			
//			throw new RuntimeException(e);
//		}
//	}

	public JSONObject getjson(String json)
	{
		JSONObject  object = null;
		if (json == null || (!json.startsWith("{")) || (!json.endsWith("}")))
			return null;
		
		try {
			object = new JSONObject(json);
		} catch (JSONException e) {
			System.out.println(json);
			
			throw new RuntimeException(e);
		}
		return object;
		
	}
	public String getcode() {
		return code;
	}

	public String getmessage() {
		return message;
	}

	public String getresult() {
		return result;
	}
	
	public <T extends Jsonable> T parseData(Class<T> classOfT) {
		if (!isOk())
			return null;
		
		T entity = null;
		if (isArray()) {
			List<T> entityList = parseDataList(classOfT);
			if (entityList.size() >= 1) {
				entity = entityList.get(0);
			}
		} else {
			entity = JsonParser.parse(classOfT, result);
		}
		
		return entity;
	}
	
	public <T extends Jsonable> List<T> parseDataList(Class<T> classOfT) {
		if (!isOk())
			return null;
		
		ArrayList<T> entityList = new ArrayList<T>();
		
		if (!isArray()) {
			return entityList;
		}
		
		try {
			JSONArray jsonArray = new JSONArray(result);
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
	
	private boolean isArray() {
		return result != null && result.startsWith("[") && result.endsWith("]");
	}
	
	public boolean isOk() {
		return "0".equalsIgnoreCase(code);
	}
}
