package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.nbw.blueapp.GlobalApplication;
import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;
import static com.nbw.blueapp.utils.Utils.StringToSHA1;

public class SplashActivity extends AppCompatActivity {

    private EditText et_id;
    private EditText et_pwd;

    private String id;
    private String pwd;
    private String uid;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        et_id = (EditText) findViewById(R.id.et_id);
        et_pwd = (EditText) findViewById(R.id.et_pwd);

        //비밀번호 안보이게 설정
        et_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        if (!uid.equals(USER_SIGNOUT)) {
            checkSignStatus(uid);
        }

    }

    public void onClick_signup(View view) {
        Intent intent = new Intent(SplashActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick_signin(View view) {

        String regEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        id = et_id.getText().toString();
        pwd = et_pwd.getText().toString();

        String pwd_sha1 = StringToSHA1(pwd);

        if (!id.matches(regEmail)) {
            Utils.toast(SplashActivity.this, "이메일 형식을 올바르게 입력해주세요.");
            return;
        }

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
                            Utils.toast(SplashActivity.this, errMsg);
                            return;
                        }
                        //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                        if (!ret.getString("responseCode").equals("SUCCESS")) {
                            Utils.toast(SplashActivity.this, ret.getString("message"));
                            return;
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("uid", ret.getString("uid"));
                        editor.commit();

                        // 로그인이 잘 끝났으니 MainActivity로 화면을 바꿔주고 종료
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Utils.toast(SplashActivity.this,e+"");
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    private void checkSignStatus(String uid) {
        ServerApi.getSignStatus(uid, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(SplashActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("sign_status").equals("true")) {
                        Utils.toast(SplashActivity.this, ret.getString("sign_status"));
                        return;
                    }

                    // 로그인이 되어있는 상태이므로 MainActivity로 화면을 바꿔주고 종료
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Utils.toast(SplashActivity.this,e+"");
                }
            }
        });
    }
}
