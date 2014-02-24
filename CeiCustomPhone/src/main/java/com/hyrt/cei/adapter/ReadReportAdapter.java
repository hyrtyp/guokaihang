package com.hyrt.cei.adapter;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.vudroid.pdfdroid.PdfViewerActivity;

import com.hyrt.cei.application.CeiApplication;
import com.hyrt.cei.db.DataHelper;
import com.hyrt.cei.util.AsyncImageLoader;
import com.hyrt.cei.util.BitmapManager;
import com.hyrt.cei.util.EncryptDecryption;
import com.hyrt.cei.util.MyTools;
import com.hyrt.cei.util.AsyncImageLoader.ImageCallback;
import com.hyrt.cei.vo.ImageResourse;
import com.hyrt.cei.vo.Report;
import com.hyrt.cei.vo.funId;
import com.hyrt.ceiphone.R;
import com.hyrt.readreport.CeiShelfBookActivity;
import com.hyrt.readreport.ReadReportMainActivity;
import com.hyrt.readreport.ReportIntro;
import com.poqop.document.ViewerPreferences;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReadReportAdapter extends BaseAdapter {

	private List<Report> data;
	private LayoutInflater inflater;
	private ListView goodList;
	private Activity context;
	private DataHelper dataHelper;
    private BitmapManager bmpManager;

	public ReadReportAdapter(Activity context, List<Report> data,
			ListView goodList) {
		if(data==null) data=new ArrayList<Report>(); 
		this.data = data;
		this.goodList = goodList;
		this.context = context;
		inflater = LayoutInflater.from(context);
		dataHelper = ((CeiApplication) context.getApplication()).dataHelper;
        this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.courseware_default_icon));
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder = new Holder();convertView = inflater.inflate(R.layout.read_report_item, null);
		holder.handImg = (ImageView) convertView
				.findViewById(R.id.iv_goodbg_hand);

		holder.title = (TextView) convertView
				.findViewById(R.id.tv_goodbg_title);
		holder.download = (Button) convertView
				.findViewById(R.id.ib_bg_download);
		convertView.setTag(holder);
		final Report report = data.get(position);
		holder.handImg.setTag(report.getSmallPpath());
		holder.title.setText(report.getName());
		ImageResourse imageResource = new ImageResourse();
		imageResource.setIconUrl(data.get(position).getSmallPpath());
		imageResource.setIconId(data.get(position).getId());
		imageResource.setIconTime(data.get(position).getProtime());
        bmpManager.loadBitmap(data.get(position).getSmallPpath(),holder.handImg,data.get(position).getId());
		holder.handImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 方法二，大窗体
				Intent intent = new Intent(context, ReportIntro.class);
				intent.putExtra("report", report);
				context.startActivity(intent);
			}
		});
		// 判断数据库中是否有记录
		if (dataHelper.getReportListById(report.getName()).size() < 1) {
			holder.download.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!((CeiApplication) context.getApplication()).isNet()) {
						MyTools.exitShow(context, goodList, "网络连接错误！请检查网络");
						return;
					}
					// 下载加入书架
					if (report.getIsFree() != null
							&& report.getIsFree().equals("1")) {

						if (Environment.getExternalStorageState().equals(
								Environment.MEDIA_MOUNTED)) {
							String fileTime = System.currentTimeMillis() + "";
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
							// 跳转书架
							if (retuCoad != -1) {
								Intent intentshelf = new Intent();
								intentshelf.setClass(context,CeiShelfBookActivity.class);
								intentshelf.putExtra("isDownload",true);
								context.startActivity(intentshelf);
//								context.startActivity(new Intent(context,
//										CeiShelfBookActivity.class));
								//context.finish();
							} else {
								Toast.makeText(context, "文件保存错误", 1).show();
							}
						} else {
							Toast.makeText(context, "SD卡没有准备好",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						List<funId> buyReportData = ((CeiApplication) context
								.getApplication()).buyReportData;
                        boolean flage =false;
						if (buyReportData != null && buyReportData.size() > 0) {
							for (funId funId : buyReportData) {
								if (funId.getFunid().equals(report.getId())) {
									flage = true;
									// 已购买
									String fileTime = System
											.currentTimeMillis() + "";
									String fileName = report.getDownpath()
											.replace("/bg.zip", "");
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
									// 跳转书架
									if (retuCoad != -1) {
										context.startActivity(new Intent(
												context,
												CeiShelfBookActivity.class));
										//context.finish();
									} else {
										Toast.makeText(context, "文件保存错误", 1)
												.show();
									}

								}
							}
							if (!flage) {
								MyTools.exitShow(context, goodList, "请你购买后下载！");
							}
						} else {
							MyTools.exitShow(context, goodList, "请你购买后下载！");
						}

					}

				}
			});
		} else if (dataHelper.getReportListById(report.getName()).get(0)
				.getIsLoad() != null
				&& dataHelper.getReportListById(report.getName()).get(0)
						.getIsLoad().equals("yes")) {
			holder.download.setText("阅读");
			holder.download.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String pdfUri = null;
					Report nowReport = dataHelper.getReportListById(
							report.getName()).get(0);
					String pdfPath = nowReport.getDatapath();
					File dir = new File(pdfPath);
					File file[] = dir.listFiles();
					if (file == null) {
						Toast.makeText(context, "文件不存在！", 2).show();
						return;
					}
					for (int i = 0; i < file.length; i++) {
						if (!file[i].isDirectory()
								&& file[i].getName().lastIndexOf(".pdf") != -1) {
							pdfUri = pdfPath + "/" + file[i].getName();// data.get(position).getDatapath()现在不正确
						}

					}
					if (nowReport.getKey().equals("")) {
						Toast.makeText(context, "后台加密错误，联系谭杰、吴明杰!", 2).show();
					} else {
						// 此处解密阅读
						try {
							EncryptDecryption.DecryptionReport(
									pdfUri,
									nowReport.getKey().substring(
											0,
											nowReport.getKey().toString()
													.length() - 1));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Uri uri = Uri.parse("file://" + pdfUri);// data.get(position).getDatapath()现在不正确
																// +pdfUri
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						intent.putExtra("name", nowReport.getName());
						intent.putExtra("report", nowReport);
						intent.putExtra("pdfPath", pdfUri);
						String uriString = uri.toString();
						intent.setClass(context, PdfViewerActivity.class);
						context.startActivity(intent);
						ViewerPreferences preferences = new ViewerPreferences(
								context);
						preferences.putYourReads(uriString);
					}
				}
			});
		}else if (dataHelper.getReportListById(report.getName()).get(0)//插入数据库,但是没有下载
				.getIsLoad() != null
				&& !dataHelper.getReportListById(report.getName()).get(0)
						.getIsLoad().equals("yes")) {
			holder.download.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					context.startActivity(new Intent(
							context,
							CeiShelfBookActivity.class));
					//context.finish();
					Toast.makeText(context, "已加入书架!", 2).show();
				}
			});
		}
		return convertView;
	}

	public class Holder {
		ImageView handImg;
		TextView title;
		Button download;
	}

}
