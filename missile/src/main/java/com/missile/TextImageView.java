package com.missile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class TextImageView extends FrameLayout {
    private ImageView iv;
    private TextView tv;

    public TextImageView(@NonNull Context context) {
        super(context);
        initView();
    }

    public TextImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        iv = new ImageView(getContext());
        iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(iv);
        tv = new TextView(getContext());
        tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tv);
    }

    public void setText(String str) {
        tv.setText(str);
    }

    public void setImageDrawable(Drawable drawable) {
        iv.setImageDrawable(drawable);
    }

    public void setGravity(int gravity) {
        tv.setGravity(gravity);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        iv.setScaleType(scaleType);
    }

    public void setTextColor(int textColor) {
        tv.setTextColor(textColor);
    }

    public void setTextSize(int complexUnitSp, int textSize) {
        tv.setTextSize(complexUnitSp, textSize);
    }
}
