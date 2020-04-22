package com.nbw.blueapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nbw.blueapp.R;
import com.nbw.blueapp.items.Notices;

import java.util.ArrayList;

public class NoticeListAdapter extends BaseAdapter {

    private ArrayList<Notices> noticesArrayList = new ArrayList<>();

    @Override
    public int getCount() {
        return noticesArrayList.size();
    }

    @Override
    public Notices getItem(int i) {
        return noticesArrayList.get(i);
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
            convertView = inflater.inflate(R.layout.notice_item, parent, false);
        }


        TextView tv_notice_title = (TextView) convertView.findViewById(R.id.tv_notice_title);
        TextView tv_notice_date = (TextView)convertView.findViewById(R.id.tv_notice_date);
        TextView tv_notice_author = (TextView) convertView.findViewById(R.id.tv_notice_author);

        Notices notices = getItem(position);

        tv_notice_title.setText(notices.getTitle());
        tv_notice_author.setText("작성자 : " + notices.getAuthor());

        String modifiedDate = notices.getModifiedDate();//2020-04-20T112:23:01
        tv_notice_date.setText("날짜 : " + modifiedDate.substring(0, 10)+" "+modifiedDate.substring(11));

        return convertView;
    }

    public void addItem(String title,
                        String contents,
                        String author,
                        String createAt,
                        String updateAt) {

        Notices notices = new Notices();

        notices.setTitle(title);
        notices.setContents(contents);
        notices.setAuthor(author);
        notices.setCreatedDate(createAt);
        notices.setModifiedDate(updateAt);

        noticesArrayList.add(notices);

    }
}