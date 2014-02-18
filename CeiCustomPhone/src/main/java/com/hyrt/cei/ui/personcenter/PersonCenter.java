package com.hyrt.cei.ui.personcenter;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hyrt.cei.ui.witsea.WitSeaActivity;
import com.hyrt.ceiphone.ContainerActivity;
import com.hyrt.ceiphone.R;
import com.hyrt.ceiphone.ContainerActivity.MenuFragmentIsLogin;
import com.hyrt.ceiphone.common.Announcement;
import com.hyrt.ceiphone.common.HomePageDZB;
import com.hyrt.readreport.CeiShelfBookActivity;

/**
 * 个人中心
 * 
 * @author Administrator
 * 
 */
public class PersonCenter extends ContainerActivity implements OnClickListener {

	private TextView person_info,change_password;
	private RelativeLayout re;
	private Intent intent;
	private static String loginName;

	// 视图
	private Fragment current;
	private Fragment mContent;
	private Fragment fragmentPersonInfo;
	private Fragment fragmentQccountInfo;
	private Fragment fragmentChangePassword;
	// 菜单
	public Fragment mFragmentmenu;
	public FragmentManager fm;
	public FragmentTransaction ft;
	private static Intent intent_CeiShelfBookActivity = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personcentered);
		overridePendingTransition(R.anim.push_in, R.anim.push_out);
		SharedPreferences settings = getSharedPreferences("loginInfo",
				Activity.MODE_PRIVATE);
		loginName = settings.getString("LOGINNAME", "");
		init();
	}

	private void init() {
		findViewById(R.id.person_info).setOnClickListener(this);
		findViewById(R.id.change_password).setOnClickListener(this);
		re = (RelativeLayout) findViewById(R.id.pc_re);
		person_info = (TextView) findViewById(R.id.person_info);
		change_password = (TextView) findViewById(R.id.change_password);
		
		intent_CeiShelfBookActivity.setClass(this, CeiShelfBookActivity.class);
		
		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		// 菜单
		mFragmentmenu = fm.findFragmentByTag("menu");
		mFragmentmenu = new MenuFragmentIsLogin();
//		ft.add(mFragmentmenu, "menu");
		//视图
		mContent = fragmentPersonInfo = new PersonInfo();
		ft.add(R.id.pc_re, fragmentPersonInfo);
		ft.commit();

		// SwitchActivity(0);
	}

	private void switchContent(Fragment from, Fragment to) {
		if (mContent != to) {
			mContent = to;
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			if (!to.isAdded()) { // 先判断是否被add过
				transaction.hide(from).add(R.id.pc_re, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_rl:
			intent = new Intent(this, HomePageDZB.class);
			startActivity(intent);
			break;
		case R.id.notice_rl:
			intent = new Intent(this, Announcement.class);
			startActivity(intent);
			break;
		case R.id.collect_rl:
			intent = new Intent(this, WitSeaActivity.class);
			startActivity(intent);
			break;
		case R.id.psc_rl:
			intent = new Intent(this, PersonCenter.class);
			startActivity(intent);
			break;
		case R.id.person_info:
			person_info.setTextColor(Color.WHITE);
			change_password.setTextColor(Color.BLUE);
			person_info.setBackgroundResource(R.drawable.phone_study_menu_select);
			change_password.setBackgroundResource(R.drawable.menu_transbg);
			if (fragmentPersonInfo == null) {
				fragmentPersonInfo = new PersonInfo();
			}
			switchContent(mContent, fragmentPersonInfo);
			break;
		case R.id.qccount_info:
			if (fragmentQccountInfo == null) {
				fragmentQccountInfo = new QccountInfo();
			}
			switchContent(mContent, fragmentQccountInfo);
			break;
		case R.id.change_password:
			person_info.setTextColor(Color.BLUE);
			change_password.setTextColor(Color.WHITE);
			person_info.setBackgroundResource(R.drawable.menu_transbg);
			change_password.setBackgroundResource(R.drawable.phone_study_menu_select);
			if (fragmentChangePassword == null) {
				fragmentChangePassword = new ChangePassword();
			}
			switchContent(mContent, fragmentChangePassword);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		ContainerActivity.activities.remove(this);
		super.onDestroy();
	}

	public static class MenuFragmentIsLogin extends SherlockFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			menu.add("书架")
					.setIcon(R.drawable.read_report_bookshelf)
					.setIntent(intent_CeiShelfBookActivity)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

	}
}
