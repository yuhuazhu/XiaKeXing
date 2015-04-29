package com.example.test;

import com.example.test.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//导购
public class ShoppingGuideActivity extends Activity {

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_guide);
		webView = (WebView) findViewById(R.id.webView1);
		// 设置WebView属性，能够执行Javascript脚本 
		webView.getSettings().setJavaScriptEnabled(true);
		// 加载需要显示的网页
		webView.loadUrl("http://www.baidu.com");
		// 设置Web视图
		webView.setWebViewClient(new HelloWebViewClient());
		//防止打不开网页
		WebSettings webSettings = webView.getSettings();
		webSettings.setDomStorageEnabled(true);
	}

	// Web视图 （点了链接还继续留在这，不是跳到系统或用户的浏览器）
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	// 设置回退
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			// goBack()表示返回WebView的上一页面
			webView.goBack();
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shopping_guide, menu);
		return true;
	}

}
