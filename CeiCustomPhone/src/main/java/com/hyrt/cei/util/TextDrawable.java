package com.hyrt.cei.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * 自定义异常图片类
 * @author yepeng
 *
 */
public class TextDrawable extends Drawable {

    protected Paint paint = new Paint();
    private final String text;

    public TextDrawable(Context paramContext, String paramString) {
        this.paint.setColor(Color.RED);
        this.paint.setStyle(Paint.Style.FILL);
        int textSize;
        if (DPIUtil.isBigScreen())
            textSize = 12;
        else
            textSize = DPIUtil.dip2px(12.0F);
        this.paint.setTextSize(textSize);
        this.paint.setTextAlign(Paint.Align.CENTER);
        this.paint.setAntiAlias(true);
        this.text = paramString;
    }

    @Override
    public void draw(Canvas paramCanvas) {
        setBounds(0,0, 100,100);
        Rect localRect = getBounds();
        float x = localRect.right - localRect.width() / 2;
        float y = localRect.bottom - localRect.height() / 2;
        paramCanvas.drawText(this.text, x, y, this.paint);
    }

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int paramInt) {
    }

    public void setColorFilter(ColorFilter paramColorFilter) {
    }
}