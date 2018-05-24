package com.chichaingho.routor.jsnativeshell.entity;

import java.util.List;

public class TitleBarInfo {
    public Boolean hideTitle;    //是否显示标题，false.为false时仍有状态栏
    public Boolean hideTitleAndStatus;//隐藏标题和状态栏透明
    public Boolean darkStatusBarText;//是否深色状态栏文字，默认否，如果没有设置，将根据titleBarColor自适应
    public String titleBarColor;    //标题栏颜色，包括状态栏
    public String titleType;        //标题类型，normal，spinner，search，默认normal

    //normal，spinner
    public String title;             //标题
    public Integer titleTextSize;    //标题字体大小
    public String titleTextColor;    //标题字体颜色，默认白色
    public String titlePosition;    //标题位置，left 或middle，默认middle

    //spinner
    public String titleSpinnerImg;   //spinner标题（在normal标题基础下，标题右侧有一个乡下的箭头）箭头图片资源
    public Boolean titleSpinnerOpen; //打开或关闭spinner标题的箭头，默认关闭

    //search
    public String searchBtnImg;    //search标题搜索按钮的图片资源
    public String searchBg;        //search标题搜索框的背景图片资源
    public String searchHint;       //搜索提示问题
    public String searchHintColor;      //提示文字颜色
    public Integer searchTextSize;   //搜索框输入文字大小
    public String searchTextColor;  //搜索框输入文字颜色

    public String titleCallback; //标题回调，搜索框标题将带参数

    //left button
    public String leftIcon;    //左侧按钮，默认为返回，此处可以使网络图片或很低图片，如果为”hide”将隐藏
    public String leftText;    //左侧返回按钮文字,默认没有，如果titlePosition为left，将不生效
    public Integer leftTextSize;    //左侧返回按钮文字大小
    public String leftTextColor;    //左侧返回按钮文字颜色，默认白色
    public String leftCallback;     //左侧返回按钮回调，默认关闭功能

    //right buttons
    public List<RightButtonInfo> rightButtons;


    public void change(TitleBarInfo titleBarInfoNew) {
        if (titleBarInfoNew.hideTitle != null)
            hideTitle = titleBarInfoNew.hideTitle;
        if (titleBarInfoNew.hideTitleAndStatus != null)
            hideTitleAndStatus = titleBarInfoNew.hideTitleAndStatus;
        if (titleBarInfoNew.darkStatusBarText != null)
            darkStatusBarText = titleBarInfoNew.darkStatusBarText;
        if (titleBarInfoNew.titleBarColor != null)
            titleBarColor = titleBarInfoNew.titleBarColor;

        if (titleBarInfoNew.titleType != null)
            titleType = titleBarInfoNew.titleType;

        if (titleBarInfoNew.title != null)
            title = titleBarInfoNew.title;
        if (titleBarInfoNew.titleTextColor != null)
            titleTextColor = titleBarInfoNew.titleTextColor;
        if (titleBarInfoNew.titleTextSize != null)
            titleTextSize = titleBarInfoNew.titleTextSize;
        if (titleBarInfoNew.titlePosition != null)
            titlePosition = titleBarInfoNew.titlePosition;

        if (titleBarInfoNew.titleSpinnerImg != null)
            titleSpinnerImg = titleBarInfoNew.titleSpinnerImg;
        if (titleBarInfoNew.titleSpinnerOpen != null)
            titleSpinnerOpen = titleBarInfoNew.titleSpinnerOpen;

        if (titleBarInfoNew.searchBtnImg != null)
            searchBtnImg = titleBarInfoNew.searchBtnImg;
        if (titleBarInfoNew.searchBg != null)
            searchBg = titleBarInfoNew.searchBg;
        if (titleBarInfoNew.searchHint != null)
            searchHint = titleBarInfoNew.searchHint;
        if (titleBarInfoNew.searchHintColor != null)
            searchHintColor = titleBarInfoNew.searchHintColor;
        if (titleBarInfoNew.searchTextColor != null)
            searchTextColor = titleBarInfoNew.searchTextColor;
        if (titleBarInfoNew.searchTextSize != null)
            searchTextSize = titleBarInfoNew.searchTextSize;

        if (titleBarInfoNew.titleCallback != null)
            titleCallback = titleBarInfoNew.titleCallback;

        if (titleBarInfoNew.leftText != null)
            leftText = titleBarInfoNew.leftText;
        if (titleBarInfoNew.leftTextColor != null)
            leftTextColor = titleBarInfoNew.leftTextColor;
        if (titleBarInfoNew.leftTextSize != null)
            leftTextSize = titleBarInfoNew.leftTextSize;
        if (titleBarInfoNew.leftCallback != null)
            leftCallback = titleBarInfoNew.leftCallback;

        if (titleBarInfoNew.rightButtons != null)
            rightButtons = titleBarInfoNew.rightButtons;
    }

    public class RightButtonInfo {
        public String img;
        public String text;
        public String textColor;
        public Integer textSize;
        public String callback;
        public List<SubRightButtonInfo> subMenu;
    }

    public class SubRightButtonInfo {
        public String img;
        public String text;
        public String textColor;
        public Integer textSize;
        public String callback;
    }
}
