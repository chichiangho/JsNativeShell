package com.missile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

/**
 * 作为其他原生Activity的基类，只有滑动手势处理
 */
public abstract class SwipeCloseBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSwipeBackFinish();
    }

    private void initSwipeBackFinish() {
        final LimitedSlidingPaneLayout slidingPaneLayout = new LimitedSlidingPaneLayout(this);

        //通过反射改变mOverhangSize的值为0，这个mOverhangSize值为菜单到右边屏幕的最短距离，默认是32dp，现在给它改成0
        try {
            Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            f_overHang.setAccessible(true);
            f_overHang.set(slidingPaneLayout, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final RelativeLayout leftView = new RelativeLayout(this);
        leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        leftView.setBackgroundResource(android.R.color.transparent);

        final View shadow = new View(this);
        shadow.setLayoutParams(new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.swipecloseactivity_shadow_width), RelativeLayout.LayoutParams.MATCH_PARENT));
        shadow.setBackgroundResource(R.drawable.swipecloseactivity_left_shadow);
        leftView.addView(shadow);
        slidingPaneLayout.addView(leftView, 0);

        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(@NonNull View panel, float slideOffset) {
                if (slideOffset >= 0.99) {
                    shadow.setVisibility(View.GONE);
                } else {
                    shadow.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) shadow.getLayoutParams();
                    param.setMargins((int) (slideOffset * leftView.getWidth() - shadow.getWidth()), 0, 0, 0);
                    shadow.setLayoutParams(param);
                }
            }

            @Override
            public void onPanelOpened(@NonNull View panel) {
                finish();
                SwipeCloseBaseActivity.this.overridePendingTransition(0, R.anim.slide_out_right);
            }

            @Override
            public void onPanelClosed(@NonNull View panel) {
            }
        });
        slidingPaneLayout.setSliderFadeColor(getResources().getColor(android.R.color.transparent));

        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        final ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundColor(getResources().getColor(android.R.color.white));
        decor.removeView(decorChild);
        decor.addView(slidingPaneLayout);
        slidingPaneLayout.addView(decorChild, 1);
    }
}
