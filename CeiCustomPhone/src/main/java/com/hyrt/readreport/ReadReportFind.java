package com.hyrt.readreport;

import java.util.List;

import com.hyrt.cei.adapter.ReadReportAdapter;
import com.hyrt.cei.application.CeiApplication;
import com.hyrt.cei.util.MyTools;
import com.hyrt.cei.util.WriteOrRead;
import com.hyrt.cei.util.XmlUtil;
import com.hyrt.cei.vo.ColumnEntry;
import com.hyrt.cei.vo.Report;
import com.hyrt.cei.webservice.service.Service;
import com.hyrt.ceiphone.ContainerActivity;
import com.hyrt.ceiphone.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ReadReportFind extends ContainerActivity implements
		OnClickListener {

	private ColumnEntry columnEntry;
	private ListView goodList;
	private ImageView findImg;
	private EditText reportName;
	private LinearLayout findLine;
	private List<Report> findData;
	private StringBuilder colIDs = null;
	private ReadReportAdapter adapter;
	private int pageindex = 1;
	private TextView moreText;
	private Handler findHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 5) {
				if (adapter != null)
					adapter.notifyDataSetChanged();
			} else if (msg.arg1 == 12) {
				if (msg.arg2 < 20) {
					moreText.setVisibility(View.GONE);
				}
				if (adapter != null)
					adapter.notifyDataSetChanged();
			} else {
				if (findData == null)
					return;
				if (findData.size() == 0) {
					MyTools.exitShow(ReadReportFind.this, ReadReportFind.this
							.getWindow().getDecorView(), "没有查到您需要的信息!");
				}
				adapter = new ReadReportAdapter(ReadReportFind.this, findData,goodList);
				goodList.setAdapter(adapter);
				if (findData.size() < 20)
					moreText.setVisibility(View.GONE);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_report_find);
		columnEntry = ((CeiApplication) getApplication()).columnEntry;
		initView();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	private void initView() {
		findLine = (LinearLayout) findViewById(R.id.find_line);
		findLine.setVisibility(View.VISIBLE);
		goodList = (ListView) findViewById(R.id.read_report_data_lv);
		findImg = (ImageView) findViewById(R.id.read_report_findreport);
		findImg.setOnClickListener(this);
		reportName = (EditText) findViewById(R.id.read_report_ettext);
		SharedPreferences settings = getSharedPreferences("search_result",
				Activity.MODE_PRIVATE);
		String historyStr = settings.getString("history_report", "");
		reportName.setText(historyStr);
		moreText = (TextView) findViewById(R.id.read_report_more);
		moreText.setOnClickListener(this);
	}

	private void initData() {
		if (((CeiApplication) getApplication()).isNet()) {
			new Thread() {
				@Override
				public void run() {
					String sortBg = "";
					ColumnEntry allColBg = columnEntry
							.getColByName(ReadReportMainActivity.MODEL_NAME);
					if (allColBg != null && allColBg.getId() != null
							&& !allColBg.getId().equals("")) {
						String allBgId = allColBg.getId();
						colIDs = new StringBuilder();
						List<ColumnEntry> allCol = columnEntry
								.getEntryChildsForParent(allBgId);
						for (ColumnEntry columnEntry : allCol) {
							colIDs.append(columnEntry.getId() + ",");
						}
						if (colIDs != null && !colIDs.equals("")) {
							if (reportName.getText().toString().trim()
									.equals("")
									|| reportName.getText().toString() == null) {
								return;
							}
							SharedPreferences settings = getSharedPreferences(
									"search_result", Activity.MODE_PRIVATE);
							Editor editor = settings.edit();
							editor.putString("history_report", reportName
									.getText().toString());
							editor.commit();
							sortBg = Service.queryReportByName(
									colIDs.toString().substring(0,
											colIDs.toString().length() - 1),
									pageindex + "", reportName.getText()
											.toString());
						}
					}

					if (!sortBg.equals("")) {
						try {
							findData = XmlUtil.parseReport(sortBg);
							WriteOrRead.write(sortBg, MyTools.nativeData,
									"findReport.xml");
							if (findHandler != null) {
								findHandler.sendEmptyMessage(1);
							}
						} catch (Exception e) {
							MyTools.showPushXml(getApplicationContext());
							e.printStackTrace();
						}
					}
				}

			}.start();
		} else {
			try {
				findData = XmlUtil.parseReport(WriteOrRead.read(
						MyTools.nativeData, "findReport.xml"));
				if (findHandler != null) {
					findHandler.sendEmptyMessage(1);
				}
			} catch (Exception e) {
				MyTools.showPushXml(getApplicationContext());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.read_report_findreport:
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(reportName.getWindowToken(), 0);
			if (reportName.getText() != null
					&& !reportName.getText().toString().trim().equals(""))
				moreText.setVisibility(View.VISIBLE);
			initData();
			break;
		case R.id.read_report_more:
			// 加载更多
			new Thread() {

				@Override
				public void run() {
					if (reportName.getText().toString().trim().equals("")
							|| reportName.getText().toString() == null) {
						return;
					}
					if (colIDs != null) {
						try {
							pageindex++;
							String newRetData = Service.queryReportByName(
									colIDs.toString().substring(0,
											colIDs.toString().length() - 1),
									pageindex + "", reportName.getText()
											.toString());
							findData.addAll(XmlUtil.parseReport(newRetData));
							Message msg = new Message();
							msg.arg1 = 12;
							msg.arg2 = XmlUtil.parseReport(newRetData).size();
							findHandler.sendMessage(msg);
							return;
						} catch (Exception e) {
							MyTools.showPushXml(getApplicationContext());
							e.printStackTrace();
						}
					}
				}
			}.start();
			break;
		}

	}

	@Override
	protected void onResume() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
		super.onResume();
	}
}
