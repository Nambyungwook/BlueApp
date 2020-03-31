package com.nbw.blueapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nbw.blueapp.R;
import com.nbw.blueapp.items.Sites;

import java.util.ArrayList;

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

        TextView tv_stie_name = (TextView) convertView.findViewById(R.id.tv_site_name);
        TextView tv_site_url = (TextView) convertView.findViewById(R.id.tv_site_url);

        Sites sites = getItem(position);

        tv_stie_name.setText(sites.getSiteName());
        tv_site_url.setText(sites.getSiteUrl());


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
