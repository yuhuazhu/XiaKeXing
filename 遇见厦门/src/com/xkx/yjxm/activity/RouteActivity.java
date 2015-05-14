package com.xkx.yjxm.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baimao.download.FileDownloadThread;
import com.baimao.download.HttpService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.BaseListAdapter;
import com.xkx.yjxm.base.Constants;
import com.xkx.yjxm.utils.HttpUtil;

//导游路线
public class RouteActivity extends Activity implements OnClickListener {
	private ImageButton btnlist;
	private ImageButton btndlist;
	private ImageButton btnback;
	private ListView listView1;
	private List<Map<String, Object>> list;
	DisplayImageOptions options; // 配置图片加载及显示选项
	private MyAdapter myAdapter;
	private LinearLayout downloadlay;
	private ImageView imgmap;
	private int img[] = new int[19];
	private String title[] = { "智慧导览", "引导台", "感互动3D景区推介区", "智慧旅游应用展示区",
			"综合服务区", "按摩免费体验区", "产品信息播放屏幕", "自助行李寄存柜", "医务室", "伴手礼超市",
			"多功能会议厅", "机房", "预警指挥中心", "办公区", "智慧旅游视屏", "单车租赁", "旅客上车处", "呼叫中心",
			"19" };
	private String time[] = new String[19];
	private int times = 10;
	private int distances = 100;
	private String distance[] = new String[19];
	private String stringExtra;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private TextView txtdetail;
	private ImageButton img_close;
	private FrameLayout txtlay;
	private Map<Integer, String> textMap;
	private String TAG = "RouteActivity";
	private Boolean finstate = false;

	public void home(View v) {
		Intent intent = null;

		intent = new Intent(this, MainActivity.class);

		startActivity(intent);
	}

