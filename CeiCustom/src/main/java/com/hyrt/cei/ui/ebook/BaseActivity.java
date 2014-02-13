package com.hyrt.cei.ui.ebook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hyrt.cei.R;
import com.hyrt.cei.ui.common.LoginActivity;
import com.hyrt.cei.ui.main.Announcement;
import com.hyrt.cei.ui.main.Disclaimer;
import com.hyrt.cei.ui.personcenter.PersonCenter;

public class BaseActivity extends SherlockFragmentActivity{

	public static String loginName;

	// 菜单
	public Fragment mFragmentmenu;
	public FragmentManager fm;
	public FragmentTransaction ft;

	public static Intent intent_CeiShelfBookActivity = new Intent();
	public static Intent intent_LoginActivity = new Intent();

	// 维护activity集合
	public static final List<Activity> activities = new ArrayList<Activity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initTitleView();
		 if(!(this instanceof ReportIntro)){
				for(int i=0;i<activities.size();i++){
					activities.get(i).finish();
					activities.remove(i);
				}
		 }
		activities.add(this);

		super.onCreate(savedInstanceState);

		showLoginBtnByUserName();

		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		mFragmentmenu = fm.findFragmentByTag("menu");
		if (mFragmentmenu == null) {
			if (loginName.equals("")) {
				mFragmentmenu = new MenuFragmentNoLogin();
			} else {
				mFragmentmenu = new MenuFragmentIsLogin();
			}
		}
		intent_CeiShelfBookActivity.setClass(this, CeiShelfBookActivity.class);
		intent_CeiShelfBookActivity.putExtra("isDownload",false);
		intent_LoginActivity.setClass(this, LoginActivity.class);
		// 添加菜单
		ft.add(mFragmentmenu, "menu");
		ft.commit();
		getSupportActionBar().setIcon(R.drawable.guokaihang_logo);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			for (int i = activities.size()-1; i >0 ; i--) {
				activities.get(i).finish();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		activities.remove(this);
		super.onDestroy();
	}

	/**
	 * A fragment that displays a menu. This fragment happens to not have a UI
	 * (it does not implement onCreateView), but it could also have one if it
	 * wanted.
	 */
	public static class MenuFragmentNoLogin extends SherlockFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//			menu.add("登录")
//					.setIcon(R.drawable.phone_study_saygroup_item_icon)
//					.setIntent(intent_LoginActivity)
//					.setShowAsAction(
//							MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("书架")
					.setIcon(R.drawable.bookshelf)
					.setIntent(intent_CeiShelfBookActivity)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		
	}

	public static class MenuFragmentIsLogin extends SherlockFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//			menu.add(loginName).setShowAsAction(
//					MenuItem.SHOW_AS_ACTION_IF_ROOM
//							| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			menu.add("书架")
					//.setIcon(R.drawable.guokaihang_zhoukan)
					.setIntent(intent_CeiShelfBookActivity)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("退出")
			.setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		
		@Override
	    public boolean onOptionsItemSelected(final MenuItem item) {
			if(item.getTitle().equals("退出")){
				View view = LayoutInflater.from(getActivity()).inflate(
						R.layout.pop_exit_show, null);
				final PopupWindow mPopupWindow = new PopupWindow(view,
						LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				mPopupWindow.setFocusable(true);
				mPopupWindow.setTouchable(true);
				mPopupWindow.setOutsideTouchable(true);
				mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
				TextView textTitle = (TextView) view
						.findViewById(R.id.pop_exit_show_title);
				textTitle.setText("确认退出此应用吗！");
				view.findViewById(R.id.pop_exit_show_yes)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								mPopupWindow.dismiss();
								
							    	  for(int i=0;i<activities.size();i++){
							    		  activities.get(i).finish();
							    	  }
							      }
							
						});
				view.findViewById(R.id.pop_exit_show_exit)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								mPopupWindow.dismiss();
							}
						});
				mPopupWindow.showAtLocation(((Activity) getActivity())
						.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
				}
		        return false;
	    }
		

	}

	// 根据登陆与否判断是否显示登陆按钮
	private void showLoginBtnByUserName() {
		// 获取登陆名
		SharedPreferences settings = getSharedPreferences("loginInfo",
				Activity.MODE_PRIVATE);
		loginName = settings.getString("LOGINNAME", "");
	}
	
	/**
	 * 所在的活动 manifest android:label=""
	 */
	private void initTitleView(){
		//自定义actionbar
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		View viewTitleBar = getLayoutInflater().inflate(
				R.layout.action_bar_title, null);
		ImageView imagetitie=(ImageView)viewTitleBar.findViewById(R.id.title);
		TextView textView1 = (TextView)viewTitleBar.findViewById(R.id.textView1);
		if(this instanceof ReadReportActivity){
		}else if(this instanceof PartitionReportActivity){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("分期浏览");
		}else if(this instanceof FindReportActivity){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("搜索周刊");
		}else if(this instanceof Announcement){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("通知公告");
		}else if(this instanceof PersonCenter){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("个人中心");
		}else if(this instanceof Disclaimer){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("关于我们");
		}else if(this instanceof ReportIntro){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("周刊详情");
		}
		getSupportActionBar().setCustomView(viewTitleBar, lp);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}
	
}
