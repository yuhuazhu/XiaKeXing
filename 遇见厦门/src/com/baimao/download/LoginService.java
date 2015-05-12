package com.baimao.download;

import java.util.HashMap;

import org.json.JSONObject;

import android.database.sqlite.SQLiteDatabase;

public class LoginService {
	private static SQLiteDatabase mDB;

	/**
	 * 返回登录信息
	 * 
	 * @return 推荐楼盘列表
	 */
	public JSONObject inquireUserList(String url, HashMap<String, String> parmes) {

		HttpService http = new HttpService(url);

		http.addParameter(parmes);
		JSONObject response = http.fetchJson();

		// if(response.isOk())
		// userinfo = response.parseData(UserInfo.class);
		// else
		// userinfo = null;
		return response;
	}
	/**
	 *  返回签到信息
	 * 
	 * @return 
	 */
	public JSONObject inquireSignList(String url, HashMap<String, String> parmes) {

		HttpService http = new HttpService(url);

		http.addParameter(parmes);
		JSONObject response = http.fetchJson();

		// if(response.isOk())
		// userinfo = response.parseData(UserInfo.class);
		// else
		// userinfo = null;
		return response;
	}
	
	
}
