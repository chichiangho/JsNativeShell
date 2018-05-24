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


var app = {
    // Application Constructor
    initialize: function() {
    },

 startNext:function(){
             JsNativeInterface.loadUrl("{url:\"file:///android_asset/www/index.html\" ,titleInfo:\"{\\\"title\\\":\\\"PageIn\\\"}\",inCurPage:true}");
},
 startNextOut:function(){
 JsNativeInterface.loadUrl("{url:\"file:///android_asset/www/index.html\" ,titleInfo:\"{\\\"title\\\":\\\"PageOut\\\"}\"}");
},
loadBaidu:function(){
JsNativeInterface.loadUrl("{url:\"http://www.baidu.com/?wd=\\\"121\\\"\",titleInfo:\"{\\\"title\\\":\\\"Baidu\\\"}\"}");
  },
 setOnBackListener(){
JsNativeInterface.setOnBackListener         ("{onResultCallback:\"alert();\"}");
                   },
 setOnTitle:function(){
JsNativeInterface.setTitleBar("{titleCallback:\"alert(\\\"titleClick\\\");\"}");
   },
   setSearchTitle:function(){
JsNativeInterface.setTitleBar("{titleCallback:\"alert();\",hideTitleAndStatus:false,titleType:\"search\",titlePosition:\"left\",leftText:\"hide\",leftIcon:\"hide\",titleColor:\"#ff0000\",titleBarColor:\"#770000\"}");
     },
  setTitle:function(){
JsNativeInterface.setTitleBar("{rightButtons:[],titleType:\"normal\",hideTitleAndStatus:false,title:\"SetTitleSuccess\",titlePosition:\"left\",leftText:\"hide\",leftIcon:\"hide\",titleColor:\"#ff0000\",titleBarColor:\"#bbccff\"}");
  },
  closeTitle:function(){
//        cordova.exec(function(winParam) {},
//                 function(error) {},
//                 "PageManagerPlugin",
//                 "closeTitleSpinner",
//                 [{"title":"SetTitleSuccess","titlePosition":"left","leftText":"hide","leftIcon":"hide","titleColor":"#ff0000","titleBarColor":"#bbccff"}]);
    },
   hideTitle:function(){
//          cordova.exec(function(winParam) {},
//                   function(error) {},
//                   "PageManagerPlugin",
//                   "setTitleBar",
//                    [{title:"SetTitleSuccess",hideTitleAndStatus:true}] );
      },
    setOperator:function(){
JsNativeInterface.setTitleBar("{rightButtons:[{img:\"\",text:\"Set\",callback:\"app.setTitle();\"},{img:\"\",text:\"Get\",subMenu:[{text:\"333\",callback:\"alert(\\\"333\\\");\"},{text:\"444\",callback:\"alert(\\\"444\\\");\"}]}]}");
   },
     setSwipeAble:function(){
//                        cordova.exec(function(winParam) {},
//                                 function(error) {},
//                                 "PageManagerPlugin",
//                                 "setSwipeAble",
//                                 [true]);
                    },
                    setUnSwipeAble:function(){
//                          cordova.exec(function(winParam) {},
//                                   function(error) {},
//                                   "PageManagerPlugin",
//                                   "setSwipeAble",
//                                   [false]);
                      },
      goBack2:function(){
//                     cordova.exec(function(winParam) {},
//                              function(error) {},
//                              "PageManagerPlugin",
//                              "backHistory",
//                              [2]);
                 },
                  goBack3:function(){
//                                      cordova.exec(function(winParam) {},
//                                               function(error) {},
//                                               "PageManagerPlugin",
//                                               "backHistory",
//                                               [3]);
                                  }
};

app.initialize();