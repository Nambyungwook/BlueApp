package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nbw.blueapp.R;
import com.nbw.blueapp.items.Sites;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.SitesListAdapter;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;

public class UserSavedSitesActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private String uid;

    TextView tv_error;
    ListView lv_user_saved_sites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_saved_sites);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        tv_error = (TextView) findViewById(R.id.tv_error_user_saved_sites);
        lv_user_saved_sites = (ListView) findViewById(R.id.lv_user_saved_sites);
    }

    @Override
    protected void onResume() {
        super.onResume();

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        getSitesToListview(uid);

        lv_user_saved_sites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Sites site = (Sites) parent.getItemAtPosition(position);

                String categoryB = site.getCategoryB();
                String categoryM = site.getCategoryM();
                String categoryS = site.getCategoryS();
                String siteName = site.getSiteName();
                String siteUrl = site.getSiteUrl();
                String siteDetail = site.getSiteDetail();

                Intent intent = new Intent(UserSavedSitesActivity.this, UserSavedSiteDetailActivity.class);
                intent.putExtra("categoryB", categoryB);
                intent.putExtra("categoryM", categoryM);
                intent.putExtra("categoryS", categoryS);
                intent.putExtra("siteName", siteName);
                intent.putExtra("siteUrl", siteUrl);
                intent.putExtra("siteDetail", siteDetail);
                startActivity(intent);
            }
        });
    }

    //저장한 사이트 목록 보여주기
    private void getSitesToListview(String uid) {
        final SitesListAdapter sitesListAdapter = new SitesListAdapter();

        ServerApi.getUserSavedSites(uid, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(UserSavedSitesActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("response_code").equals("SUCCESS")) {
                        Utils.toast(UserSavedSitesActivity.this, "사용자 정보를 불러오는데 실패했습니다.");
                        return;
                    } else if (ret.get("sites").toString().equals("[]")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_error.setVisibility(View.VISIBLE);
                                lv_user_saved_sites.setVisibility(View.GONE);
                            }
                        });
                        Utils.toast(UserSavedSitesActivity.this, "해당 사이트가 없습니다.");
                        return;
                    } else {
                        Gson gson = new GsonBuilder().create();
                        JsonParser parser = new JsonParser();
                        JsonElement rootObject = parser.parse(ret.get("sites").toString());

                        Sites[] sites = gson.fromJson(rootObject, Sites[].class);

                        if(sites.length > 0){
                            for(Sites p : sites){
                                sitesListAdapter.addItem(p.getCategoryB(), p.getCategoryM(), p.getCategoryS(), p.getSiteName(), p.getSiteUrl(), p.getSiteDetail());
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_error.setVisibility(View.GONE);
                                lv_user_saved_sites.setVisibility(View.VISIBLE);
                                lv_user_saved_sites.setAdapter(sitesListAdapter);
                            }
                        });

                    }

                } catch (Exception e) {
                    Utils.toast(UserSavedSitesActivity.this, e + "");
                }
            }
        });

    }
}
