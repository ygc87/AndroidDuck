package com.xhy.neihanduanzi.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xhy.neihanduanzi.XHYApplication;
import com.xhy.neihanduanzi.utils.DisplayUtils;


public class ResizableImageView extends ImageView {


    public ResizableImageView(Context context) {
        super(context);
    }

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int width = DisplayUtils.getDisplayWidth(XHYApplication.getAppContext());
            //高度根据使得图片的宽度充满屏幕计算而得
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
//            setMaxWidth(width);
//            setMaxHeight(height);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}

