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

    /* 카테고리1,2,3, 사이트명, url */
    public void addItem(String categoryB, String categoryM, String categoryS, String siteName, String siteUrl, String siteDetail) {

        Sites sites = new Sites();

        sites.setCategoryB(categoryB);
        sites.setCategoryM(categoryM);
        sites.setCategoryS(categoryS);
        sites.setSiteName(siteName);
        sites.setSiteUrl(siteUrl);
        sites.setSiteDetail(siteDetail);

        sitesArrayList.add(sites);

    }
}
