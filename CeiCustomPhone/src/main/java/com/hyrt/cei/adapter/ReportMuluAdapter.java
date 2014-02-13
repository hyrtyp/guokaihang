package com.hyrt.cei.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportMuluAdapter extends BaseAdapter {
	private List<String> data;
	private Activity context;
	private List<String> leftData = new ArrayList<String>();
	private List<String> rightData = new ArrayList<String>();
	
   public ReportMuluAdapter(Activity context,List<String> data){
	   this.context=context;
	   this.data=data;
	   Object[] directories = data.toArray();
		for (int i = 0; i < directories.length; i++) {
			try{
				String leftContent = "...."+directories[i].toString().substring(directories[i].toString().lastIndexOf(".")+1,directories[i].toString().length());
				leftData.add(leftContent);
			}catch(Exception e){
			}
		}
		for(int i=0;i<directories.length;i++){
			try{
				String rightContent = directories[i].toString().substring(0,directories[i].toString().length()-3)+".............................................................................................";
				rightData.add(rightContent);
			}catch(Exception e){
			}
		}
   }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView=new LinearLayout(context);
			TextView tv = new TextView(context);
			tv.setWidth(200);
			tv.setTextSize(15);
			tv.setSingleLine(true);
			tv.setPadding(10, 10, 0, 0);
			((LinearLayout)convertView).addView(tv);
			
			TextView tv1 = new TextView(context);
			tv1.setTextSize(15);
			tv1.setSingleLine(true);
			tv1.setPadding(0, 10, 0, 0);
			((LinearLayout)convertView).addView(tv1);
			
		}
		TextView leftTv = (TextView) (((LinearLayout)convertView).getChildAt(1));
		TextView rightTv = (TextView) (((LinearLayout)convertView).getChildAt(0));
		leftTv.setText(leftData.get(position));
		rightTv.setText(rightData.get(position));
		return convertView;
	}

}
