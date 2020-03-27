package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.nbw.blueapp.GlobalApplication;
import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.utils.Utils.StringToSHA1;

public class SignupCompleteActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private String id;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_complete);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        id = intent.getStringExtra("email");
        pwd = intent.getStringExtra("pwd");

    }

    public void onClick_ok(View view) {

        String pwd_sha1 = StringToSHA1(pwd);

        signin(id, pwd_sha1);

    }

    private void signin(String id, String pwd) {
        try {
            //위에서 받아온 값들을 GlobalApplication의 static 변수에 저장하여 앱 어디서든 불러서 사용할수있도록 설정
            GlobalApplication.id = id;
            GlobalApplication.pwd = pwd;

            //서버에 회원가입 정보 전달
            JSONObject json = new JSONObject();
            json.put("email", id);
            json.put("pwd", pwd);

            //회원가입 api 호출
            ServerApi.signinPost(json, new PostCallBack() {
                @Override
                public void onResponse(JSONObject ret, String errMsg) {
                    try {
                        //api호출 실패로 서버에서 에러가 나는지 확인
                        if (errMsg != null) {
                            Utils.toast(SignupCompleteActivity.this, errMsg);
                            return;
                        }
                        //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                        if (!ret.getString("response_code").equals("SUCCESS")) {
                            Utils.toast(SignupCompleteActivity.this, ret.getString("message"));
                            return;
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("uid", ret.getString("uid"));
                        editor.commit();

                        // 로그인이 잘 끝났으니 MainActivity로 화면을 바꿔주고 종료
                        Intent intent = new Intent(SignupCompleteActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Utils.toast(SignupCompleteActivity.this,e+"");
                    }
                }
            });
        } catch (Exception e) {

        }
    }
}
