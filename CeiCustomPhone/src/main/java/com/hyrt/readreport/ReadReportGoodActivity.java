package com.hyrt.readreport;

import java.util.ArrayList;
import java.util.List;

import com.hyrt.cei.adapter.GoodReportAdapter;
import com.hyrt.cei.adapter.ReadReportAdapter;
import com.hyrt.cei.application.CeiApplication;
import com.hyrt.cei.ui.common.LoginActivity;
import com.hyrt.cei.util.MyTools;
import com.hyrt.cei.util.WriteOrRead;
import com.hyrt.cei.util.XmlUtil;
import com.hyrt.cei.vo.ColumnEntry;
import com.hyrt.cei.vo.Report;
import com.hyrt.cei.webservice.service.Service;
import com.hyrt.ceiphone.ContainerActivity;
import com.hyrt.ceiphone.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ReadReportGoodActivity extends ContainerActivity implements
		OnClickListener {
	private ColumnEntry columnEntry;
	private ListView goodList;
	private List<Report> goodData;
	private int pageindex = 1;
	private TextView moreText;
	ReadReportAdapter adapter;
	private String bgId;
	private Handler goodHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 12) {
				if (msg.arg2 < 20) {
					moreText.setVisibility(View.GONE);
				}
				if (adapter != null)
					adapter.notifyDataSetChanged();
			} else {
				adapter = new ReadReportAdapter(ReadReportGoodActivity.this,
						goodData, goodList);
				goodList.setAdapter(adapter);
				if (goodData.size() < 20)
					moreText.setVisibility(View.GONE);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_report_find);
		initView();
		initData();
	}

	private void initView() {
		goodList = (ListView) findViewById(R.id.read_report_data_lv);
		moreText = (TextView) findViewById(R.id.read_report_more);
		moreText.setOnClickListener(this);
	}

	private void initData() {
		columnEntry = ((CeiApplication) getApplication()).columnEntry;
		goodData = new ArrayList<Report>();
		// 更据业务ID查询业务里面的数据
		if (ReadReportMainActivity.allColBg != null
				&& ReadReportMainActivity.allColBg.getId() != null
				&& !ReadReportMainActivity.allColBg.getId().equals("")) {
			String allBgId = ReadReportMainActivity.allColBg.getId();
			List<ColumnEntry> allCol = columnEntry
					.getEntryChildsForParent(allBgId);
			for (ColumnEntry columnEntry : allCol) {
				if (columnEntry.getName().equals("精品推荐")) {
					bgId = columnEntry.getId();
				}
			}
		}
		if (((CeiApplication) getApplication()).isNet()) {
			new Thread() {

				@Override
				public void run() {
					// 所有精彩报告
					// colBg = columnEntry.getColByName("精品推荐");
					if (bgId != null && !bgId.equals("")) {
						String retData = Service.queryReport(bgId, "bg",
								pageindex + "");
						try {
							goodData.clear();
							goodData.addAll(XmlUtil.parseReport(retData));
							if (goodHandler != null) {
								goodHandler.sendEmptyMessage(1);
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
				goodData.addAll(XmlUtil.parseReport(WriteOrRead.read(
						MyTools.nativeData, "goodReport.xml")));
				if (goodHandler != null) {
					goodHandler.sendEmptyMessage(1);
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
		case R.id.read_report_more:
			// 加载更多
			if (bgId != null && !bgId.equals("")) {
				pageindex++;
				String retData = Service
						.queryReport(bgId, "bg", pageindex + "");
				try {
					goodData.addAll(XmlUtil.parseReport(retData));
					Message msg = new Message();
					msg.arg1 = 12;
					msg.arg2 = XmlUtil.parseReport(retData).size();
					goodHandler.sendMessage(msg);
				} catch (Exception e) {
					MyTools.showPushXml(getApplicationContext());
					e.printStackTrace();
				}
			}
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
