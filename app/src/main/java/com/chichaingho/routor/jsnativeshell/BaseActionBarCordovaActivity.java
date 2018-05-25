package com.chichaingho.routor.jsnativeshell;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chichaingho.routor.jsnativeshell.entity.ActionSetRightButtonByIndex;
import com.chichaingho.routor.jsnativeshell.entity.PageInfo;
import com.chichaingho.routor.jsnativeshell.entity.RightButtonIndex;
import com.chichaingho.routor.jsnativeshell.entity.TitleBarInfo;

import org.xwalk.core.XWalkActivityDelegate;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 处理原生标题栏加webView结构交互逻辑的类
 */
public abstract class BaseActionBarCordovaActivity extends AppCompatActivity {
    //标题类型
    private static final String TYPE_NORMAL = "normal";
    private static final String TYPE_SEARCH = "search";
    private static final String TYPE_WITH_SPINNER = "spinner";

    private static Timer progressTimer = new Timer();//使用一个静态的timer来处理进度条，因为并未持有引用，不会导致内存泄漏
    //页面栈
    protected Stack<PageInfo> pages = new Stack<>();
    private PopupWindow menu;
    protected View titleBar;
    protected View titleBarContent;
    private View titleNormalContainer;
    private ImageView titleSpinner;
    private TextView titleTv;
    private View titleSearchContainer;
    private TextView leftText;
    private ImageView leftIcon;
    private ProgressBar progress;
    private EditText titleEt;
    private ImageView titleEtBtn;
    protected XWalkActivityDelegate activityDelegate;
    protected XWalkView webView;
    private boolean showProgress = true;
    private float curProgress;
    private JsNativeInterface jsNativeInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_actionbar);
        initViews();

        //设置状态栏透明,通过标题栏模拟沉浸状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }

            int statusBarHeight = getStatusBarHeight();

            ViewGroup.LayoutParams actionBarParam = titleBar.getLayoutParams();
            actionBarParam.height += statusBarHeight;
            titleBar.setLayoutParams(actionBarParam);

            LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams) titleBarContent.getLayoutParams();
            contentParam.setMargins(0, statusBarHeight, 0, 0);
            titleBarContent.setLayoutParams(contentParam);
        }

        loadUrl(new PageInfo(getIntent().getStringExtra("url"),
                JsNativeInterface.gson.fromJson(getIntent().getStringExtra("titleBarInfo"), TitleBarInfo.class),
                getIntent().getStringExtra("params")));
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        progress = findViewById(R.id.title_bar_progress);
        titleBar = findViewById(R.id.title_bar);
        titleBarContent = findViewById(R.id.title_bar_content);
        titleNormalContainer = findViewById(R.id.title_bar_title_normal_container);
        titleSearchContainer = findViewById(R.id.title_bar_title_search_container);
        titleSpinner = findViewById(R.id.title_bar_title_spinner);
        titleTv = findViewById(R.id.title_bar_title);
        titleEt = findViewById(R.id.title_bar_title_search);
        titleEtBtn = findViewById(R.id.title_bar_title_search_btn);
        leftText = findViewById(R.id.left_text);
        leftIcon = findViewById(R.id.left_icon);

        webView.clearCache(false);
        jsNativeInterface = new JsNativeInterface(this);
        webView.addJavascriptInterface(jsNativeInterface, "JsNativeInterface");
        webView.setUIClient(new XWalkUIClient(webView) {
            @Override
            public void onPageLoadStarted(XWalkView view, String url) {
                super.onPageLoadStarted(view, url);
                jsNativeInterface.execJs(pages.lastElement().onLoadStartedCallback, pages.lastElement().params);

                if (showProgress) {
                    progress.setVisibility(View.VISIBLE);
                    progressTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!showProgress) {
                                curProgress = 0;
                                cancel();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setVisibility(View.GONE);
                                    }
                                });
                            }
                            curProgress += curProgress > 50 ? (curProgress > 75 ? 0.4 : 0.7) : 1;
                            if (curProgress >= 100) {
                                cancel();
                            }
                            if (Build.VERSION.SDK_INT >= 24)//setProgress 做了线程同步处理，不存在UI线程问题
                                progress.setProgress((int) curProgress, true);
                            else
                                progress.setProgress((int) curProgress);
                        }
                    }, 50, 50);
                }
            }

            @Override
            public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                super.onPageLoadStopped(view, url, status);
                jsNativeInterface.execJs(pages.lastElement().onLoadStoppedCallback, pages.lastElement().params);

                if (showProgress) {
                    progressTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            curProgress += 5;
                            if (!showProgress || curProgress >= 100) {
                                curProgress = 0;
                                cancel();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setVisibility(View.GONE);
                                    }
                                });
                            }
                            progress.setProgress((int) curProgress);
                        }
                    }, 10, 10);
                }
            }
        });
    }

    public boolean isXWalkReady() {
        return activityDelegate != null && activityDelegate.isXWalkReady();
    }

    public void loadUrl(final PageInfo info) {
        if (info.url == null)
            return;

        //替换url
//        try {
//            if (UrlParserUtil.shouldRepalceLoadUrl(info.url))
//                info.url = UrlParserUtil.ReplaceLoadUrl(info.url);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

        final String url = info.url;
        if (pages.size() > 0) {
            if (!isXWalkReady()) {
                activityDelegate = new XWalkActivityDelegate(this, null, new Runnable() {
                    @Override
                    public void run() {
                        webView.load(url, null);
                    }
                });
                activityDelegate.onResume();
            } else {
                webView.load(url, null);
            }
        } else {
            activityDelegate = new XWalkActivityDelegate(this, null, new Runnable() {
                @Override
                public void run() {
                    webView.load(url, null);
                }
            });
            activityDelegate.onResume();
        }
        pages.push(info);

        if (info.titleBarInfo == null)
            info.titleBarInfo = new TitleBarInfo();
        setTitleBar(info.titleBarInfo);
    }

    @Override
    public void onBackPressed() {
        handleBackClick();
    }

    protected void handleBackClick() {
        String callback = pages.lastElement().titleBarInfo.leftCallback;
        if (callback == null || callback.equals("")) {
            goBack(1, null);
        } else {
            jsNativeInterface.execJs(callback);
        }
    }

    public void goBack(final int backCount, final String backParams) {
        Intent intent = new Intent();
        intent.putExtra("backParams", backParams);
        if (pages.size() - backCount > 0) {
            intent.putExtra("backCount", backCount + 1);//因为是本activity内调用，故加1
            onActivityResult(0, 0, intent);
        } else {
            intent.putExtra("backCount", backCount - pages.size() + 1);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null && intent.getExtras() != null) {
            int backCount = intent.getIntExtra("backCount", 0);

            if (backCount > 1) {//中文习惯，从1开始
                backCount--;
                if (pages.size() - backCount > 0) {//activity内
                    while (backCount-- > 0) {
                        pages.pop();
                        webView.getNavigationHistory().canGoBack();
                    }
                    TitleBarInfo info = pages.lastElement().titleBarInfo;
                    info.titleSpinnerOpen = false;
                    setTitleBar(info);

                    jsNativeInterface.execJs(pages.lastElement().onResultCallback, intent.getStringExtra("backParams"));
                } else {
                    intent.putExtra("backCount", backCount + 1 - pages.size());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {//已回到了目标页
                jsNativeInterface.execJs(pages.lastElement().onResultCallback, intent.getStringExtra("backParams"));
            }
        }
    }

    /******************************* methods about title *************************************/
    public void setTitleBar(TitleBarInfo titleBarInfoNew) {
        if (titleBarInfoNew == null) {
            leftIcon.setVisibility(View.VISIBLE);
            leftText.setClickable(false);
            leftIcon.setImageResource(R.drawable.ic_title_back);
            leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleBackClick();
                }
            });
            return;
        }
        if (pages.lastElement().titleBarInfo == null)
            pages.lastElement().titleBarInfo = titleBarInfoNew;
        else
            pages.lastElement().titleBarInfo.change(titleBarInfoNew);
        final TitleBarInfo titleBarInfo = pages.lastElement().titleBarInfo;


        if (titleBarInfo.hideTitleAndStatus != null && titleBarInfo.hideTitleAndStatus) {
            titleBar.setVisibility(View.GONE);
            return;
        } else if (titleBarInfo.hideTitle != null && titleBarInfo.hideTitle) {
            titleBar.setVisibility(View.VISIBLE);
            titleBarContent.setVisibility(View.GONE);
            ViewGroup.LayoutParams actionBarParam = titleBar.getLayoutParams();
            actionBarParam.height = getStatusBarHeight();
            titleBar.setLayoutParams(actionBarParam);
            return;
        } else {
            titleBar.setVisibility(View.VISIBLE);
            titleBarContent.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams actionBarParam = titleBar.getLayoutParams();
            actionBarParam.height = (int) (getStatusBarHeight() + getResources().getDimension(R.dimen.actionbar_height));
            titleBar.setLayoutParams(actionBarParam);
        }

        if ((titleBarInfo.darkStatusBarText != null && titleBarInfo.darkStatusBarText || titleBarInfo.darkStatusBarText == null && titleBarInfo.titleBarColor != null && isLightEnough(titleBarInfo.titleBarColor)) && Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        if (titleBarInfo.titleBarColor != null) {
            titleBar.setBackgroundColor(Color.parseColor(titleBarInfo.titleBarColor));
        } else {
            titleBar.setBackgroundColor(getResources().getColor(R.color.actionbar_bg));
        }

        if (titleBarInfo.leftIcon != null) {
            switch (titleBarInfo.leftIcon) {
                case "hide":
                    leftIcon.setVisibility(View.GONE);
                    leftText.setGravity(Gravity.CENTER);
                    leftText.setClickable(true);
                    leftText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleBackClick();
                        }
                    });
                    break;
                default:
                    leftIcon.setVisibility(View.VISIBLE);
                    leftText.setClickable(false);
                    leftText.setGravity(Gravity.CENTER_VERTICAL);
                    Glide.with(BaseActionBarCordovaActivity.this).load(titleBarInfo.leftIcon).into(leftIcon);
                    leftIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleBackClick();
                        }
                    });
                    break;
            }
        } else {
            leftIcon.setVisibility(View.VISIBLE);
            leftText.setClickable(false);
            leftText.setGravity(Gravity.CENTER_VERTICAL);
            leftIcon.setImageResource(R.drawable.ic_title_back);
            leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleBackClick();
                }
            });
        }

        if (titleBarInfo.leftText != null) {
            leftText.setVisibility(View.VISIBLE);
            leftText.setText(titleBarInfo.leftText);
        } else {
            leftText.setVisibility(View.GONE);
            leftText.setClickable(false);
        }
        if (titleBarInfo.leftTextColor != null) {
            leftText.setTextColor(Color.parseColor(titleBarInfo.leftTextColor));
        } else {
            leftText.setTextColor(getResources().getColor(android.R.color.white));
        }
        if (titleBarInfo.leftTextSize != null && titleBarInfo.leftTextSize > 0) {
            leftText.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleBarInfo.leftTextSize);
        } else {
            leftText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        if (titleBarInfo.titleType == null)
            titleBarInfo.titleType = TYPE_NORMAL;
        switch (titleBarInfo.titleType) {
            case TYPE_SEARCH: {
                titleNormalContainer.setVisibility(View.GONE);
                titleSearchContainer.setVisibility(View.VISIBLE);
                titleEtBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jsNativeInterface.execJs(titleBarInfo.titleCallback, titleEt.getText().toString());
                    }
                });
                if (titleBarInfo.searchTextColor != null) {
                    titleEt.setTextColor(Color.parseColor(titleBarInfo.searchTextColor));
                } else {//not default,can not use needReset
                    titleEt.setTextColor(Color.GRAY);
                }
                if (titleBarInfo.searchTextSize != null && titleBarInfo.searchTextSize > 0) {
                    titleEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleBarInfo.searchTextSize);
                } else {
                    titleEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
                if (titleBarInfo.searchBtnImg != null) {
                    Glide.with(BaseActionBarCordovaActivity.this).load(titleBarInfo.searchBtnImg).into(titleEtBtn);
                } else {
                    titleEtBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_search));
                }
                if (titleBarInfo.searchBg != null) {
                    Glide.with(BaseActionBarCordovaActivity.this).load(titleBarInfo.searchBg).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                            titleEt.setBackground(resource);
                        }
                    });
                } else {
                    titleEt.setBackgroundColor(Color.parseColor("#dddddd"));
                }
                if (titleBarInfo.searchHintColor != null) {
                    titleEt.setHintTextColor(Color.parseColor(titleBarInfo.searchHintColor));
                } else {
                    titleEt.setHintTextColor(Color.GRAY);
                }
                if (titleBarInfo.searchHint != null) {
                    titleEt.setHint(titleBarInfo.searchHint);
                } else {
                    titleEt.setHint("");
                }
                break;
            }
            case TYPE_WITH_SPINNER:
            case TYPE_NORMAL: {
                titleNormalContainer.setVisibility(View.VISIBLE);
                titleSearchContainer.setVisibility(View.GONE);
                if (titleBarInfo.title != null) {
                    titleTv.setText(titleBarInfo.title);
                } else {
                    titleTv.setText("");
                }

                if (titleBarInfo.titleSpinnerImg != null)
                    Glide.with(titleSpinner).load(titleBarInfo.titleSpinnerImg).into(titleSpinner);
                else
                    titleSpinner.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_spinner));

                if (titleBarInfo.titleCallback == null || titleBarInfo.titleCallback.equals("") || titleBarInfo.titleType.equals(TYPE_NORMAL)) {
                    titleSpinner.setVisibility(View.GONE);
                } else {
                    titleSpinner.setVisibility(View.VISIBLE);
                }

                if (titleBarInfo.titleType.equals(TYPE_WITH_SPINNER))
                    changeSpinner(titleBarInfo.titleSpinnerOpen != null && titleBarInfo.titleSpinnerOpen);

                titleNormalContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jsNativeInterface.execJs(titleBarInfo.titleCallback);
                        if (titleBarInfo.titleType.equals(TYPE_WITH_SPINNER))
                            changeSpinner(!(titleBarInfo.titleSpinnerOpen != null && titleBarInfo.titleSpinnerOpen));
                    }
                });
                if ("left".equals(titleBarInfo.titlePosition)) {
                    leftText.setVisibility(View.GONE);
                    leftIcon.setVisibility(View.VISIBLE);
                    leftIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_title_back));
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.leftMargin = leftIcon.getMeasuredWidth() == 0 ? (int) getResources().getDimension(R.dimen.actionbar_height) : leftIcon.getMeasuredWidth();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    titleNormalContainer.setLayoutParams(lp);
                } else {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.leftMargin = 0;
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    if (titleBarInfo.leftText != null)
                        leftText.setVisibility(View.VISIBLE);
                    titleNormalContainer.setLayoutParams(lp);
                }
                if (titleBarInfo.titleTextColor != null) {
                    titleTv.setTextColor(Color.parseColor(titleBarInfo.titleTextColor));
                } else {
                    titleTv.setTextColor(Color.WHITE);
                }
                if (titleBarInfo.titleTextSize != null && titleBarInfo.titleTextSize > 0) {
                    titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleBarInfo.titleTextSize);
                } else {
                    titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
                break;
            }
            default:
                break;
        }

        if (titleBarInfo.rightButtons != null)
            setRightButtons(titleBarInfo.rightButtons);
    }

    protected void changeSpinner(boolean toOpen) {
        ValueAnimator animator;
        if (toOpen && titleSpinner.getRotation() == 0) {
            animator = ValueAnimator.ofInt(0, 180);
            pages.lastElement().titleBarInfo.titleSpinnerOpen = true;
        } else if (!toOpen && titleSpinner.getRotation() == 180) {
            animator = ValueAnimator.ofInt(180, 0);
            pages.lastElement().titleBarInfo.titleSpinnerOpen = false;
        } else {
            return;
        }
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                titleSpinner.setPivotX(titleSpinner.getWidth() / 2);
                titleSpinner.setPivotY(titleSpinner.getHeight() / 2);//支点在图片中心
                titleSpinner.setRotation((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    private boolean isLightEnough(String colorString) {
        if (colorString.charAt(0) == '#') {
            long r, g, b;
            if (colorString.length() == 7) {
                r = Long.parseLong(colorString.substring(1, 3), 16);
                g = Long.parseLong(colorString.substring(3, 5), 16);
                b = Long.parseLong(colorString.substring(5, 7), 16);
            } else if (colorString.length() == 9) {
                r = Long.parseLong(colorString.substring(3, 5), 16);
                g = Long.parseLong(colorString.substring(5, 7), 16);
                b = Long.parseLong(colorString.substring(7, 9), 16);
            } else {
                throw new IllegalArgumentException("Unknown color");
            }
            return r * 0.299 + g * 0.587 + b * 0.114 >= 192;
        }
        throw new IllegalArgumentException("Unknown color");
    }

    private void setRightButtons(final List<TitleBarInfo.RightButtonInfo> rightButtonInfos) {
        LinearLayout operatorLayout = findViewById(R.id.title_bar_operator);
        operatorLayout.removeAllViews();
        if (rightButtonInfos == null)
            return;
        int operatorSize = operatorLayout.getMeasuredHeight();
        for (int i = rightButtonInfos.size() - 1; i >= 0; i--) {//倒序排列
            final int index = i;
            final TitleBarInfo.RightButtonInfo operatorInfo = rightButtonInfos.get(i);

            final TextImageView operator = new TextImageView(BaseActionBarCordovaActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(operatorSize, operatorSize);
            operator.setLayoutParams(lp);
            operator.setGravity(Gravity.CENTER);
            operator.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            operator.setBackground(getResources().getDrawable(R.drawable.title_click_bg));
            operatorLayout.addView(operator);
            if (operatorInfo.img != null) {
                Glide.with(BaseActionBarCordovaActivity.this).load(operatorInfo.img).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                        operator.setImageDrawable(resource);
                    }
                });
            }
            if (operatorInfo.text != null) {
                operator.setText(operatorInfo.text);
            }
            if (operatorInfo.textColor != null) {
                operator.setTextColor(Color.parseColor(operatorInfo.textColor));
            } else {
                operator.setTextColor(getResources().getColor(android.R.color.white));
            }
            if (operatorInfo.textSize != null && operatorInfo.textSize > 0) {
                operator.setTextSize(TypedValue.COMPLEX_UNIT_SP, operatorInfo.textSize);
            } else {
                operator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            operator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operatorInfo.subMenu != null) {
                        showPopWindow(v, index, operatorInfo.subMenu);
                    } else {
                        jsNativeInterface.execJs(operatorInfo.callback);
                    }
                }
            });
        }
    }

    @SuppressLint("InflateParams")
    private void showPopWindow(View v, int parentIndex, List<TitleBarInfo.SubRightButtonInfo> operator) {
        RecyclerView recyclerView;
        if (menu == null) {
            menu = new PopupWindow(LayoutInflater.from(this).inflate(R.layout.layout_operator_pop, null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            if (Build.VERSION.SDK_INT >= 23) {
                menu.setEnterTransition(new Slide(Gravity.TOP));
                menu.setExitTransition(new Slide(Gravity.TOP));
            }
            recyclerView = (RecyclerView) menu.getContentView();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MenuAdapter(jsNativeInterface, menu, operator));
        } else {
            recyclerView = (RecyclerView) menu.getContentView();
            ((MenuAdapter) recyclerView.getAdapter()).setData(operator);
        }
        if (pages.lastElement().titleBarInfo.titleBarColor != null)
            recyclerView.setBackgroundColor(Color.parseColor(pages.lastElement().titleBarInfo.titleBarColor));
        else
            recyclerView.setBackgroundColor(getResources().getColor(R.color.actionbar_bg));
        recyclerView.setMinimumWidth(v.getWidth() * (1 + parentIndex));
        menu.showAsDropDown(v);
    }

    /******************************* methods about title end *************************************/
    private int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public void setRightButtonByIndex(ActionSetRightButtonByIndex action) {
        if (action.subIndex == null) {
            TitleBarInfo.RightButtonInfo cur = pages.lastElement().titleBarInfo.rightButtons.get(action.index);
            if (action.button.callback != null)
                cur.callback = action.button.callback;
            if (action.button.text != null)
                cur.text = action.button.text;
            if (action.button.textColor != null)
                cur.textColor = action.button.textColor;
            if (action.button.textSize != null)
                cur.textSize = action.button.textSize;
            if (action.button.img != null)
                cur.img = action.button.img;
            if (action.button.subMenu != null)
                cur.subMenu = action.button.subMenu;
        } else {
            TitleBarInfo.SubRightButtonInfo cur = pages.lastElement().titleBarInfo.rightButtons.get(action.index).subMenu.get(action.index);
            if (action.button.callback != null)
                cur.callback = action.button.callback;
            if (action.button.text != null)
                cur.text = action.button.text;
            if (action.button.textColor != null)
                cur.textColor = action.button.textColor;
            if (action.button.textSize != null)
                cur.textSize = action.button.textSize;
            if (action.button.img != null)
                cur.img = action.button.img;
        }
        setRightButtons(pages.lastElement().titleBarInfo.rightButtons);
    }

    public void removeRightButtonByIndex(RightButtonIndex action) {
        if (action.index == null)
            return;
        if (action.subIndex == null) {
            pages.lastElement().titleBarInfo.rightButtons.remove((int) action.index);
        } else {
            pages.lastElement().titleBarInfo.rightButtons.get(action.index).subMenu.remove((int) action.subIndex);
        }
        setRightButtons(pages.lastElement().titleBarInfo.rightButtons);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.resumeTimers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.onDestroy();
        }
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
        progress.setVisibility(showProgress ? View.VISIBLE : View.GONE);
    }

    static class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private JsNativeInterface jsNativeInterface;
        private PopupWindow menu;
        private List<TitleBarInfo.SubRightButtonInfo> subButtons;

        MenuAdapter(JsNativeInterface jsNativeInterface, PopupWindow menu, List<TitleBarInfo.SubRightButtonInfo> subButtons) {
            this.jsNativeInterface = jsNativeInterface;
            this.menu = menu;
            this.subButtons = subButtons;
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_item, null));
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            TitleBarInfo.SubRightButtonInfo sub = subButtons.get(position);
            if (sub.img != null) {
                ((Holder) holder).img.setVisibility(View.VISIBLE);
                Glide.with(((Holder) holder).img).load(sub.img).into(((Holder) holder).img);
            }
            if (sub.text != null) {
                ((Holder) holder).text.setVisibility(View.VISIBLE);
                ((Holder) holder).text.setText(sub.text);
                if (sub.textColor != null) {
                    ((Holder) holder).text.setTextColor(Color.parseColor(sub.textColor));
                } else {
                    ((Holder) holder).text.setTextColor(Color.WHITE);
                }
                if (sub.textSize != null) {
                    ((Holder) holder).text.setTextSize(TypedValue.COMPLEX_UNIT_SP, sub.textSize);
                } else {
                    ((Holder) holder).text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
            } else {
                ((Holder) holder).text.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (jsNativeInterface != null) {
                        jsNativeInterface.execJs(subButtons.get(holder.getAdapterPosition()).callback);
                    }
                    menu.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return subButtons.size();
        }

        void setData(List<TitleBarInfo.SubRightButtonInfo> operator) {
            this.subButtons = operator;
            notifyDataSetChanged();
        }

        class Holder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView text;

            Holder(View itemView) {
                super(itemView);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemView.setLayoutParams(lp);
                img = itemView.findViewById(R.id.img);
                text = itemView.findViewById(R.id.text);
            }
        }
    }
}
