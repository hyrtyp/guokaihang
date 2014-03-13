package com.hyrt.cei.adapter;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hyrt.cei.application.CeiApplication;
import com.hyrt.cei.db.DataHelper;
import com.hyrt.cei.ui.common.RegistActivity;
import com.hyrt.cei.util.AsyncImageLoader;
import com.hyrt.cei.util.AsyncImageLoader.ImageCallback;
import com.hyrt.cei.vo.ImageResourse;
import com.hyrt.cei.vo.Report;
import com.hyrt.ceiphone.R;
import com.hyrt.readreport.CeiShelfBookActivity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BookSelfAdapter extends BaseAdapter {
    private List<Report> data;
    private GridView gridView;
    private LayoutInflater inflater;
    private AsyncImageLoader asyncImageLoader;
    DataHelper dataHelper;
    private List<String> indexs = new ArrayList<String>();
    public View viewBookSelf;
    private HashMap<String, SoftReference<Drawable>> drawables = new
            HashMap<String, SoftReference<Drawable>>();
    private Activity activity;

    public BookSelfAdapter(Activity activity, List<Report> data, GridView gridView) {
        this.data = data;
        this.gridView = gridView;
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        asyncImageLoader = ((CeiApplication) activity.getApplication()).asyncImageLoader;
        dataHelper = ((CeiApplication) activity.getApplication()).dataHelper;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // ImageView imageView;
        indexs.add(position, "no");
        final ProgressBar bar;
        final TextView tv;
        final Report report = data.get(position);
        if (convertView == null) {
            convertView = inflater
                    .inflate(R.layout.read_report_book_item, null);
        }
        final View convertView1 = convertView;
        if (viewBookSelf == null)
            viewBookSelf = convertView;
        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.yjbg_book_item_iv);
        imageView.setTag(report.getSmallPpath());
        imageView.setImageResource(R.drawable.read_report_report1);
        final ImageResourse imageResource = new ImageResourse();
        imageResource
                .setIconUrl(report.getSmallPpath().replace("/big.png", ""));
        imageResource.setIconId(report.getId());
        imageResource.setIconTime(report.getProtime());

        if (drawables.containsKey(report.getSmallPpath()) &&
                drawables.get(report.getSmallPpath()) != null) {
            imageView.setImageDrawable(drawables.get(
                    report.getSmallPpath()).get());
            Log.i("view", position + "起作用了");
        } else {


            asyncImageLoader.loadDrawable(imageResource, new ImageCallback() {

                @Override
                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                    ImageView img = (ImageView) gridView.findViewWithTag(report
                            .getSmallPpath());
                    if (img != null && imageDrawable != null) {

                        drawables.put(report.getSmallPpath(), new
                                SoftReference<Drawable>(imageDrawable));
                        //img.setLayoutParams(new LinearLayout.LayoutParams(360, 450));
                        //img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        img.setImageDrawable(imageDrawable);
                    }
                }
            });

        }
        if (report.getIsLoad() != null && report.getIsLoad().equals("yes")) {
            tv = (TextView) convertView.findViewById(R.id.yjbg_book_item_tv);
            bar = (ProgressBar) convertView
                    .findViewById(R.id.yjbg_book_item_pro);
            bar.setVisibility(View.INVISIBLE);
            tv.setText("100%");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CeiShelfBookActivity)activity).ShelfDownload(convertView1, position);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Report report = data.get(position);
                    ((CeiShelfBookActivity)activity).alertIsSurePop(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ((CeiShelfBookActivity)activity).popWin.dismiss();
                            data.remove(report);
                            if (dataHelper.delReport(report.getId()) == 0) {
                                ((CeiShelfBookActivity)activity).showAlert("删除失败");
                                ((CeiShelfBookActivity)activity).popWin.dismiss();
                                return;
                            } else {
                                // 删除成功后把本地文件也删除
                                String pdfPath = report.getDatapath();
                                File dir = new File(pdfPath);
                                ((CeiShelfBookActivity)activity).delAll(dir);
                                Message msg = new Message();
                                msg.what = 10;
                                ((CeiShelfBookActivity)activity).handler.sendMessage(msg);
                                ((CeiShelfBookActivity)activity).popWin.dismiss();
                                ((CeiShelfBookActivity)activity).showAlert("删除成功");
                            }
                        }
                    });
                    return true;
                }
            });
        }

        TextView textView = (TextView)convertView.findViewById(R.id.yjbg_book_title_tv);
        textView.setText(report.getName().substring(report.getName().indexOf("(")));
        //convertView.setLayoutParams(new AbsListView.LayoutParams(125, 210));
        return convertView;
    }
}
