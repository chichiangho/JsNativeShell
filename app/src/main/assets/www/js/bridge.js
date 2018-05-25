let jsNativeBridge = {
    onLoadFinished(callBack) {
        this.finishCallBack = callBack;
        JsNativeInterface.setOnLoadStoppedListener("jsNativeBridge.performOnLoadFinished();");
    },
    performOnLoadFinished(params) {
        this.finishCallBack(params);
    },
    onLoadStarted(callBack) {
        this.startCallBack = callBack;
        JsNativeInterface.setOnLoadStartedListener("jsNativeBridge.performOnLoadStarted();");
    },
    performOnLoadStarted() {
        this.startCallBack();
    },
    onResult(callBack) {
        this.resultCallback = callBack;
        JsNativeInterface.setOnResultListener("jsNativeBridge.performOnResult();");
    },
    performOnResult(params) {
        this.resultCallback(params);
    },
    onTitle(callBack) {
        this.onTitleCallback = callBack;
        JsNativeInterface.setTitleBar(JSON.stringify({titleCallback: "jsNativeBridge.performOnTitle();"}));
    },
    performOnTitle(params) {
        this.onTitleCallback(params);
    },
    showSearchTitle(callBack) {
        this.onSearchCallback = callBack;
        JsNativeInterface.setTitleBar(JSON.stringify({
            titleType: "search",
            hideTitleAndStatus: false,
            titleCallback: "jsNativeBridge.performOnSearch();"
        }));
    },
    performOnSearch(params) {
        this.onSearchCallback(params);
    },

    loadUrl(param) {
        JsNativeInterface.loadUrl(JSON.stringify(param));
    },
    setTitleBar(titleInfo) {
        JsNativeInterface.setTitleBar(JSON.stringify(titleInfo));
    },
    backHistory(backInfo) {
        JsNativeInterface.backHistory(JSON.stringify(backInfo));
    },
    removeRightButtonByIndex(buttonInfo) {
        JsNativeInterface.removeRightButtonByIndex(JSON.stringify(buttonInfo));
    },
    setRightButtonByIndex(buttonInfo) {
        JsNativeInterface.setRightButtonByIndex(JSON.stringify(buttonInfo));
    },
    addRightButton(buttonInfo) {
        JsNativeInterface.addRightButton(JSON.stringify(buttonInfo));
    },
    setSwipCloseAble(enable) {
        JsNativeInterface.setSwipeAble(enable);
    },
};