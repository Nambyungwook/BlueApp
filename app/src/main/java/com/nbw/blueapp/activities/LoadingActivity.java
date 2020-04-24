package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

public class LoadingActivity extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_loading;
    private TextView tv_error;
    private ProgressBar pb_loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        tv_title = (TextView) findViewById(R.id.tv_app_title);
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        tv_error = (TextView) findViewById(R.id.tv_error);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

        String htmlText = "<font color=\"#000000\">D</font>"+"<font color=\"#ffffff\">D</font>"+"IPS";

        tv_title.setText(Html.fromHtml(htmlText));

        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkServer();
            }
        }, 2000);
    }

    private void checkServer() {
        ServerApi.checkServer(new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        //Utils.toast(LoadingActivity.this, errMsg);
                        //ui가 변경되는 경우(단순 글자가 아닌 화면상에 그려지는 것이 변경되는 경우) 쓰레드 사용
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_error.setVisibility(View.VISIBLE);
                                tv_loading.setVisibility(View.GONE);
                                pb_loading.setVisibility(View.GONE);
                            }
                        });

                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("responseCode").equals("SUCCESS")) {
                        Utils.toast(LoadingActivity.this, ret.getString("message"));
                        //ui가 변경되는 경우(단순 글자가 아닌 화면상에 그려지는 것이 변경되는 경우) 쓰레드 사용
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_error.setVisibility(View.VISIBLE);
                                tv_loading.setVisibility(View.GONE);
                                pb_loading.setVisibility(View.GONE);
                            }
                        });
                        return;
                    }
                    Intent intent = new Intent(LoadingActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();


                } catch (Exception e) {
                    Utils.toast(LoadingActivity.this, e + "");
                }
            }
        });
    }
}
