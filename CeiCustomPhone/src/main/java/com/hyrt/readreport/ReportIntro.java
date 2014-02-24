package com.hyrt.readreport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vudroid.pdfdroid.PdfViewerActivity;
import com.hyrt.cei.application.CeiApplication;
import com.hyrt.cei.db.DataHelper;
import com.hyrt.cei.util.AsyncImageLoader;
import com.hyrt.cei.util.EncryptDecryption;
import com.hyrt.cei.util.MyTools;
import com.hyrt.cei.util.AsyncImageLoader.ImageCallback;
import com.hyrt.cei.vo.ImageResourse;
import com.hyrt.cei.vo.Report;
import com.hyrt.cei.vo.funId;
import com.hyrt.ceiphone.ContainerActivity;
import com.hyrt.ceiphone.R;
import com.poqop.document.ViewerPreferences;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ReportIntro extends ContainerActivity {

	private LinearLayout textMul;
	private TextView time, title;
	private ImageView reportImg;
	private Button read;
	private Report report;
	private DataHelper dataHelper;
	private AsyncImageLoader asyncImageLoader;
	private CeiApplication application;
	private boolean flage;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == -1) {
				MyTools.exitShow(ReportIntro.this, ReportIntro.this.getWindow()
						.getDecorView(), "文件保存错误！");
			} else {
				Intent intentshelf = new Intent();
				intentshelf.setClass(ReportIntro.this,CeiShelfBookActivity.class);
				intentshelf.putExtra("isDownload",true);
				startActivity(intentshelf);
				finish();
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_report_intro);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		report = (Report) bundle.get("report");
		asyncImageLoader = ((CeiApplication) this.getApplication()).asyncImageLoader;
		dataHelper = ((CeiApplication) this.getApplication()).dataHelper;
		application = (CeiApplication) getApplication();
		initView();
	}

	private void initView() {
		textMul = (LinearLayout) findViewById(R.id.report_intro_mul);
		Pattern pattern = Pattern.compile("[.](\\.[0-9]{1,4})");
		String[] directories = pattern.split(report.getMulu());
		String[] numdirectories = new String[directories.length];
	      Matcher matcher = pattern.matcher(report.getMulu());
	      int j = 0;
	      while(matcher.find()){
	    	  numdirectories[j] = matcher.group();
	    	  j++;
	      	}
		LinearLayout mulNum = (LinearLayout)findViewById(R.id.report_intro_mulnum);
		for (int i = 0; i < numdirectories.length; i++) {
			try{
				TextView tv = new TextView(this);
				tv.setTextColor(getResources().getColor(android.R.color.black));
				tv.setText("...."+numdirectories[i].substring(numdirectories[i].lastIndexOf(".")+1,numdirectories[i].length()));
				tv.setTextSize(15);
				tv.setSingleLine(true);
				mulNum.addView(tv);
			}catch(Exception e){
			}
		}
		for(int i=0;i<directories.length;i++){
			try{
				String leftContent;
				if(directories[i].length() > 17)
					leftContent = directories[i].substring(0,17)+"........................................................";
				else
			    leftContent = directories[i].substring(0,directories[i].length()-3)+"........................................................";
			TextView tv = new TextView(this);
			tv.setTextColor(getResources().getColor(android.R.color.black));
			tv.setText(leftContent);
			tv.setTextSize(15);
			tv.setSingleLine(true);
			textMul.addView(tv);
			}catch(Exception e){
			}
		}
		time = (TextView) findViewById(R.id.report_intro_time);
		time.setText(report.getProtime());
		title = (TextView) findViewById(R.id.report_intro_content);
		title.setText(report.getName());
		reportImg = (ImageView) findViewById(R.id.report_intro_book);
		// 加载图片
		ImageResourse imageResource = new ImageResourse();
		imageResource.setIconUrl(report.getSmallPpath());
		imageResource.setIconId(report.getId());
		imageResource.setIconTime(report.getProtime());
		asyncImageLoader.loadDrawable(imageResource, new ImageCallback() {

			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {

				if (reportImg != null && imageDrawable != null) {
					// img.setLayoutParams(new Gallery.LayoutParams(360, 160));
					reportImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
					reportImg.setBackgroundDrawable(imageDrawable);
				}

			}
		});
		read = (Button) findViewById(R.id.report_intro_read);
		// 判断数据库中是否有记录
		if (dataHelper.getReportListById(report.getName()).size() < 1) {
			read.setText("下载");
			read.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 下载加入书架
					if (!((CeiApplication) ReportIntro.this.getApplication())
							.isNet()) {
						MyTools.exitShow(ReportIntro.this,
								findViewById(R.id.report_intro_main),
								"网络连接错误！请检查网络");
						return;
					}
					if (report.getIsFree() != null
							&& report.getIsFree().equals("1")) {

						new Thread() {

							@Override
							public void run() {
								String fileTime = System.currentTimeMillis()
										+ "";
								String fileName = report.getDownpath().replace(
										"/bg.zip", "");
								fileName = fileName.substring(fileName
										.lastIndexOf("/") + 1);
								// 保存数据库
								report.setReadtime(fileTime);
								report.setFileName(fileName);
								report.setDatapath(Report.SD_PATH + fileName);
								long retuCoad = dataHelper.saveReport(report);
								report.setIsLoad("start");
								dataHelper.UpdateReportZT(report);
								Message msg = new Message();
								msg.arg1 = (int) retuCoad;
								handler.sendMessage(msg);
							}

						}.start();
					} else {// 收费报告，需要判断是否购买？
						List<funId> buyReportData = ((CeiApplication) getApplication()).buyReportData;
						Set<funId> set = new HashSet<funId>();
						set.addAll(buyReportData);
						List<funId> newlist = new ArrayList<funId>();
						newlist.addAll(set);
						if (newlist != null && newlist.size() > 0) {
							for (funId funId : newlist) {
								if (funId.getFunid().equals(report.getId())) {
									flage = true;// 表示此报告已经购买！
									new Thread() {
										@Override
										public void run() {
											String fileTime = System
													.currentTimeMillis() + "";
											String fileName = report
													.getDownpath().replace(
															"/bg.zip", "");
											fileName = fileName.substring(fileName
													.lastIndexOf("/") + 1);
											// 保存数据库
											report.setReadtime(fileTime);
											report.setFileName(fileName);
											report.setDatapath(Report.SD_PATH
													+ fileName);
											long retuCoad = dataHelper
													.saveReport(report);
											report.setIsLoad("start");
											dataHelper.UpdateReportZT(report);
											Message msg = new Message();
											msg.arg1 = (int) retuCoad;
											handler.sendMessage(msg);
										}

									}.start();
								}
							}
							if (!flage) {
								MyTools.exitShow(ReportIntro.this,
										findViewById(R.id.report_intro_main),
										"请你购买后下载！");
							}
						} else {
							MyTools.exitShow(ReportIntro.this,
									findViewById(R.id.report_intro_main),
									"请你购买后下载！");
						}

					}

				}
			});

		} else {
			read.setText("阅读");
			read.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String pdfUri = null;
					Report nowReport = dataHelper.getReportListById(
							report.getName()).get(0);
					String pdfPath = nowReport.getDatapath();
					File dir = new File(pdfPath);
					File file[] = dir.listFiles();
					if (file == null) {
						MyTools.exitShow(ReportIntro.this,
								findViewById(R.id.report_intro_main),
								"文件还没有下载完成，请到书架下载！");
						return;
					}
					for (int i = 0; i < file.length; i++) {
						if (!file[i].isDirectory()) {
							file[i].getName().lastIndexOf(".pdf");
							pdfUri = pdfPath + "/" + file[i].getName();// data.get(position).getDatapath()现在不正确
						}

					}
					if (report.getKey().equals("")) {
						MyTools.exitShow(ReportIntro.this,
								findViewById(R.id.report_intro_main), "后台加密错误！");
					} else {
						// 此处解密阅读
						try {
							EncryptDecryption.DecryptionReport(
									pdfUri,
									report.getKey()
											.substring(
													0,
													report.getKey().toString()
															.length() - 1));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Uri uri = Uri.parse("file://" + pdfUri);// data.get(position).getDatapath()现在不正确
																// +pdfUri
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						intent.putExtra("name", nowReport.getName());
						intent.putExtra("pdfPath", pdfUri);
						intent.putExtra("report", report);
						String uriString = uri.toString();
						intent.setClass(ReportIntro.this,
								PdfViewerActivity.class);
						startActivity(intent);
						ViewerPreferences preferences = new ViewerPreferences(
								ReportIntro.this);
						preferences.putYourReads(uriString);
					}
				}
			});

		}

	}

}
