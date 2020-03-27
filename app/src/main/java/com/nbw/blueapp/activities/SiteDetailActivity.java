package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nbw.blueapp.R;

public class SiteDetailActivity extends AppCompatActivity {

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
    }
}
