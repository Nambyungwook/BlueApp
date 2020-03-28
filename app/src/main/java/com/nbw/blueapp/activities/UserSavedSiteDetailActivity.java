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

public class UserSavedSiteDetailActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private String uid;
    private String siteName;
    private String siteUrl;
    private String siteDetail;

    TextView tv_site_name_user_saved;
    TextView tv_site_url_user_saved;
    TextView tv_site_detail_user_saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_saved_site_detail);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        tv_site_detail_user_saved = (TextView) findViewById(R.id.tv_site_detail_user_saved);
        tv_site_name_user_saved = (TextView) findViewById(R.id.tv_site_name_user_saved);
        tv_site_url_user_saved = (TextView) findViewById(R.id.tv_site_url_user_saved);

        Intent intent = getIntent();
        siteName = intent.getStringExtra("siteName");
        siteUrl = intent.getStringExtra("siteUrl");
        siteDetail = intent.getStringExtra("siteDetail");

        tv_site_name_user_saved.setText(siteName);
        tv_site_url_user_saved.setText(siteUrl);
        tv_site_detail_user_saved.setText(siteDetail);
    }

    public void onClick_move_site(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(siteUrl)); startActivity(intent);
    }

    public void onClick_delete_site(View view) {
        ServerApi.deleteUserSavedSites(uid, siteName, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(UserSavedSiteDetailActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("response_code").equals("SUCCESS")) {
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
}
