package com.xkx.yjxm.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.xkx.yjxm.R;

public class PhotoWashActivity extends Activity implements OnClickListener {
	private ImageButton btnback;
	private LinearLayout laybtn01;
	private LinearLayout laybtn02;
	private String fileName = "";
	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photowash);

		initData();
		initUI();
	}
	public void home(View v) {
		Intent intent = null;

		intent = new Intent(this, MainActivity.class);

		startActivity(intent);
	}
	private void initData() {

	}

	private void initUI() {
		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		laybtn01 = (LinearLayout) findViewById(R.id.laybtn01);
		laybtn01.setOnClickListener(this);
		laybtn02 = (LinearLayout) findViewById(R.id.laybtn02);
		laybtn02.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btnback:

			finish();
			break;
		case R.id.laybtn01:

			intent.setClass(this, PhotoUploadActivity.class);

			startActivity(intent);
			break;
		case R.id.laybtn02:
          
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File photo = new File(Environment.getExternalStorageDirectory(),
					"Pic.jpg");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
			imageUri = Uri.fromFile(photo);
			startActivityForResult(intent, 1);
			// startActivity(intent);
			break;
		default:
			break;
		}
	}

	@SuppressLint("SdCardPath")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		

		if (resultCode == Activity.RESULT_OK) {
			Uri selectedImage = imageUri;
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.i("TestFile",
						"SD card is not avaiable/writeable right now.");
				return;
			}
			new DateFormat();
			String name = DateFormat.format("yyyyMMdd_hhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".jpg";
			// Toast.makeText(this, name, Toast.LENGTH_LONG).show();
			//Bundle bundle = data.getExtras();
			ContentResolver cr = getContentResolver();
			Bitmap bitmap = null;
			try {
				bitmap = android.provider.MediaStore.Images.Media
						.getBitmap(cr, selectedImage);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 获取相机返回的数据，并转换为Bitmap图片格式

			FileOutputStream b = null;
			// ???????????????????????????????为什么不能直接保存在系统相册位置呢？？？？？？？？？？？？
			File file = new File("/sdcard/myImage/");
			file.mkdirs();// 创建文件夹
			fileName = "/sdcard/myImage/" + name;

			try {
				b = new FileOutputStream(selectedImage.getPath());
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent intent = new Intent();

			intent.setClass(this, PhotoTakeActivity.class);
			intent.putExtra("Uri", selectedImage);
			intent.putExtra("fileName", selectedImage.getPath());
			startActivity(intent);
			// ((ImageView)
			// findViewById(R.id.imageView1)).setImageBitmap(bitmap);//
			// 将图片显示在ImageView里
		}
	}

}
