package com.example.test;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baimao.adpater.BaseListAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.xkx.utils.BitmapUtils;

public class PhotoUploadActivity extends Activity implements OnClickListener {
	private GridView gridphoto;
	DisplayImageOptions options; // 配置图片加载及显示选项
	private MyAdapter myAdapter;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private List<Map<String, Object>> list;

	private String newName = "image.jpg";
	private String uploadFile = "/sdcard/image.JPG";
	private String actionUrl = "http://www.xmlyt.cn/ajax/Statistics.ashx?sn=addUserPic";
	private TextView mText1;
	private TextView mText2;
	private Button mButton;
	private StringBuffer b;
	private RelativeLayout upbtn;
	private String position = "";
	private String QRCODE = "";
	private Boolean selflag = false;
	private Button btnback;
	private RelativeLayout progresslay;
	private ProgressBar progress_horizontal;
	/**
	 * 存储图片地址
	 */
	ArrayList<String> listImgPath;
	List<Object> imglist = new ArrayList<Object>();
	String[] imageUriArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photosel);
		initData();
		initUI();
	}

	private void initData() {

		// 扫描内存中图片并存入list

		listImgPath = getImgPathList();
		if (listImgPath.size() < 1) {

		} else {
			// list转成数组
			imageUriArray = (String[]) listImgPath
					.toArray(new String[listImgPath.size()]);

			// 配置图片加载及显示选项（还有一些其他的配置，查阅doc文档吧）
			options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.ic_launcher) // 在ImageView加载过程中显示图片
					.showImageForEmptyUri(R.drawable.ic_launcher) // image连接地址为空时
					.showImageOnFail(R.drawable.ic_launcher) // image加载失败
					.cacheInMemory(true) // 加载图片时会在内存中加载缓存
					.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
					// 设置用户加载图片task(这里是圆角图片显示)
					.build();

		}
		// getData();

	}

	/** 图片加载监听事件 **/
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
					displayedImages.add(imageUri); // 将图片uri添加到集合中
				}
			}
		}
	}

	/**
	 * 
	 * @Title:
	 * @Description:listview适配器
	 * @Copyright: Copyright (c) 2014
	 * @Company:
	 * @author: Administrator
	 * @version: 1.0.0.0
	 * @Date: 2014-2-14
	 */
	class AdapterList extends BaseListAdapter {
		// private ImageLoadingListener animateFirstListener = new
		// AnimateFirstDisplayListener();

		/**
		 * 适配器
		 */
		private class ViewHolder {

			private ImageView imgShow;

		}

		private int selectItem = -1;

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				// 初始化绑定控件
				convertView = getLayoutInflater().inflate(
						R.layout.activity_griditem, null);
				holder.imgShow = (ImageView) findViewById(R.id.img01);
				// add to convertView
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置img，text具体显示的内容

			// holder.imgShow.setBackground(getResources().getDrawable(R.id.btnback));
			return convertView;
		}
	}

	// @Override
	// public void onBackPressed() {
	// AnimateFirstDisplayListener.displayedImages.clear();
	// super.onBackPressed();
	//
	private List<Map<String, Object>> getData() {
		// map.put(参数名字,参数值)
		list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();

		String externalStoragePath = Environment.getExternalStorageDirectory()
				.getPath();
		BitmapUtils.getLoacalBitmapByAssets(this, externalStoragePath);

		map.put("img",
				BitmapUtils.getLoacalBitmapByAssets(this, externalStoragePath));
		list.add(map);
		return list;
	}

	private void initUI() {
		upbtn = (RelativeLayout) findViewById(R.id.upbtn);
		progresslay = (RelativeLayout) findViewById(R.id.progresslay);
		progress_horizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
		upbtn.setOnClickListener(this);
		btnback = (Button) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		gridphoto = (GridView) findViewById(R.id.gridphoto);
		myAdapter = new MyAdapter();
		gridphoto.setAdapter(myAdapter);
		gridphoto.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				position = String.valueOf(arg2);
				myAdapter.setSelectItem(arg2);
				if (selflag) {
					selflag = false;
					position = "";

				} else {
					selflag = true;
				}
				myAdapter.notifyDataSetChanged();

				// TODO Auto-generated method stub

			}
		});
	}

	private class LocationTask extends
			AsyncTask<HashMap<String, String>, Integer, JSONObject> {

		// doInBackground方法内部执行后台任务,不可在此方法内修改UI
		@Override
		protected JSONObject doInBackground(HashMap<String, String>... params) {
			// TODO Auto-generated method stub

			uploadFile(imageUriArray[Integer.valueOf(params[0].get("arg2"))]);
			return null;
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
		protected void onPostExecute(JSONObject Signinfo) {

			//

			try {
				JSONObject jsonObject = new JSONObject(b.toString());
				if (jsonObject != null) {

					if (jsonObject.getString("code").equals("0000")) {
						QRCODE = jsonObject.getJSONObject("result").optString(
								"QRCODE");
						if (QRCODE.equals("")) {
							return;
						}
					}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showDialog("上传成功");
			progress_horizontal.setProgress(100);
			progresslay.setVisibility(View.GONE);
			// showDialog("上传成功" + b.toString().trim()); /* 关闭DataOutputStream
			// */

		}
	}

	/**
	 * 
	 * 获取图片地址列表
	 * 
	 * @return list
	 */
	private ArrayList<String> getImgPathList() {
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { "_id", "_data" }, null, null, null);
		while (cursor.moveToNext()) {

			imglist.add(R.drawable.img_ptsel);

			list.add(cursor.getString(1));// 将图片路径添加到list中
		}
		cursor.close();

		return list;
	}

	private class MyAdapter extends BaseListAdapter {

		/**
		 * 适配器
		 */
		private class ViewHolder {

			private ImageView img01;
			private ImageView img02;
		}

		private int selectItem = -1;

		@SuppressWarnings("unused")
		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageUriArray.length;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder;
			if (convertView == null) {

				convertView = getLayoutInflater().inflate(
						R.layout.activity_griditem, null);

				holder = new ViewHolder();

				holder.img01 = (ImageView) convertView.findViewById(R.id.img01);
				holder.img02 = (ImageView) convertView.findViewById(R.id.img02);

				// 设置交错颜色
				// int[] arrayOfInt = mColors;
				// int colorLength = mColors.length;
				// int selected = arrayOfInt[position % colorLength];
				//
				// convertView.setBackgroundResource(selected);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Bitmap bit
			// =BitmapUtils.getLoacalBitmapByAssets(PhotoUploadActivity.this,"file:/"+imageUriArray[position]);
			// holder.img01.setImageBitmap(bit);
			// //holder.img01.setBackgroundDrawable(getResources().getDrawable(R.id.btnback));

			imageLoader.displayImage("file:/" + imageUriArray[position],
					holder.img01, options);
			if (position == selectItem) { // 选中状态 高亮
				holder.img02.setBackgroundResource(R.drawable.img_ptsel);
				holder.img02.setVisibility(selflag ? View.VISIBLE : View.GONE);// 设置删除按钮是否显示
			} else { // 正常状态
				holder.img02.setVisibility(View.GONE);

			}
			return convertView;
		}

	}

	/* 上传文件至Server的方法 */
	private void uploadFile(String str) {
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
			ds.flush(); /* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			} /* 将Response显示于Dialog */
			// Toast.makeText(this, "上传成功", 3000).show();

			ds.close();
		} catch (Exception e) {
			// showDialog("上传失败" + e);
		}
	}

	/* 显示Dialog的method */private void showDialog(String mess) {
		new AlertDialog.Builder(PhotoUploadActivity.this).setTitle("Message")
				.setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (!position.equals("")) {
							Intent intent = new Intent();
							intent.setClass(PhotoUploadActivity.this,
									PhotoScanning.class);
							intent.putExtra("QRCODE", QRCODE);
							QRCODE = "";
							position = "";
							selflag = false;
							startActivity(intent);
							
						}
					}
				}).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.upbtn:

			if (position.equals("")) {
				showDialog("请选择一个照片!");
				return;
			}
			progresslay.setVisibility(View.VISIBLE);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("arg2", position);
			LocationTask task = new LocationTask();

			task.execute(params);

			break;
		case R.id.btnback:
			finish();
			break;
		default:
			break;
		}
	}
}
