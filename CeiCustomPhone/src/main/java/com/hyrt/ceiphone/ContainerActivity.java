package com.hyrt.ceiphone;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Handler;
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
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hyrt.cei.application.CeiApplication;
import com.hyrt.cei.ui.common.LoginActivity;
import com.hyrt.cei.ui.personcenter.PersonCenter;
import com.hyrt.cei.util.AsyncImageLoader;
import com.hyrt.cei.util.TextDrawable;
import com.hyrt.cei.vo.ColumnEntry;
import com.hyrt.cei.vo.ImageResourse;
import com.hyrt.ceiphone.common.Announcement;
import com.hyrt.ceiphone.common.AnnouncementRead;
import com.hyrt.ceiphone.common.Disclaimer;
import com.hyrt.readreport.CeiShelfBookActivity;
import com.hyrt.readreport.ReadReportFL;
import com.hyrt.readreport.ReadReportFind;
import com.hyrt.readreport.ReadReportMainActivity;
import com.hyrt.readreport.ReportIntro;
import com.poqop.document.BaseViewerActivity;
import com.poqop.document.MainBrowserActivity;

public class ContainerActivity extends SherlockFragmentActivity {

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
		if(ft.isEmpty()){
			ft.add(mFragmentmenu, "menu");
			ft.commit();
		}
		getSupportActionBar().setIcon(R.drawable.guokaihang_logo2);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			/*for (int i = activities.size() - 1; i > 0; i--) {
				activities.get(i).finish();
			}*/
            this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (this instanceof ReadReportMainActivity) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		} else {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setIcon(R.drawable.cei_back);
		}

		ColumnEntry columnEntry = ((CeiApplication) getApplication()).columnEntry;
		ImageResourse imageResource = new ImageResourse();
		imageResource.setIconUrl(columnEntry.getLogo());
		imageResource.setIconId(columnEntry.getLogo());
		((CeiApplication) (this.getApplication())).asyncImageLoader
				.loadDrawable(imageResource,
						new AsyncImageLoader.ImageCallback() {

							@Override
							public void imageLoaded(Drawable drawable,
									String path) {
								if (ContainerActivity.this instanceof ReadReportMainActivity)
									getSupportActionBar().setIcon(drawable);
							}
						});
	}

	@Override
	protected void onDestroy() {
		activities.remove(this);
		super.onDestroy();
	}

	public static void destroyActivities() {
		for (int i = 0; i < activities.size(); i++) {
			activities.get(i).finish();
		}
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
			// menu.add("登录")
			// .setIcon(R.drawable.main_login)
			// .setIntent(intent_LoginActivity)
			// .setShowAsAction(
			// MenuItem.SHOW_AS_ACTION_IF_ROOM
			// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			menu.add("书架")
					.setIcon(R.drawable.read_report_bookshelf)
					.setIntent(intent_CeiShelfBookActivity)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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
			// menu.add(loginName).setShowAsAction(
			// MenuItem.SHOW_AS_ACTION_IF_ROOM
			// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

				menu.add("书架")
				//.setIcon(R.drawable.read_report_bookshelf)
				.setIntent(intent_CeiShelfBookActivity)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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

                                for (int i = 0; i < activities.size(); i++) {
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
	
	private void initTitleView(){
		//自定义actionbar
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		View viewTitleBar = getLayoutInflater().inflate(
				R.layout.action_bar_title, null);
		ImageView imagetitie=(ImageView)viewTitleBar.findViewById(R.id.title);
		TextView textView1 = (TextView)viewTitleBar.findViewById(R.id.textView1);
		if(this instanceof ReadReportMainActivity){
            imagetitie.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);
            textView1.setText("客户信息服务周刊");
		}else if(this instanceof ReadReportFL){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("分期浏览");
		}else if(this instanceof ReadReportFind){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("搜索周刊");
		}else if(this instanceof Announcement){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("通知公告");
		}else if(this instanceof AnnouncementRead){
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
		}else if(this instanceof CeiShelfBookActivity){
			imagetitie.setVisibility(View.GONE);
			textView1.setVisibility(View.VISIBLE);
			textView1.setText("我的书架");
		}else if(this instanceof ReportIntro){
            imagetitie.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);
            textView1.setText("报告详细");
        }
		getSupportActionBar().setCustomView(viewTitleBar, lp);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}

}
