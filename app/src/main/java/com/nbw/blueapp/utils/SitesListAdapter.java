package com.nbw.blueapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nbw.blueapp.R;
import com.nbw.blueapp.items.Sites;

import java.util.ArrayList;

import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_1;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_2;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_3;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_4;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_5;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_6;
import static com.nbw.blueapp.GlobalApplication.TARGET_MAIN_7;

public class SitesListAdapter extends BaseAdapter {

    private ArrayList<Sites> sitesArrayList = new ArrayList<>();

    @Override
    public int getCount() {
        return sitesArrayList.size();
    }

    @Override
    public Sites getItem(int i) {
        return sitesArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {



            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.site_item, parent, false);
        }

        ImageView iv_target_main = (ImageView) convertView.findViewById(R.id.iv_target_main);

        TextView tv_stie_name = (TextView) convertView.findViewById(R.id.tv_site_name);
        TextView tv_site_detail = (TextView) convertView.findViewById(R.id.tv_site_detail);
        TextView tv_target_main = (TextView) convertView.findViewById(R.id.tv_target_main);
        TextView tv_target_detail = (TextView) convertView.findViewById(R.id.tv_target_detail);

        Sites sites = getItem(position);

        tv_stie_name.setText(sites.getSiteName());
        tv_site_detail.setText(sites.getSiteDetail().substring(0,20)+"....");
        tv_target_main.setText(sites.getTargetMain());
        tv_target_detail.setText(sites.getTargetDetail());

        switch (sites.getTargetMain()) {
            case TARGET_MAIN_1://전체
                iv_target_main.setImageResource(R.drawable.total_ic);
                break;
            case TARGET_MAIN_2://취업지원
                iv_target_main.setImageResource(R.drawable.job_support_ic);
                break;
            case TARGET_MAIN_3://창업지원
                iv_target_main.setImageResource(R.drawable.startup_support_ic);
                break;
            case TARGET_MAIN_4://혜택
                iv_target_main.setImageResource(R.drawable.gift_ic);
                break;
            case TARGET_MAIN_5://공연
                iv_target_main.setImageResource(R.drawable.concert_ic);
                break;
            case TARGET_MAIN_6: //쇼핑
                iv_target_main.setImageResource(R.drawable.shopping_ic);
                break;
            case TARGET_MAIN_7://기타
                iv_target_main.setImageResource(R.drawable.etc_ic);
                break;
        }


        return convertView;
    }

    /*
    private Long id;
    private String targetMain;
    private String targetDetail;
    private String local;
    private String income;
    private String age;
    private String gender;
    private String siteName;
    private String siteUrl;
    private String siteDetail;
    */
    public void addItem(Long id,
                        String targetMain,
                        String targetDetail,
                        String local,
                        String income,
                        String age,
                        String gender,
                        String siteName,
                        String siteUrl,
                        String siteDetail) {

        Sites sites = new Sites();

        sites.setId(id);
        sites.setTargetMain(targetMain);
        sites.setTargetDetail(targetDetail);
        sites.setLocal(local);
        sites.setIncome(income);
        sites.setAge(age);
        sites.setGender(gender);
        sites.setSiteName(siteName);
        sites.setSiteUrl(siteUrl);
        sites.setSiteDetail(siteDetail);

        sitesArrayList.add(sites);

    }
}
