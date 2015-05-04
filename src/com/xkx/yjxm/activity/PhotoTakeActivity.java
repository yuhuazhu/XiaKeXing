package com.xkx.yjxm.activity;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.xkx.yjxm.R;

public class PhotoTakeActivity extends Activity implements OnClickListener {
	private RelativeLayout upbtn;
	private RelativeLayout cancel;
	private RelativeLayout progresslay;
	private Bitmap bitmap;
	private StringBuffer b;
	private String QRCODE = "";
	private ImageButton btnback;
	private String newName = "image.jpg";
	private ProgressBar progress_horizontal;
	private String fileName;
	private Uri selectedImage;
	private String actionUrl = "http://www.xmlyt.cn/ajax/Statistics.ashx?sn=addUserPic";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_phototake);
		initData();
		initUI();

	}
    private void initData()
    {
    	selectedImage = (Uri) getIntent().getExtras().get("Uri");
    	fileName = (String) getIntent().getExtras().get("fileName");
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				((ImageView) findViewById(R.id.imageView1)).setImageURI(selectedImage);// 将图片显示在ImageView里
			}
		}).start();
    	
    	
    }
	private void initUI() {
		upbtn = (RelativeLayout) findViewById(R.id.upbtn);
		upbtn.setOnClickListener(this);
		cancel = (RelativeLayout) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		
		progresslay = (RelativeLayout) findViewById(R.id.progresslay);
		progress_horizontal = (ProgressBar) findViewById(R.id.progress_horizontal);

		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		
	}

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.upbtn:

			if (!fileName.equals("")) {
				progresslay.setVisibility(View.VISIBLE);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("arg2", fileName);
				LocationTask task = new LocationTask();

				task.execute(params);
			}
			break;
		case R.id.btnback:
		case R.id.cancel:
			finish();
			break;
		default:
			break;
		}
	}

	/* 上传文件至Server的方法 */
	private String uploadFile(String str) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); /* 设置传送的method=POST */
			con.setRequestMethod("POST"); /* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary); /* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end); /* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(str); /* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1; /* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) { /* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end); /*
																	 * close
																	 * streams
																	 */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			} /* 将Response显示于Dialog */
			// Toast.makeText(this, "上传成功", 3000).show();

			ds.close();
		} catch (Exception e) {
			 showDialog("上传失败" + e);
		}
		return b.toString();
	}

	/* 显示Dialog的method */private void showDialog(String mess) {
		new AlertDialog.Builder(PhotoTakeActivity.this).setTitle("Message")
				.setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent();
						intent.setClass(PhotoTakeActivity.this,
								PhotoScanningActivity.class);
						intent.putExtra("QRCODE", QRCODE);
						QRCODE = "";

						startActivity(intent);

					}
				}).show();
	}

	private class LocationTask extends
			AsyncTask<HashMap<String, String>, Integer, String> {

		// doInBackground方法内部执行后台任务,不可在此方法内修改UI
		@Override
		protected String doInBackground(HashMap<String, String>... params) {
			// TODO Auto-generated method stub

			String str = uploadFile(params[0].get("arg2").toString());
			
			return str;
		}

		// onProgressUpdate方法用于更新进度信息
		@Override
		protected void onProgressUpdate(Integer... progresses) {
			// Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
			progress_horizontal.setProgress(progresses[0]);
			// textView.setText("loading..." + progresses[0] + "%");

		}

		
	
		
		
		// onCancelled方法用于在取消执行中的任务时更改UI
		@Override
		protected void onCancelled() {

			// Log.i(TAG, "onCancelled() called");
			// textView.setText("cancelled");
			// progressBar.setProgress(0);
			//
			// execute.setEnabled(true);
			// cancel.setEnabled(false);
		}

		// / </summary>
		// / <param name="json">JSON</param>
		// / <returns>Dictionary`[string, object]</returns>

		// onPostExecute方法用于在执行完后台任务后更新UI,显示结果
		@Override
		protected void onPostExecute(String Signinfo) {

			//

			try {
				JSONObject jsonObject = new JSONObject(Signinfo);
				if (jsonObject != null) {

					if (jsonObject.getString("code").equals("0000")) {
						QRCODE = jsonObject.getJSONObject("result").optString(
								"QRCODE");
						if (QRCODE.equals("")) {
							return;
						}
						else
						{
							showDialog("上传成功");
							progress_horizontal.setProgress(100);
							progresslay.setVisibility(View.GONE);
						}
					}

				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// showDialog("上传成功" + b.toString().trim()); /* 关闭DataOutputStream
			// */

		}
	}
}
