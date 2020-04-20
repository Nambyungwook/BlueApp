package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nbw.blueapp.R;
import com.nbw.blueapp.items.Notices;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.NoticeListAdapter;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

public class NoticeActivity extends AppCompatActivity {

    ListView lv_notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        lv_notice = (ListView) findViewById(R.id.lv_notice);

        //공지사항 리스트뷰 리스너 구현----------------------------------------------------------------------------------------

        lv_notice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Notices notices = (Notices) parent.getItemAtPosition(position);

                String title = notices.getTitle();
                String contents = notices.getContents();
                String author = notices.getAuthor();

                AlertDialog.Builder builder = new AlertDialog.Builder(NoticeActivity.this);

                builder.setTitle(title)
                        .setMessage(contents + "\n\n작성자 : " + author)
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                return;
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기

            }
        });

        //공지사항 리스트뷰 리스너 구현 End----------------------------------------------------------------------------------------

    }

    @Override
    protected void onResume() {
        super.onResume();

        setNoticeListView();
    }

    public void onClick_back(View view) {
        finish();
    }

    private void setNoticeListView() {
        final NoticeListAdapter noticeListAdapter = new NoticeListAdapter();

        ServerApi.getNotice(new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(NoticeActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("responseCode").equals("SUCCESS")) {
                        Utils.toast(NoticeActivity.this, ret.getString("message"));
                        return;
                    } else {
                        Gson gson = new GsonBuilder().create();
                        JsonParser parser = new JsonParser();
                        JsonElement rootObject = parser.parse(ret.get("notices").toString());

                        Notices[] notices = gson.fromJson(rootObject, Notices[].class);

                        if(notices.length > 0){
                            for(Notices p : notices){
                                noticeListAdapter.addItem(p.getTitle(),
                                        p.getContents(),
                                        p.getAuthor(),
                                        p.getCreatedDate(),
                                        p.getModifiedDate());
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lv_notice.setAdapter(noticeListAdapter);
                            }
                        });
                    }

                } catch (Exception e) {
                    Utils.toast(NoticeActivity.this, e + "");
                }
            }
        });

    }
}
