package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    //체크 박스에서 사용할 값
    private int TERMS_AGREE_PRIVACY = 0;// No Check = 0, Check = 1
    private int TERMS_AGREE_USE = 0;     // No Check = 0, Check = 1
    private int TERMS_AGREE_ALL = 0;     // No Check = 0, Check = 1

    private boolean termsChecker = false;

    private EditText et_signup_id;
    private EditText et_signup_pwd;
    private EditText et_signup_pwd_confirm;

    private CheckBox cb_use;
    private CheckBox cb_privacy;
    private CheckBox cb_all;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_signup_id = (EditText) findViewById(R.id.et_signup_id);
        et_signup_pwd = (EditText) findViewById(R.id.et_signup_pwd);
        et_signup_pwd_confirm = (EditText) findViewById(R.id.et_signup_pwd_confirm);

        //비밀번호 안보이게 설정
        et_signup_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et_signup_pwd_confirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        cb_use = (CheckBox) findViewById(R.id.cb_use);
        cb_privacy = (CheckBox) findViewById(R.id.cb_privacy);
        cb_all = (CheckBox) findViewById(R.id.cb_all);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        //checkbox를 사용해서 확인
        cb_privacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    TERMS_AGREE_PRIVACY = 1;
                } else {
                    TERMS_AGREE_PRIVACY = 0;
                }
            }
        });

        cb_use.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    TERMS_AGREE_USE = 1;
                } else {
                    TERMS_AGREE_USE = 0;
                }
            }
        });

        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    cb_privacy.setChecked(true);
                    cb_use.setChecked(true);
                    TERMS_AGREE_ALL = 1;
                } else {
                    cb_privacy.setChecked(false);
                    cb_use.setChecked(false);
                    TERMS_AGREE_ALL = 0;
                }
            }
        });
    }

    public void onClick_signup(View view) {

        if (TERMS_AGREE_ALL != 1) {
            if (TERMS_AGREE_USE == 1) {
                if (TERMS_AGREE_PRIVACY == 1) {

                    //모두 동의가 된경우
                    termsChecker = true;
                } else {
                    Utils.toast(SignupActivity.this, "개인정보취급방침을 체크해주세요");
                    return;
                }
            } else {
                Utils.toast(SignupActivity.this, "이용약관을 체크해주세요");
            }
        } else {
            //모두 동의가 된경우
            termsChecker = true;
        }

        if (termsChecker!=true) {
            Utils.toast(SignupActivity.this, "앱을 사용하기 위해서는 모두 동의해야 합니다.");
            return;
        }

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
                json.put("signType", "M");//메일로 회원가입
                json.put("terms_agree", "Y");//이용약관, 개인정보처리방침 동의

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
                            intent.putExtra("email", id);
                            intent.putExtra("pwd", pwd);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Utils.toast(SignupActivity.this,e+"");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void onClick_use(View view) {
        //이용약관 보여주기
    }

    public void onClick_privacy(View view) {
        //개인정보처리방침 보여주기
    }
}
