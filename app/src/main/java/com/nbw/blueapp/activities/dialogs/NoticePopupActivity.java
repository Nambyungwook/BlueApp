package com.nbw.blueapp.activities.dialogs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nbw.blueapp.R;
import com.nbw.blueapp.items.Notices;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

public class NoticePopupActivity extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_author;
    private TextView tv_update_time;
    private TextView tv_create_time;

    private CheckBox cb_today;

    private int todayChecker;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //상태 바 제거(전체화면 모드)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_notice_popup);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_author = (TextView) findViewById(R.id.tv_author);
        tv_update_time = (TextView) findViewById(R.id.tv_update_time);
        tv_create_time = (TextView) findViewById(R.id.tv_create_time);

        cb_today = (CheckBox) findViewById(R.id.cb_today);

        getLastNotice();

        cb_today.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //하루동안 안보기
                    todayChecker = 1;
                } else {
                    //계속 보기
                    todayChecker = 0;
                }
            }
        });
    }

    private void getLastNotice() {
        ServerApi.getNotice(new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(NoticePopupActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("responseCode").equals("SUCCESS")) {
                        Utils.toast(NoticePopupActivity.this, ret.getString("message"));
                        return;
                    } else {
                        Gson gson = new GsonBuilder().create();
                        JsonParser parser = new JsonParser();
                        JsonElement rootObject = parser.parse(ret.get("notices").toString());

                        Notices[] notices = gson.fromJson(rootObject, Notices[].class);

                        int lastIndex = notices.length-1;

                        final Notices lastNotice = notices[lastIndex];

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_title.setText(lastNotice.getTitle());
                                tv_content.setText(lastNotice.getContents());
                                tv_author.setText(lastNotice.getAuthor());

                                String createdDate = lastNotice.getCreatedDate();
                                String createdDate_ = createdDate.substring(0, 10)+" "+createdDate.substring(11);

                                String modifiedDate = lastNotice.getModifiedDate();
                                String modifiedDate_ = modifiedDate.substring(0, 10)+" "+modifiedDate.substring(11);

                                tv_create_time.setText(createdDate_);
                                tv_update_time.setText(modifiedDate_);
                            }
                        });
                    }

                } catch (Exception e) {
                    Utils.toast(NoticePopupActivity.this, e + "");
                }
            }
        });
    }

    public void onClick_ok(View view) {

        String today = Utils.getCurrentDate();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(today, todayChecker);
        editor.commit();

        finish();
    }
}
