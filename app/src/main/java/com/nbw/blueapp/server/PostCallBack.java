package com.nbw.blueapp.server;

import org.json.JSONObject;

// 인터페이스 : 함수를 파라미터처럼 쓸 수 있도록
public interface PostCallBack {
    void onResponse(JSONObject ret, String errMsg);
}