package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_1;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_2;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_3;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_4;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_5;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_6;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_7;
import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;

public class UserSavedSiteDetailActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private Long siteId;
    private String uid;
    private String targetMain;
    private String targetDetail;
    private String local;
    private String income;
    private String age;
    private String gender;
    private String siteName;
    private String siteUrl;
    private String siteDetail;

    TextView tv_targetMain_user;
    TextView tv_targetDetail_user;
    TextView tv_local_user;
    TextView tv_income_user;
    TextView tv_age_user;
    TextView tv_gender_user;
    TextView tv_site_name_sd_user;
    TextView tv_site_url_sd_user;
    TextView tv_site_detail_sd_user;

    ImageView iv_target_main_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_saved_site_detail);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        tv_targetMain_user = (TextView) findViewById(R.id.tv_site_target_main_user);
        tv_targetDetail_user = (TextView) findViewById(R.id.tv_site_target_detail_user);
        tv_local_user = (TextView) findViewById(R.id.tv_site_user_local);
        tv_income_user = (TextView) findViewById(R.id.tv_site_user_income);
        tv_age_user = (TextView) findViewById(R.id.tv_site_user_age);
        tv_gender_user = (TextView) findViewById(R.id.tv_site_user_gender);
        tv_site_detail_sd_user = (TextView) findViewById(R.id.tv_site_detail_user_sd);
        tv_site_name_sd_user = (TextView) findViewById(R.id.tv_site_name_user_sd);
        tv_site_url_sd_user = (TextView) findViewById(R.id.tv_site_url_user_sd);

        iv_target_main_user = (ImageView) findViewById(R.id.iv_target_main_user);

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

        tv_targetMain_user.setText(targetMain);
        tv_targetDetail_user.setText(targetDetail);
        tv_local_user.setText("지역 : "+local);
        tv_income_user.setText("연봉 : "+income);
        tv_age_user.setText("나이 : "+age);
        tv_gender_user.setText("성별 : "+gender);
        tv_site_name_sd_user.setText(siteName);
        tv_site_url_sd_user.setText(siteUrl);
        tv_site_detail_sd_user.setText(siteDetail);

        switch (targetMain) {
            case TARGET_MAIN_1://전체
                iv_target_main_user.setImageResource(R.drawable.total_ic);
                break;
            case TARGET_MAIN_2://취업지원
                iv_target_main_user.setImageResource(R.drawable.job_support_ic);
                break;
            case TARGET_MAIN_3://창업지원
                iv_target_main_user.setImageResource(R.drawable.startup_support_ic);
                break;
            case TARGET_MAIN_4://혜택
                iv_target_main_user.setImageResource(R.drawable.gift_ic);
                break;
            case TARGET_MAIN_5://공연
                iv_target_main_user.setImageResource(R.drawable.concert_ic);
                break;
            case TARGET_MAIN_6: //쇼핑
                iv_target_main_user.setImageResource(R.drawable.shopping_ic);
                break;
            case TARGET_MAIN_7://기타
                iv_target_main_user.setImageResource(R.drawable.etc_ic);
                break;
        }
    }

    public void onClick_move_site(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(siteUrl)); startActivity(intent);
    }

    public void onClick_delete_site(View view) {
        ServerApi.deleteUserSavedSites(uid, siteId, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(UserSavedSiteDetailActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("responseCode").equals("SUCCESS")) {
                        Utils.toast(UserSavedSiteDetailActivity.this, ret.getString("message"));
                        return;
                    }

                    Utils.toast(UserSavedSiteDetailActivity.this, ret.getString("message"));

                    finish();

                } catch (Exception e) {
                    Utils.toast(UserSavedSiteDetailActivity.this, e + "");
                }
            }
        });
    }

    public void onClick_back(View view) {
        finish();
    }
}
