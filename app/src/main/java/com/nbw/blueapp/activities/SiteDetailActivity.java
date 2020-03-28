package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;

public class SiteDetailActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private String uid;
    private String categoryB;
    private String categoryM;
    private String categoryS;
    private String siteName;
    private String siteUrl;
    private String siteDetail;

    TextView tv_category_b;
    TextView tv_category_m;
    TextView tv_category_s;
    TextView tv_site_name_sd;
    TextView tv_site_url_sd;
    TextView tv_site_detail_sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        tv_category_b = (TextView) findViewById(R.id.tv_site_category_b);
        tv_category_m = (TextView) findViewById(R.id.tv_site_category_m);
        tv_category_s = (TextView) findViewById(R.id.tv_site_category_s);
        tv_site_name_sd = (TextView) findViewById(R.id.tv_site_name_sd);
        tv_site_url_sd = (TextView) findViewById(R.id.tv_site_url_sd);
        tv_site_detail_sd = (TextView) findViewById(R.id.tv_site_detail_sd);

        Intent intent = getIntent();
        categoryB = intent.getStringExtra("categoryB");
        categoryM = intent.getStringExtra("categoryM");
        categoryS = intent.getStringExtra("categoryS");
        siteName = intent.getStringExtra("siteName");
        siteUrl = intent.getStringExtra("siteUrl");
        siteDetail = intent.getStringExtra("siteDetail");

        tv_category_b.setText(categoryB);
        tv_category_m.setText(categoryM);
        tv_category_s.setText(categoryS);
        tv_site_name_sd.setText(siteName);
        tv_site_url_sd.setText(siteUrl);
        tv_site_detail_sd.setText(siteDetail);
    }

    public void onClick_saveSite(View view) {

        try {
            //서버에 저장할 사이트 정보
            JSONObject json = new JSONObject();
            json.put("uid", uid);
            json.put("siteName", siteName);
            json.put("siteUrl", siteUrl);
            json.put("siteDetail", siteDetail);

            //회원가입 api 호출
            ServerApi.saveSites(json, new PostCallBack() {
                @Override
                public void onResponse(JSONObject ret, String errMsg) {
                    try {
                        //api호출 실패로 서버에서 에러가 나는지 확인
                        if (errMsg != null) {
                            Utils.toast(SiteDetailActivity.this, errMsg);
                            return;
                        }
                        //uid가 정상적으로 저장되었는지 확인
                        if (!ret.getString("uid").equals(uid)) {
                            Utils.toast(SiteDetailActivity.this, ret.getString("uid"));
                            return;
                        }
                        //정상작동
                        Utils.toast(SiteDetailActivity.this, ret.getString("uid"));

                    } catch (Exception e) {
                        Utils.toast(SiteDetailActivity.this,e+"");
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    public void onClick_move_site(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(siteUrl)); startActivity(intent);
    }
}
