package com.xkx.yjxm.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xkx.yjxm.R;

public class PhotoTakeActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout upbtn;
	private RelativeLayout cancel;
	private RelativeLayout progresslay;
	private Bitmap bitmap;
	private String QRCODE = "";
	private ImageButton btnback;
	private String newName = "image.jpg";
	private ProgressBar uploadProgressBar;
	private String fileName;
	private Uri selectedImage;
	private String actionUrl = "http://www.xmlyt.cn/ajax/Statistics.ashx?sn=addUserPic";

	private DisplayImageOptions options; // 配置图片加载及显示选项

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_phototake);
		initImageLoader(this);
		initData();
		initUI();
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		// 配置图片加载及显示选项（还有一些其他的配置，查阅doc文档吧）
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.ic_launcher) // image连接地址为空时
				.showImageOnFail(R.drawable.ic_launcher) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(false) // 加载图片时会在磁盘中加载缓存
				.build();
	}

	public void home(View v) {
		Intent intent = null;

		intent = new Intent(this, MainActivity.class);

		startActivity(intent);
	}

	private void initData() {
		// selectedImage = (Uri) getIntent().getExtras().get("Uri");
		ImageView iv = (ImageView) findViewById(R.id.imageView1);
		String filename = getIntent().getStringExtra("fileName");
		String path = "file:///mnt/sdcard/myImage/" + filename;
		fileName = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/myImage/" + filename;
		ImageLoader.getInstance().displayImage(path, iv, options);
	}

	private void initUI() {
		upbtn = (RelativeLayout) findViewById(R.id.upbtn);
		upbtn.setOnClickListener(this);
		cancel = (RelativeLayout) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);

		progresslay = (RelativeLayout) findViewById(R.id.progresslay);
		uploadProgressBar = (ProgressBar) findViewById(R.id.progress_horizontal);

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

	private void upload() {
	}

	private class LocationTask extends
			AsyncTask<HashMap<String, String>, Integer, String> {

		// doInBackground方法内部执行后台任务,不可在此方法内修改UI
		@Override
		protected String doInBackground(HashMap<String, String>... params) {
			String filePath = params[0].get("arg2").toString();
			String twoHyphens = "--";
			String boundary = "*****";
			String end = "\r\n";
			try {
				URL url = new URL(actionUrl);
				File f = new File(filePath);
				long totalLength = f.length();
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				/* 允许Input、Output，不使用Cache */
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setChunkedStreamingMode(0);
				con.setUseCaches(false); /* 设置传送的method=POST */
				con.setRequestMethod("POST"); /* setRequestProperty */
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Charset", "UTF-8");
				con.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary); /* 设置DataOutputStream */
				DataOutputStream dos = new DataOutputStream(
						con.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file\";filename=\"" + newName + "\"" + end);
				dos.writeBytes(end); /* 取得文件的FileInputStream */
				FileInputStream fis = new FileInputStream(filePath); /* 设置每次写入1024bytes */
				long transmit = 0;
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int length = -1; /* 从文件读取数据至缓冲区 */
				int progress = 0;
				while ((length = fis.read(buffer)) != -1) { /* 将资料写入DataOutputStream中 */
					dos.write(buffer, 0, length);
					// dos.flush();
					transmit += length;
					int temp = (int) (transmit * 100 / totalLength);
					if (temp != progress) {
						Log.e("progress", "" + temp);
						publishProgress(temp);
					}
					progress = temp;
				}
				dos.writeBytes(end);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
				fis.close();
				dos.flush();
				/* 取得Response内容 */
				InputStream is = con.getInputStream();
				int ch;
				StringBuffer sbResponse = new StringBuffer();
				while ((ch = is.read()) != -1) {
					sbResponse.append((char) ch);
				}
				is.close();
				dos.close();
				return sbResponse.toString();
			} catch (Exception e) {
				showDialog("上传失败" + e);
			}
			return null;
		}

		// onProgressUpdate方法用于更新进度信息
		@Override
		protected void onProgressUpdate(Integer... progress) {
			if (uploadProgressBar != null) {
				uploadProgressBar.setProgress(progress[0]);
			}
		}

		// onCancelled方法用于在取消执行中的任务时更改UI
		@Override
		protected void onCancelled() {
		}

		@Override
		protected void onPostExecute(String Signinfo) {
			try {
				JSONObject jsonObject = new JSONObject(Signinfo);
				if (jsonObject != null) {
					if (jsonObject.getString("code").equals("0000")) {
						QRCODE = jsonObject.getJSONObject("result").optString(
								"QRCODE");
						if (QRCODE.equals("")) {
							return;
						} else {
							showDialog("上传成功");
							uploadProgressBar.setProgress(100);
							progresslay.setVisibility(View.GONE);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
