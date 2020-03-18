package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.nbw.blueapp.GlobalApplication;
import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private String uid;
    private String id;
    private String pwd;
    private String pwd_conrfirm;

    private EditText et_signup_id;
    private EditText et_signup_pwd;
    private EditText et_signup_pwd_confirm;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_signup_id = (EditText) findViewById(R.id.et_signup_id);
        et_signup_pwd = (EditText) findViewById(R.id.et_signup_pwd);
        et_signup_pwd_confirm = (EditText) findViewById(R.id.et_signup_pwd_confirm);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);
    }

    public void onClick_signup(View view) {

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        id = et_signup_id.getText().toString();
        pwd = et_signup_pwd.getText().toString();
        pwd_conrfirm = et_signup_pwd_confirm.getText().toString();

        if (!pwd.equals(pwd_conrfirm)) {

            Utils.toast(SignupActivity.this, "비밀번호가 일치하지 않습니다.");

            return;

        } else {
            try {
                //서버에 회원가입 정보 전달
                JSONObject json = new JSONObject();
                json.put("email", id);
                json.put("pwd", pwd);

                //회원가입 api 호출
                ServerApi.signupPost(json, new PostCallBack() {
                    @Override
                    public void onResponse(JSONObject ret, String errMsg) {
                        try {
                            //api호출 실패로 서버에서 에러가 나는지 확인
                            if (errMsg != null) {
                                Utils.toast(SignupActivity.this, errMsg);
                                return;
                            }
                            //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                            if (ret.getString("uid").equals(null)) {
                                Utils.toast(SignupActivity.this, "회원가입에 문제가 생겼습니다. 나중에 다시 시도해주세요.ㅠ");
                                return;
                            }

                            uid = ret.getString("uid");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uid", uid);
                            editor.commit();

                            // 로그인이 잘 끝났으니 MainActivity로 화면을 바꿔주고 종료
                            Intent intent = new Intent(SignupActivity.this, SignupCompleteActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Utils.toast(SignupActivity.this,e+"");
                        }
                    }
                });
            } catch (Exception e) {

            }
        }


    }
}
