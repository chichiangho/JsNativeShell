let jsNativeEvent = {
    onLoadFinished(callBack) {
        this.finishCallBack = callBack;
    },
    performOnLoadFinished(params) {
        this.finishCallBack(params);
    },
    onLoadStarted(callBack) {
        this.startCallBack = callBack;
    },
    performOnLoadStarted() {
        this.startCallBack();
    }
};