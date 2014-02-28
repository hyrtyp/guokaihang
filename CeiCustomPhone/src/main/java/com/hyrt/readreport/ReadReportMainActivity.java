package com.hyrt.readreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hyrt.cei.adapter.GoodReportAdapter;
import com.hyrt.cei.adapter.NewsReportAdapter;
import com.hyrt.cei.application.CeiApplication;
import com.hyrt.cei.update.UpdateManager;
import com.hyrt.cei.util.AsyncImageLoader;
import com.hyrt.cei.util.MyTools;
import com.hyrt.cei.util.WriteOrRead;
import com.hyrt.cei.util.XmlUtil;
import com.hyrt.cei.vo.ColumnEntry;
import com.hyrt.cei.vo.New;
import com.hyrt.cei.vo.Report;
import com.hyrt.cei.webservice.service.Service;
import com.hyrt.ceiphone.ContainerActivity;
import com.hyrt.ceiphone.R;
import com.hyrt.ceiphone.common.HomePageDZB;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadReportMainActivity extends ContainerActivity implements
		OnClickListener, OnItemClickListener {

	public static boolean bbStart;
	public static String MODEL_NAME;
	// 研究报告的子集业务集合
	public static ColumnEntry allColBg;
	private String jcBgId;
	private ColumnEntry columnEntry;
	private CeiApplication application;
	private StringBuilder colIDs = null;
	private List<Report> goodReportData, newReportData;
	private ImageView point1, point2, point3, point4, point5;
	private GridView newsReport;
	private TextView moreText;
	private Gallery goodReport;
	private int pageindex = 1;
	private NewsReportAdapter newsAdapter;
	private Handler viewHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 12 && newReportData.size() > 0) {
				if (msg.arg2 < 18 || newReportData.size() >= 36) {
					moreText.setVisibility(View.GONE);
				}
				if (newsAdapter != null)
					newsAdapter.notifyDataSetChanged();
			} else if (msg.arg1 == 3) {
				// Toast.makeText(this, "同步错误", 1).show();
				MyTools.exitShow(ReadReportMainActivity.this,
						ReadReportMainActivity.this.getWindow().getDecorView(),
						"没有数据！");
			} else {
				GoodReportAdapter adapter = new GoodReportAdapter(
						ReadReportMainActivity.this, goodReportData, goodReport);
				goodReport.setAdapter(adapter);
				goodReport.setSelection(Integer.MAX_VALUE / 2 - 3);
				point1.setBackgroundResource(R.drawable.read_report_index_select);
				newsAdapter = new NewsReportAdapter(
						ReadReportMainActivity.this, newsReport, newReportData);
				if (newReportData != null && newReportData.size() < 18)
					moreText.setVisibility(View.GONE);
				newsReport.setAdapter(newsAdapter);
				newsReport.setOnItemClickListener(ReadReportMainActivity.this);
				goodReport
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								if (arg2 % 5 == 0) {
									point1.setBackgroundResource(R.drawable.read_report_index_select);
									point2.setBackgroundResource(R.drawable.home_img_ratio);
									point3.setBackgroundResource(R.drawable.home_img_ratio);
									point4.setBackgroundResource(R.drawable.home_img_ratio);
									point5.setBackgroundResource(R.drawable.home_img_ratio);

								}
								if (arg2 % 5 == 1) {
									point2.setBackgroundResource(R.drawable.read_report_index_select);
									point1.setBackgroundResource(R.drawable.home_img_ratio);
									point3.setBackgroundResource(R.drawable.home_img_ratio);
									point4.setBackgroundResource(R.drawable.home_img_ratio);
									point5.setBackgroundResource(R.drawable.home_img_ratio);
								}
								if (arg2 % 5 == 2) {
									point3.setBackgroundResource(R.drawable.read_report_index_select);
									point1.setBackgroundResource(R.drawable.home_img_ratio);
									point2.setBackgroundResource(R.drawable.home_img_ratio);
									point4.setBackgroundResource(R.drawable.home_img_ratio);
									point5.setBackgroundResource(R.drawable.home_img_ratio);
								}
								if (arg2 % 5 == 3) {
									point4.setBackgroundResource(R.drawable.read_report_index_select);
									point1.setBackgroundResource(R.drawable.home_img_ratio);
									point2.setBackgroundResource(R.drawable.home_img_ratio);
									point3.setBackgroundResource(R.drawable.home_img_ratio);
									point5.setBackgroundResource(R.drawable.home_img_ratio);
								}
								if (arg2 % 5 == 4) {
									point5.setBackgroundResource(R.drawable.read_report_index_select);
									point1.setBackgroundResource(R.drawable.home_img_ratio);
									point3.setBackgroundResource(R.drawable.home_img_ratio);
									point4.setBackgroundResource(R.drawable.home_img_ratio);
									point2.setBackgroundResource(R.drawable.home_img_ratio);
								}

							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}
						});
			}
		}

	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_report_main);
		/*UpdateManager manager = new UpdateManager(this);
		// 检查软件更新
		manager.isUpdate();*/
        ((CeiApplication) getApplication()).nowStart = MODEL_NAME;
		columnEntry = ((CeiApplication) getApplication()).columnEntry;
		try {
			bbStart = columnEntry.getColumnEntryChilds().get(0).getName()
					.equals("通用版") ? true : false;
		} catch (Exception e) {
		}
		application = (CeiApplication) getApplication();
		initView();
		initData();
	}

	private void initView() {
		goodReport = (Gallery) findViewById(R.id.read_report_main_ga);
		newsReport = (GridView) findViewById(R.id.read_report_main_gv);
		moreText = (TextView) findViewById(R.id.read_report_more);
		moreText.setOnClickListener(this);
		point1 = (ImageView) findViewById(R.id.read_report_point1);
		point2 = (ImageView) findViewById(R.id.read_report_point2);
		point3 = (ImageView) findViewById(R.id.read_report_point3);
		point4 = (ImageView) findViewById(R.id.read_report_point4);
		point5 = (ImageView) findViewById(R.id.read_report_point5);
	}

	private void initData() {
		// 更据业务ID查询业务里面的数据
		goodReportData = new ArrayList<Report>();
		newReportData = new ArrayList<Report>();
		allColBg = columnEntry.getColByName(MODEL_NAME);
		if (allColBg != null && allColBg.getId() != null
				&& !allColBg.getId().equals("")) {
			String allBgId = allColBg.getId();
			colIDs = new StringBuilder();
			List<ColumnEntry> allCol = columnEntry
					.getEntryChildsForParent(allBgId);
			for (ColumnEntry columnEntry : allCol) {
				if (columnEntry.getName().equals("置顶报告")) {
					jcBgId = columnEntry.getId();
				}
				colIDs.append(columnEntry.getId() + ",");
			}
		}

		new Thread() {
			@Override
			public void run() {
				if (jcBgId != null && !jcBgId.equals("")) {
					try {
						if (((CeiApplication) (getApplication())).isNet()) {
							String retData = Service.queryReport(jcBgId, "bg",
									"1");
							goodReportData.addAll(XmlUtil.parseReport(retData));
							if (goodReportData.size() < 5) {
								// 如果为空
								goodReportData.add(new Report());
								goodReportData.add(new Report());
								goodReportData.add(new Report());
								goodReportData.add(new Report());
								goodReportData.add(new Report());
							}

							String newRetData = Service.queryNewReport(
									colIDs.toString().substring(0,
											colIDs.toString().length() - 1),
									pageindex + "");
							newReportData = XmlUtil.parseReport(newRetData);
							// 本地缓存
							WriteOrRead.write(retData, MyTools.nativeData,
									"goodReport.xml");
							WriteOrRead.write(newRetData, MyTools.nativeData,
									"newReport.xml");
							String buyReport = Service
									.queryBuyReport(columnEntry.getUserId());
							WriteOrRead.write(buyReport, MyTools.nativeData,
									"buyReport.xml");
							application.buyReportData.clear();
							application.buyReportData.addAll(XmlUtil
									.queryBuyReports(buyReport));// 此处缺解析方法
						} else {
							String gooddata = WriteOrRead.read(
									MyTools.nativeData, "goodReport.xml");
							String newsdata = WriteOrRead.read(
									MyTools.nativeData, "newReport.xml");
							String buydata = WriteOrRead.read(
									MyTools.nativeData, "buyReport.xml");
							goodReportData = XmlUtil.parseReport(gooddata);
							newReportData = XmlUtil.parseReport(newsdata);
							if (goodReportData.size() < 5) {
								// 如果为空
								goodReportData.add(new Report());
								goodReportData.add(new Report());
								goodReportData.add(new Report());
								goodReportData.add(new Report());
								goodReportData.add(new Report());
							}
							application.buyReportData.clear();
							application.buyReportData.addAll(XmlUtil
									.queryBuyNews(buydata));
						}
						if (viewHandler != null) {
							viewHandler.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						Message msg = new Message();
						msg.arg1 = 3;
						viewHandler.sendMessage(msg);
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.read_report_back:
			intent = new Intent(this, HomePageDZB.class);
			startActivity(intent);
			break;
		case R.id.read_report_more:
			// 加载更多
			new Thread() {

				@Override
				public void run() {
					if (colIDs != null) {
						try {
							pageindex++;
							String newRetData = Service.queryNewReport(
									colIDs.toString().substring(0,
											colIDs.toString().length() - 1),
									pageindex + "");
							if (!newRetData
									.equals("<?xml version='1.0' encoding='utf-8'?><ROOT></ROOT>")) {
								if (XmlUtil.parseReport(newRetData) != null) {
									newReportData.addAll(XmlUtil
											.parseReport(newRetData));
								}
							}
							Message msg = new Message();
							msg.arg1 = 12;
							msg.arg2 = XmlUtil.parseReport(newRetData).size();
							viewHandler.sendMessage(msg);
							return;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}.start();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Report report = (Report) arg0.getAdapter().getItem(arg2);
		if (report.getId() == null || report.getId().equals("")) {
			return;
		}
		Intent intent = new Intent(this, ReportIntro.class);// OpenBookActivity
		intent.putExtra("report", report);
		startActivity(intent);
	}

}
