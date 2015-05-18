package com.baimao.download;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.xkx.yjxm.activity.RouteActivity;
import com.xkx.yjxm.base.Constants;
import com.xkx.yjxm.utils.HttpUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

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