	downloadTask task;
	downloadTask2 task2;
	/** 显示下载进度TextView */
	private TextView mMessageView;
	/** 显示下载进度ProgressBar */
	private ProgressBar mProgressbar;
	private URL connectURL;
	private String path;
	private String response;
	private String fistr;
	private boolean downstate = false;
	String strFileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_route);
		Intent intent = getIntent();
		// stringExtra 得到的是（观音山or胡力山or鼓浪屿）
		stringExtra = intent.getStringExtra("name");
		initData();
		initUI();
		for (int i = 0; i < img.length; i++) {
			img[i] = R.drawable.route01 + i;
		}
		Random random = new Random();
		Random random2 = null;
		for (int i = 0; i < img.length; i++) {
			int nextInt = random.nextInt();
			random2 = new Random(nextInt);
			times = random2.nextInt(10) + 5;

			time[i] = "浏览时长：" + times + "分钟";
		}
		for (int i = 0; i < img.length; i++) {
			distances += 10;
			distance[i] = distances + "m";
		}
	}

	private List<Map<String, Object>> getData() {
		// map.put(参数名字,参数值)
		list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("img", R.drawable.img_feng_qing);
		map.put("title", getResources().getString(R.string.R_title1));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis1));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("img", R.drawable.img_ri_guang_yan);
		map.put("title", getResources().getString(R.string.R_title2));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis2));
		list.add(map);

		map = new HashMap<String, Object>();

		map.put("img", R.drawable.img_bai_niao_yuan);
		map.put("title", getResources().getString(R.string.R_title3));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis3));
		list.add(map);

		map = new HashMap<String, Object>();

		map.put("img", R.drawable.img_shu_zhuang_hua_yuan);
		map.put("title", getResources().getString(R.string.R_title4));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis4));
		list.add(map);

		map = new HashMap<String, Object>();

		map.put("img", R.drawable.img_hao_yue_yuan);
		map.put("title", getResources().getString(R.string.R_title5));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis5));
		list.add(map);
		return list;
	}

	private void initData() {
		getData();
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

	private void initUI() {
		downloadlay = (LinearLayout) findViewById(R.id.downloadlay);
		btndlist = (ImageButton) findViewById(R.id.btndlist);
		btndlist.setOnClickListener(this);
		txtlay = (FrameLayout) findViewById(R.id.txtlay);
		txtdetail = (TextView) findViewById(R.id.txtdetail);
		img_close = (ImageButton) findViewById(R.id.img_close);

		img_close.setOnClickListener(this);

		textMap = new HashMap<Integer, String>();
		textMap.put(0, getResources().getString(R.string.txt_dao_lan_she_bei));
		textMap.put(1, getResources().getString(R.string.txt_yin_dao));

		// mapBgMap.put(2, R.drawable.img_map)

		textMap.put(2, getResources().getString(R.string.txt_tiyan_3d));

		textMap.put(3, getResources().getString(R.string.txt_lv_you_zhan_shi));
		textMap.put(4, getResources().getString(R.string.txt_zhi_hui_lv_you));
		textMap.put(5, getResources().getString(R.string.txt_anmo));
		textMap.put(6, getResources().getString(R.string.txt_xin_xi_bo_fang));

		textMap.put(7, getResources().getString(R.string.txt_xing_li_ji_cun));
		// mapBgMap.put(7, R.drawable.img_map);
		textMap.put(8, getResources().getString(R.string.txt_yiwu_shi));
		textMap.put(9, getResources().getString(R.string.txt_ban_shou_li));
		textMap.put(10, getResources().getString(R.string.txt_duo_gong_neng));
		textMap.put(11, getResources().getString(R.string.txt_yun_shu_ju));
		textMap.put(12, getResources().getString(R.string.txt_yu_jin));
		textMap.put(13, getResources().getString(R.string.txt_ban_gong_qu));
		textMap.put(14, getResources().getString(R.string.txt_xin_xi_bo_fang));

		textMap.put(16, getResources().getString(R.string.txt_hu_jiao));

		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		btnlist = (ImageButton) findViewById(R.id.btnlist);
		btnlist.setOnClickListener(this);

		imgmap = (ImageView) findViewById(R.id.imgmap);
		imgmap.setOnClickListener(this);
		listView1 = (ListView) findViewById(R.id.listView1);
		myAdapter = new MyAdapter();
		listView1.setAdapter(myAdapter);
		listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				txtdetail.setText(textMap.get(position));
				txtlay.setVisibility(View.VISIBLE);
			}
		});
		findViewById(R.id.download_btn).setOnClickListener(this);
		findViewById(R.id.download_btn).setEnabled(true);
		mMessageView = (TextView) findViewById(R.id.download_message);
		mProgressbar = (ProgressBar) findViewById(R.id.download_progress);
		// 向服务器请求资源，判断是否需要下载资源
		downloadrequest();
	}

	/**
	 * 使用Handler更新UI界面信息
	 */
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			mProgressbar.setProgress(msg.getData().getInt("size"));

			float temp = (float) mProgressbar.getProgress()
					/ (float) mProgressbar.getMax();

			int progress = (int) (temp * 100);
			if (progress == 100) {
				Toast.makeText(RouteActivity.this, "下载完成,你现在可以进入地图享受自动讲解乐趣了！", Toast.LENGTH_LONG)
						.show();
				// 获取SD卡路径
				String path = Environment.getExternalStorageDirectory()
						+ "/resource/" + fistr;
				String folderPath = Environment.getExternalStorageDirectory()
						+ "/resource/";
				File file = new File(path);

				try {
					upZipFile(file, folderPath);
				} catch (ZipException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mMessageView.setText("下载进度:" + progress + " %");
			downloadlay.setVisibility(View.GONE);

		}
	};

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public int upZipFile(File zipFile, String folderPath) throws ZipException,
			IOException {
		// public static void upZipFile() throws Exception{
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				Log.d("upZipFile", "ze.getName() = " + ze.getName());
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "str = " + dirstr);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			Log.d("upZipFile", "ze.getName() = " + ze.getName());
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		Log.d("upZipFile", "finishssssssssssssssssssss");
		return 0;
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ret = new File(ret, substr);

			}
			Log.d("upZipFile", "1ret = " + ret);
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			Log.d("upZipFile", "2ret = " + ret);
			return ret;
		}
		return ret;
	}

	/**
	 * 多线程文件下载
	 * 
	 * @author yangxiaolong
	 * @2014-8-7
	 */
	class downloadTask2 extends Thread {
		private String downloadUrl;// 下载链接地址
		private int threadNum;// 开启的线程数
		private String filePath;// 保存文件路径地址
		private int blockSize;// 每一个线程的下载量
		private String filename;

		public downloadTask2(String downloadUrl, int threadNum,
				String fileptah, String filename) {
			this.downloadUrl = downloadUrl;
			this.threadNum = threadNum;
			this.filePath = fileptah;
			this.filename = filename;
		}

		@Override
		public void run() {

			FileDownloadThread[] threads = new FileDownloadThread[threadNum];
			try {
				URL url = new URL(downloadUrl);

				byte[] data = null;
				if ((url != null && !"".equals(url))
						&& (filename != null && !"".equals(filename))) {
					InputStream input = null;
					try {
						connectURL = new URL(downloadUrl);
						HttpURLConnection conn = (HttpURLConnection) connectURL
								.openConnection();
						conn.setRequestMethod("POST");
						conn.setReadTimeout(1000);
						input = conn.getInputStream();
					} catch (MalformedURLException e) {
						// Log.e(ERROTAG, "请确认输入的地址并确认权限是否添加!!!!!!");
						e.printStackTrace();
					} catch (IOException e) {
						// toasterror("连接失败");
						// Log.w(ERROTAG, "请求的路径错误！");
						e.printStackTrace();
					}
					FileOutputStream outStream = null;
					try {
						outStream = RouteActivity.this.openFileOutput(filename,
								Context.MODE_WORLD_READABLE
										| Context.MODE_WORLD_WRITEABLE);
						int temp = 0;
						data = new byte[1024];
						while ((temp = input.read(data)) != -1) {
							outStream.write(data, 0, temp);
						}
					} catch (FileNotFoundException e) {
						// toasterror("请传入正确的上下文");
						e.printStackTrace();
					} catch (IOException e) {
						// toastemessage("读写错误");
						e.printStackTrace();
					} finally {
						try {
							outStream.flush();
							outStream.close();
						} catch (IOException e) {
							// toasterror("文件流未能正常关闭");
							e.printStackTrace();
						}
					}
				}
				// toastemessage("下载成功");

				// 读取下载文件总大小
				int fileSize = data.length;
				if (fileSize <= 0) {
					System.out.println("读取文件失败");
					return;
				}
				// 设置ProgressBar最大的长度为文件Size
				mProgressbar.setMax(fileSize);

				// 计算每条线程下载的数据长度
				blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
						: fileSize / threadNum + 1;

				Log.d(TAG, "fileSize:" + fileSize + "  blockSize:");

				File file = new File(filePath);
				for (int i = 0; i < threads.length; i++) {
					// 启动线程，分别下载每个线程需要下载的部分
					threads[i] = new FileDownloadThread(url, file, blockSize,
							(i + 1));
					threads[i].setName("Thread:" + i);
					threads[i].start();
				}

				boolean isfinished = false;
				int downloadedAllSize = 0;
				while (!isfinished) {
					isfinished = true;
					// 当前所有线程下载总量
					downloadedAllSize = 0;
					for (int i = 0; i < threads.length; i++) {
						downloadedAllSize += threads[i].getDownloadLength();
						if (!threads[i].isCompleted()) {
							isfinished = false;
						}
					}
					// 通知handler去更新视图组件
					Message msg = new Message();
					msg.getData().putInt("size", downloadedAllSize);
					mHandler.sendMessage(msg);
					// Log.d(TAG, "current downloadSize:" +
					// downloadedAllSize);
					Thread.sleep(1000);// 休息1秒后再读取下载进度
				}
				Log.d(TAG, " all of downloadSize:" + downloadedAllSize);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 多线程文件下载
	 * 
	 * @author yangxiaolong
	 * @2014-8-7
	 */
	class downloadTask extends Thread {
		private String downloadUrl;// 下载链接地址
		private int threadNum;// 开启的线程数
		private String filePath;// 保存文件路径地址
		private int blockSize;// 每一个线程的下载量

		public downloadTask(String downloadUrl, int threadNum, String fileptah) {
			this.downloadUrl = downloadUrl;
			this.threadNum = threadNum;
			this.filePath = fileptah;
		}

		@Override
		public void run() {

			FileDownloadThread[] threads = new FileDownloadThread[threadNum];
			try {
				URL url = new URL(downloadUrl);
				Log.d(TAG, "download file http path:" + downloadUrl);
				URLConnection conn = url.openConnection();
				// 读取下载文件总大小
				int fileSize = conn.getContentLength();
				if (fileSize <= 0) {
					System.out.println("读取文件失败");
					return;
				}
				// 设置ProgressBar最大的长度为文件Size
				mProgressbar.setMax(fileSize);

				// 计算每条线程下载的数据长度
				blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
						: fileSize / threadNum + 1;

				Log.d(TAG, "fileSize:" + fileSize + "  blockSize:");

				File file = new File(filePath);
				for (int i = 0; i < threads.length; i++) {
					// 启动线程，分别下载每个线程需要下载的部分
					threads[i] = new FileDownloadThread(url, file, blockSize,
							(i + 1));
					threads[i].setName("Thread:" + i);
					threads[i].start();
				}

				boolean isfinished = false;
				int downloadedAllSize = 0;
				while (!isfinished) {
					isfinished = true;
					// 当前所有线程下载总量
					downloadedAllSize = 0;
					for (int i = 0; i < threads.length; i++) {
						downloadedAllSize += threads[i].getDownloadLength();
						if (!threads[i].isCompleted()) {
							isfinished = false;
						}
					}
					// 通知handler去更新视图组件
					Message msg = new Message();
					msg.getData().putInt("size", downloadedAllSize);
					mHandler.sendMessage(msg);
					// Log.d(TAG, "current downloadSize:" +
					// downloadedAllSize);
					Thread.sleep(1000);// 休息1秒后再读取下载进度
				}
				Log.d(TAG, " all of downloadSize:" + downloadedAllSize);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private void downloadrequest() {
		if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
			Toast.makeText(RouteActivity.this, "sd卡不存在", Toast.LENGTH_SHORT)
					.show();
			if (Environment.getRootDirectory() != null) {
				// 获取本地路径
				path = Environment.getRootDirectory() + "/resource/";
				File file = new File(path);
				// 如果本地目录不存在创建
				if (!file.exists()) {
					file.mkdir();
				}
				File[] file2 = file.listFiles();
				if (file2 == null) {
					HttpService http = new HttpService(Constants.RESOURCEREQURL);
					response = http.fetchContent();
					downloadlay.setVisibility(View.VISIBLE);
					// 设置progressBar初始化
					mProgressbar.setProgress(0);
					downstate = true;
				} else {
					String strFileName = "";
					for (int i = 0; i < file2.length; i++) {
						if (file2[i].isDirectory()) {
						} else {
							strFileName = file2[i].getAbsolutePath()
									.toLowerCase();
						}
					}

					// HttpService http = new
					// HttpService(Constants.RESOURCEREQURL+"FDFD="+K+"&FDF="+P);
					// HashMap<String, String> parmes = new HashMap<String,
					// String>();
					// parmes.put("name", strFileName);
					// http.addParameter(parmes);
					response = HttpUtil
							.queryStringForPost(Constants.RESOURCEREQURL
									+ "name=" + strFileName);
					String str[] = response.split(",");
					response = str[0] + str[1];
					fistr = str[1];
				}
			}
		} else {
			// 获取SD卡路径
			path = Environment.getExternalStorageDirectory() + "/resource/";
			File file = new File(path);
			// 如果SD卡目录不存在创建
			if (!file.exists()) {
				file.mkdir();
			}
			File[] file2 = file.listFiles();
			if (file2.length == 0) {

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						response = HttpUtil
								.queryStringForPost(Constants.RESOURCEREQURL);
						String str[] = response.split(",");
						response = str[0] + str[1];
						fistr = str[1];
					}
				}).start();

				downloadlay.setVisibility(View.VISIBLE);
				// 设置progressBar初始化
				mProgressbar.setProgress(0);
				downstate = false;
			} else {
				strFileName = "";
				for (int i = 0; i < file2.length; i++) {
					if (file2[i].isDirectory()) {
					} else {
						strFileName = file2[i].getName();
					}
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						response = HttpUtil
								.queryStringForPost(Constants.RESOURCEREQURL
										+ "?name=" + strFileName);
						if (!response.equals("false")) {
							String str[] = response.split(",");
							response = str[0] + str[1];
							fistr = str[1];
							finstate = false;
							
						}
						else
						{
							finstate = true;
						}
					}
				}).start();

			}
		}
	}

	/**
	 * 下载准备工作，获取SD卡路径、开启线程
	 */
	private void doDownloadlocal(String url, String filestr) {

		// 获取本地路径

		String path = Environment.getRootDirectory() + "/resource/";
		File file = new File(path);
		// 如果本地目录不存在创建
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(path + "观音山resource2.0.rar");
		if (!file2.exists()) {
			downloadlay.setVisibility(View.VISIBLE);
			// 设置progressBar初始化
			mProgressbar.setProgress(0);

			// 简单起见，我先把URL和文件名称写死，其实这些都可以通过HttpHeader获取到
			String downloadUrl = url;
			String fileName = filestr;
			int threadNum = 5;
			String filepath = path + fileName;
			Log.d(TAG, "download file  path:" + filepath);
			task2 = new downloadTask2(downloadUrl, threadNum, filepath,
					fileName);

			task2.start();
		}

	}

	/**
	 * 下载准备工作，获取SD卡路径、开启线程
	 */
	private void doDownloadSDka(String url, String filestr) {

		// 获取SD卡路径
		String path = Environment.getExternalStorageDirectory()
				+ "/amosdownload/";
		File file = new File(path);
		// 如果SD卡目录不存在创建
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(path + "观音山resource2.0.rar");
		if (!file2.exists()) {
			downloadlay.setVisibility(View.VISIBLE);
			// 设置progressBar初始化
			mProgressbar.setProgress(0);

			String downloadUrl = url;
			String fileName = filestr;
			int threadNum = 5;
			String filepath = path + fileName;
			Log.d(TAG, "download file  path:" + filepath);
			task = new downloadTask(downloadUrl, threadNum, filepath);

			task.start();
		}

	}

	private class MyAdapter extends BaseListAdapter {
		/**
		 * 适配器
		 */
		private class ViewHolder {

			private ImageView imageView1;
			private TextView txttitle;
			private TextView txttime;
			private TextView txtdistance;

		}

		private int selectItem = -1;

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return title.length;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;
			if (convertView == null) {

				convertView = getLayoutInflater().inflate(
						R.layout.activity_listitem, null);

				holder = new ViewHolder();

				holder.imageView1 = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.txttitle = (TextView) convertView
						.findViewById(R.id.txttitle);
				holder.txttime = (TextView) convertView
						.findViewById(R.id.txttime);
				holder.txtdistance = (TextView) convertView
						.findViewById(R.id.txtdistance);

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

			holder.imageView1.setBackgroundResource(img[position]);
			// Bitmap image =
			// Bitmap.createBitmap(((BitmapDrawable)holder.imageView1.getDrawable()).getBitmap());
			// imgUtils.getRoundedCornerBitmap(image, 90);
			// imageLoader.displayImage(
			// "drawable://" + (Integer) list.get(position).get("img"),
			// holder.imageView1, options);

			holder.txttitle.setText(title[position]);
			holder.txttime.setText(time[position]);
			holder.txtdistance.setText(distance[position]);

			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btndlist:

			break;
		case R.id.btnback:
			finish();
			break;

		case R.id.imgmap:

			Intent intent = new Intent();
			intent.setClass(this, RouteMapActivity.class);

			startActivity(intent);
			break;
		case R.id.img_close:
			txtlay.setVisibility(View.GONE);
			break;
		case R.id.download_btn:
			if(finstate)
			{
				Toast.makeText(RouteActivity.this, "该资源已经下载过了！", Toast.LENGTH_LONG)
				.show();
				return;
			}
			
			// 设置progressBar初始化
			if (downstate) {
				// 下载到SD卡
				// 简单起见，我先把URL和文件名称写死，其实这些都可以通过HttpHeader获取到
				String downloadUrl = response;
				String fileName = fistr;
				int threadNum = 5;
				String filepath = path + fileName;
				Log.d(TAG, "download file  path:" + filepath);

				task2 = new downloadTask2(downloadUrl, threadNum, filepath,
						fileName);

				task2.start();
				// 设置progressBar初始化
				downstate = true;
			} else {
				findViewById(R.id.download_btn).setEnabled(false);
				// 下载到手机本地
				// 简单起见，我先把URL和文件名称写死，其实这些都可以通过HttpHeader获取到
				String downloadUrl = response;
				String fileName = fistr;
				int threadNum = 5;
				String filepath = path + fileName;
				Log.d(TAG, "download file  path:" + filepath);
				task = new downloadTask(downloadUrl, threadNum, filepath);

				task.start();
				downstate = false;
			}
			break;
		default:
			break;
		}
	}
}
