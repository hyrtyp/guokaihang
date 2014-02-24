package com.poqop.document.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hyrt.cei.vo.Report;

public class MyDBHelper extends SQLiteOpenHelper{

	public MyDBHelper(Context context) {
		super(context, "myRead.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE myRead_table(id integer primary key autoincrement,book_path string,pageNo string,key string," +
                Report.REPORT_DOOR+ " VERCHAR" + ")" );
		db.execSQL("CREATE TABLE myRecentRead_table(id integer primary key autoincrement,book_path string," +
                Report.REPORT_DOOR+ " VERCHAR" + ")" );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
