package com.hyrt.cei.adapter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import com.hyrt.cei.application.CeiApplication;
import com.hyrt.cei.util.AsyncImageLoader;
import com.hyrt.cei.util.AsyncImageLoader.ImageCallback;
import com.hyrt.cei.util.BitmapManager;
import com.hyrt.cei.vo.ImageResourse;
import com.hyrt.cei.vo.Report;
import com.hyrt.ceiphone.R;
import com.hyrt.readreport.ReadReportMainActivity;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsReportAdapter extends BaseAdapter {

	private List<Report> data;
	private GridView gridView;
	private LayoutInflater inflater;
    private BitmapManager bmpManager;

	public NewsReportAdapter(ReadReportMainActivity c, GridView gridView,
			List<Report> data) {
		this.data = data;
		this.gridView = gridView;
		inflater = LayoutInflater.from(c);
        this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(c.getResources(), R.drawable.read_report_report1));
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.read_report_main_news_item,
				null);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.report_item_reportImg);
		imageView.setTag(data.get(position).getSmallPpath());
		TextView textView = (TextView) convertView
				.findViewById(R.id.report_item_reportName);
		textView.setText(data.get(position).getName().length()>15?data.get(position).getName().substring(0,14):data.get(position).getName());
        bmpManager.loadBitmap(data.get(position).getSmallPpath(),imageView,data.get(position).getId());
		return convertView;
	}

}
