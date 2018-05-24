/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

let app = {
    // Application Constructor
    initialize: function () {
//        JsNativeInterface.setShowProgress(false);
        jsNativeEvent.onLoadFinished(function (params) {
             alert(params);
        });
        jsNativeEvent.onHistoryBack(function (params) {
            alert(params);
        })
    },
    startNext: function () {
        JsNativeInterface.loadUrl("{url:\"file:///android_asset/www/index.html\",params:\"in params\",titleInfo:\"{title:\\\"Load In WebView\\\"}\",inCurPage:true}");
    },
    startNextOut: function () {
        JsNativeInterface.loadUrl("{url:\"file:///android_asset/www/index.html\" ,params:\"out params\",titleInfo:\"{title:\\\"Load Out WebView\\\"}\"}");
    },
    loadBaidu: function () {
        JsNativeInterface.loadUrl("{url:\"http://www.baidu.com/?wd=\\\"121\\\"\",params:\"this is what I send to you!\",titleInfo:\"{title:\\\"Baidu\\\"}\"}");
    },
    setOnBackListener() {
        JsNativeInterface.setOnBackListener("{onResultCallback:\"alert();\"}");
    },
    setOnTitle: function () {
        JsNativeInterface.setTitleBar("{titleCallback:\"alert(\\\"titleClick\\\");\"}");
    },
    setSearchTitle: function () {
        JsNativeInterface.setTitleBar("{titleCallback:\"alert();\",hideTitleAndStatus:false,titleType:\"search\",titlePosition:\"left\",leftText:\"hide\",leftIcon:\"hide\",titleColor:\"#ff0000\",titleBarColor:\"#770000\"}");
    },
    setTitle: function () {
        JsNativeInterface.setTitleBar("{rightButtons:[],titleType:\"normal\",hideTitleAndStatus:false,title:\"SetTitleSuccess\",titlePosition:\"left\",leftText:\"hide\",leftIcon:\"hide\",titleColor:\"#ff0000\",titleBarColor:\"#bbccff\"}");
    },
    closeTitle: function () {
        JsNativeInterface.setTitleBar("{titleSpinnerOpen:false}");
    },
    hideTitle: function () {
        JsNativeInterface.setTitleBar("{hideTitleAndStatus:true}");
    },
    setOperator: function () {
        JsNativeInterface.setTitleBar("{rightButtons:[{img:\"\",text:\"Set\",subMenu:[{text:\"111\",callback:\"alert(\\\"111\\\");\"},{text:\"222\",callback:\"alert(\\\"222\\\");\"}],callback:\"app.setTitle();\"},{img:\"\",text:\"Get\",subMenu:[{text:\"333\",callback:\"alert(\\\"333\\\");\"},{text:\"444\",callback:\"alert(\\\"444\\\");\"}]}]}");
    },
    setSwipeAble: function () {
        JsNativeInterface.setSwipeAble(true);
    },
    setUnSwipeAble: function () {
        JsNativeInterface.setSwipeAble(false);
    },
    goBack2: function () {
        JsNativeInterface.backHistory("{backCount:2}");
    },
    goBack3: function () {
        JsNativeInterface.backHistory("{backCount:3}");
    },
    remove0: function () {
        JsNativeInterface.removeRightButtonByIndex("{index:0}");
    },
    change0: function () {
        JsNativeInterface.setRightButtonByIndex("{index:0,button:{text:\"change\"}}");
    },
    remove00: function () {
        JsNativeInterface.removeRightButtonByIndex("{index:0,subIndex:0}");
    },
    change00: function () {
        JsNativeInterface.setRightButtonByIndex("{index:0,subIndex:0,button:{text:\"change\"}}");
    }
};

app.initialize();