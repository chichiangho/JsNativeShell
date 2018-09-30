let missile = {
    onLoadFinished(callBack) {
        this.finishCallBack = callBack;
        Missile.execute("Core","setOnLoadStoppedListener","missile.performOnLoadFinished();");
    },
    performOnLoadFinished(params) {
        this.finishCallBack(params);
    },
    onLoadStarted(callBack) {
        this.startCallBack = callBack;
        Missile.execute("Core","setOnLoadStartedListener","missile.performOnLoadStarted();");
    },
    performOnLoadStarted() {
        this.startCallBack();
    },
    onResult(callBack) {
        this.resultCallback = callBack;
        Missile.execute("Core","setOnResultListener","missile.performOnResult();");
    },
    performOnResult(params) {
        this.resultCallback(params);
    },
    onTitle(callBack) {
        this.onTitleCallback = callBack;
        Missile.execute("Core","setTitleBar",JSON.stringify({titleCallback: "missile.performOnTitle();"}));
    },
    performOnTitle(params) {
        this.onTitleCallback(params);
    },
    showSearchTitle(callBack) {
        this.onSearchCallback = callBack;
        Missile.execute("Core","setTitleBar",JSON.stringify({
            titleType: "search",
            hideTitleAndStatus: false,
            titleCallback: "missile.performOnSearch();"
        }));
    },
    performOnSearch(params) {
        this.onSearchCallback(params);
    },

    loadUrl(param) {
        Missile.execute("Core","loadUrl",JSON.stringify(param));
    },
    setTitleBar(titleInfo) {
        Missile.execute("Core","setTitleBar",JSON.stringify(titleInfo));
    },
    backHistory(backInfo) {
        Missile.execute("Core","backHistory",JSON.stringify(backInfo));
    },
    removeRightButtonByIndex(buttonInfo) {
        Missile.execute("Core","removeRightButtonByIndex",JSON.stringify(buttonInfo));
    },
    setRightButtonByIndex(buttonInfo) {
        Missile.execute("Core","setRightButtonByIndex",JSON.stringify(buttonInfo));
    },
    addRightButton(buttonInfo) {
        Missile.execute("Core","addRightButton",JSON.stringify(buttonInfo));
    },
    setSwipeCloseAble(enable) {
        Missile.execute("Core","setSwipeAble",enable);
    },
};