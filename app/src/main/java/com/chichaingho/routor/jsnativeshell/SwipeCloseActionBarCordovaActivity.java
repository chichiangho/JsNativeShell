package com.chichaingho.routor.jsnativeshell;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.chichaingho.routor.jsnativeshell.entity.PageInfo;

import org.xwalk.core.XWalkGetBitmapCallback;

import java.lang.reflect.Field;
import java.util.Stack;

/**
 * 原生标题栏加webview结构的页面，实现手势滑动关闭的类
 *
 * @warning 此类在单webView多页面情况下不能支持横竖屏切换，否则截图栈会有问题。
 * 如需支持横屏，继承BaseActionBarCordovaActivity另起炉灶，不支持手势，专门处理横竖屏问题，
 * 或者在需要支持横屏的页面，在以loadUrl加载此页面时不以inCurPage的方式加载，且不以此方式离开此页面
 */
public class SwipeCloseActionBarCordovaActivity extends BaseActionBarCordovaActivity {

    private RelativeLayout leftView;
    private Stack<Bitmap> leftBg = new Stack<>();
    private Bitmap exceptWebView;
    private Drawable maskDrawable;
    private View maskView;
    private LimitedSlidingPaneLayout slidingPaneLayout;

    private void createMaskView() {
        if (maskView != null)
            return;
        maskView = new View(SwipeCloseActionBarCordovaActivity.this);
        maskView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) getWindow().getDecorView()).addView(maskView);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSwipeBackFinish();
    }

    public void setSwipeAble(boolean swipeAble) {
        slidingPaneLayout.setTouchAble(swipeAble);
    }

    @Override
    public void loadUrl(PageInfo info) {
        if (pages.size() > 0) {//switch page in activity, screenshot for swipe close url
            //screenshot except webView
            titleBar.setDrawingCacheEnabled(true);
            exceptWebView = Bitmap.createBitmap(titleBar.getDrawingCache());
            titleBar.setDrawingCacheEnabled(false);

            //screenshot webView
            try {
                webView.captureBitmapAsync(new XWalkGetBitmapCallback() {
                    @Override
                    public void onFinishGetBitmap(Bitmap bitmap, int i) {
                        pushBg(exceptWebView, bitmap);
                    }
                });
            } catch (Exception e) {
                webView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(webView.getDrawingCache());
                webView.setDrawingCacheEnabled(false);
                pushBg(exceptWebView, bitmap);
            }

            //懒初始化maskView
            if (maskView == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createMaskView();
                    }
                });
            }
        }
        super.loadUrl(info);
    }

    private void pushBg(Bitmap exceptWebView, Bitmap webView) {
        int top = titleBar.getVisibility() == View.VISIBLE ? titleBar.getHeight() : 0;
        Bitmap result = Bitmap.createBitmap(exceptWebView.getWidth(), top + webView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        if (top > 0)
            canvas.drawBitmap(exceptWebView, 0, 0, null);
        canvas.drawBitmap(webView, 0, top, null);
        exceptWebView.recycle();
        webView.recycle();
        leftBg.push(result);
    }

    private void initSwipeBackFinish() {
        slidingPaneLayout = new LimitedSlidingPaneLayout(this);

        //通过反射改变mOverhangSize的值为0，这个mOverhangSize值为菜单到右边屏幕的最短距离，默认是32dp，现在给它改成0
        try {
            Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            f_overHang.setAccessible(true);
            f_overHang.set(slidingPaneLayout, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        leftView = new RelativeLayout(this);
        leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final View shadow = new View(this);
        shadow.setLayoutParams(new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.swipecloseactivity_shadow_width), RelativeLayout.LayoutParams.MATCH_PARENT));
        shadow.setBackgroundResource(R.drawable.swipecloseactivity_left_shadow);
        leftView.addView(shadow);
        slidingPaneLayout.addView(leftView, 0);

        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            boolean setedbg = false;

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPanelSlide(@NonNull View panel, float slideOffset) {
                if (!setedbg) {
                    setedbg = true;
                    if (leftBg.size() > 0) {//switch page in activity,use screenshot as background for a fake previous view
                        maskDrawable = new BitmapDrawable(getResources(), leftBg.lastElement());
                        leftView.setBackground(maskDrawable);
                    } else {
                        leftView.setBackgroundResource(android.R.color.transparent);
                    }
                }
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
                handleBackClick();
                if (leftBg.size() > 0) {//switch page in activity,close pane and use a maskView as fake previous view
                    slidingPaneLayout.setTouchAble(false);
                    slidingPaneLayout.closePane();//will call onPanelSlide and call onPanelClosed when done
                    maskView.setBackground(maskDrawable);
                    maskView.setVisibility(View.VISIBLE);
                    leftBg.pop();
                } else {//finish activity
                    setedbg = false;
                    SwipeCloseActionBarCordovaActivity.this.overridePendingTransition(0, R.anim.slide_out_right);
                }
            }

            @Override
            public void onPanelClosed(@NonNull View panel) {
                setedbg = false;
                if (maskView != null) {//if maskView not null,witch means switch page in activity.
                    maskView.setBackgroundColor(Color.TRANSPARENT);
                    maskView.setVisibility(View.GONE);
                    slidingPaneLayout.setTouchAble(true);
                }
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

    public void setLocalSwipeAble(boolean localSwipeAble) {
        slidingPaneLayout.setLocalTouchAble(localSwipeAble);
    }
}
