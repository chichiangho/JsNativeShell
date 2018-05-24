let jsNativeEvent = {
    onLoadFinished(callBack) {
        this.finishCallBack = callBack;
        JsNativeInterface.setOnLoadStoppedListener("jsNativeEvent.performOnLoadFinished();");
    },
    performOnLoadFinished(params) {
        this.finishCallBack(params);
    },
    onLoadStarted(callBack) {
        this.startCallBack = callBack;
        JsNativeInterface.setOnLoadStartedListener("jsNativeEvent.performOnLoadStarted();");
    },
    performOnLoadStarted() {
        this.startCallBack();
    },
    onHistoryBack(callBack) {
        this.historyCallBack = callBack;
        JsNativeInterface.setOnBackListener("jsNativeEvent.performOnHistoryBack();");
    },
    performOnHistoryBack(params) {
        this.historyCallBack(params);
    }
};