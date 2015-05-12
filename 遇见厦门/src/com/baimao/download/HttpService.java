package com.baimao.download;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * http服务�?
 * @author dream
 */
public class HttpService {
	private final static HttpMethodEnum DEFAULT_METHOD = HttpMethodEnum.POST;
	private final static String DEFAULT_CHARSET = "utf-8";

	private String url = null;
	private HttpMethodEnum method = null;
	private String charset = null;

	private HashMap<String, String> parameterMap = new HashMap<String, String>();
	private HashMap<String, String[]> batchParameterMap = new HashMap<String, String[]>();
	
	public HttpService(String url) {
		this(url, DEFAULT_METHOD, DEFAULT_CHARSET);
	}
	
	public HttpService(String url, HttpMethodEnum method) {
		this(url, method, DEFAULT_CHARSET);
	}

	public HttpService(String url, HttpMethodEnum method, String charset) {
		this.url = url;
		this.method = method == null ? DEFAULT_METHOD : method;
		this.charset = charset == null ? DEFAULT_CHARSET : charset;
	}


	public void addParameter(String key, String value) {
		parameterMap.put(key, value);
	}

	public void addParameter(String key, String[] value) {
		batchParameterMap.put(key, value);
	}
	
	void addParameter(Map<String, String> parameter) {
		for (Map.Entry<String, String> each : parameter.entrySet()) {
			parameterMap.put(each.getKey(), each.getValue());
		}
	}
	
	public JSONObject fetchJson() {
		String json = fetchContent();
		JsonResponse response = new JsonResponse();
		
		
		return response.getjson(json);
	}

	private String fetchContent() {
		String content = method == HttpMethodEnum.POST ?  post():get()  ;
		
		return content;
	}
	
	private String get() {
		String content = "";

		try {
			HttpClient httpClient = new DefaultHttpClient();
			String uri = buildGetUri();
			HttpGet httpGet = new HttpGet(uri);
			HttpResponse response = httpClient.execute(httpGet);
			content = EntityUtils.toString(response.getEntity(), charset);
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}

		return content;
	}

	private String buildGetUri() {
		StringBuffer result = new StringBuffer();

		result.append(url);
		result.append(url.indexOf("?") < 0 ? "?" : "&");
		
		for (Entry<String, String> each : parameterMap.entrySet()) {
			try {
				String key = each.getKey();
				String value = each.getValue();
				if (value == null) {
					value = "";
				}
				
				result.append(URLEncoder.encode(key, charset))
						.append("=")
						.append(URLEncoder.encode(value, charset))
						.append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		for (Entry<String, String[]> each : batchParameterMap.entrySet()) {
			for (String eachValue : each.getValue()) {
				try {
					result.append(URLEncoder.encode(each.getKey(), charset))
							.append("=")
							.append(URLEncoder.encode(eachValue, charset))
							.append("&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		return result.toString();
	}

	private String post() {
		String content = null;
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpEntity formEntity = buildPostEntity();
			httppost.setEntity(formEntity);
			HttpResponse response = httpClient.execute(httppost);
			content = EntityUtils.toString(response.getEntity(), charset);
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}
		
		return content;
	}
	
	private HttpEntity buildPostEntity() {
		ArrayList<NameValuePair>  params = new ArrayList<NameValuePair>();
		
		for (Entry<String, String> each : parameterMap.entrySet()) {
			params.add(new BasicNameValuePair(each.getKey(), each.getValue()));
		}

		for (Entry<String, String[]> each : batchParameterMap.entrySet()) {
			for (String eachValue : each.getValue()) {
				params.add(new BasicNameValuePair(each.getKey(), eachValue));
			}
		}
		
		HttpEntity formEntity = null;
		try {
			formEntity = new UrlEncodedFormEntity(params, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return formEntity;
	}
}
