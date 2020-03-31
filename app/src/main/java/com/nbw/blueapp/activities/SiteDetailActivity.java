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
    private Long siteId;
    private String targetMain;
    private String targetDetail;
    private String local;
    private String income;
    private String age;
    private String gender;
    private String siteName;
    private String siteUrl;
    private String siteDetail;

    TextView tv_targetMain;
    TextView tv_targetDetail;
    TextView tv_local;
    TextView tv_income;
    TextView tv_age;
    TextView tv_gender;
    TextView tv_site_name_sd;
    TextView tv_site_url_sd;
    TextView tv_site_detail_sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        tv_targetMain = (TextView) findViewById(R.id.tv_site_target_main);
        tv_targetDetail = (TextView) findViewById(R.id.tv_site_target_detail);
        tv_local = (TextView) findViewById(R.id.tv_site_local);
        tv_income = (TextView) findViewById(R.id.tv_site_income);
        tv_age = (TextView) findViewById(R.id.tv_site_age);
        tv_gender = (TextView) findViewById(R.id.tv_site_gender);
        tv_site_name_sd = (TextView) findViewById(R.id.tv_site_name_sd);
        tv_site_url_sd = (TextView) findViewById(R.id.tv_site_url_sd);
        tv_site_detail_sd = (TextView) findViewById(R.id.tv_site_detail_sd);

        Intent intent = getIntent();

        siteId = intent.getLongExtra("siteId", 0);
        targetMain = intent.getStringExtra("targetMain");
        targetDetail = intent.getStringExtra("targetDetail");
        local = intent.getStringExtra("local");
        income = intent.getStringExtra("income");
        age = intent.getStringExtra("age");
        gender = intent.getStringExtra("gender");
        siteName = intent.getStringExtra("siteName");
        siteUrl = intent.getStringExtra("siteUrl");
        siteDetail = intent.getStringExtra("siteDetail");

        tv_targetMain.setText(targetMain);
        tv_targetDetail.setText(targetDetail);
        tv_local.setText(local);
        tv_income.setText(income);
        tv_age.setText(age);
        tv_gender.setText(gender);
        tv_site_name_sd.setText(siteName);
        tv_site_url_sd.setText(siteUrl);
        tv_site_detail_sd.setText(siteDetail);
    }

    public void onClick_saveSite(View view) {

        try {
            //서버에 저장할 사이트 정보
            JSONObject json = new JSONObject();
            json.put("uid", uid);
            json.put("siteId", siteId);

            //사용자별 사이트 저장(즐겨찾기 추가)
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
                        if (!ret.getString("responseCode").equals("SUCCESS")) {
                            Utils.toast(SiteDetailActivity.this, "이미 저장한 사이트입니다.");
                            return;
                        }
                        //정상작동
                        Utils.toast(SiteDetailActivity.this, "사이트를 저장하였습니다.");

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
