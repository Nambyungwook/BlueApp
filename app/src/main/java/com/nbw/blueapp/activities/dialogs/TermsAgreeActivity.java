package com.nbw.blueapp.activities.dialogs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.nbw.blueapp.GlobalApplication;
import com.nbw.blueapp.R;
import com.nbw.blueapp.activities.MainActivity;
import com.nbw.blueapp.activities.SignupCompleteActivity;
import com.nbw.blueapp.activities.SplashActivity;
import com.nbw.blueapp.activities.TermsOfPrivacyActivity;
import com.nbw.blueapp.activities.TermsOfUseActivity;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;
import static com.nbw.blueapp.utils.Utils.StringToSHA1;

public class TermsAgreeActivity extends AppCompatActivity {

    private String uid;
    private String signType;

    //체크 박스에서 사용할 값
    private int TERMS_AGREE_PRIVACY = 0;// No Check = 0, Check = 1
    private int TERMS_AGREE_USE = 0;     // No Check = 0, Check = 1
    private int TERMS_AGREE_ALL = 0;     // No Check = 0, Check = 1

    private boolean termsChecker = false;

    private CheckBox cb_use;
    private CheckBox cb_privacy;
    private CheckBox cb_all;

    //구글인증
    private String gmail;

    //카카오인증
    private String email_kakao;

    //네이버인증
    private String email_naver;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //상태 바 제거(전체화면 모드)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_terms_agree);
        //외부터치시 종료 안되게 설정
        this.setFinishOnTouchOutside(false);

        cb_use = (CheckBox) findViewById(R.id.cb_use);
        cb_privacy = (CheckBox) findViewById(R.id.cb_privacy);
        cb_all = (CheckBox) findViewById(R.id.cb_all);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        signType = intent.getStringExtra("signType");

        switch (signType) {
            case "G":
                //구글
                gmail = intent.getStringExtra("email");
                break;
            case "K":
                //kakao
                email_kakao = intent.getStringExtra("email");
                break;
            case "N":
                //naver
                email_naver = intent.getStringExtra("email");
                break;
        }

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

    @Override
    public void onBackPressed() {
        return;
    }

    public void onClick_use(View view) {
        //이용약관 보여주기
        Intent termsOfUseIntent = new Intent(TermsAgreeActivity.this, TermsOfUseActivity.class);
        startActivity(termsOfUseIntent);

    }

    public void onClick_privacy(View view) {
        //개인정보 약관 보여주기
        Intent termsOfPrivacyIntent = new Intent(TermsAgreeActivity.this, TermsOfPrivacyActivity.class);
        startActivity(termsOfPrivacyIntent);
    }

    public void onClick_signup(View view) {

        if (TERMS_AGREE_ALL != 1) {
            if (TERMS_AGREE_USE == 1) {
                if (TERMS_AGREE_PRIVACY == 1) {

                    //모두 동의가 된경우
                    termsChecker = true;
                } else {
                    Utils.toast(TermsAgreeActivity.this, "개인정보취급방침을 체크해주세요");
                    return;
                }
            } else {
                Utils.toast(TermsAgreeActivity.this, "이용약관을 체크해주세요");
            }
        } else {
            //모두 동의가 된경우
            termsChecker = true;
        }

        if (termsChecker!=true) {
            Utils.toast(TermsAgreeActivity.this, "앱을 사용하기 위해서는 모두 동의해야 합니다.");
            return;
        }

        switch (signType) {
            case "G":
                //구글
                String pwdG = "Google_nbw";
                signup(gmail, pwdG, signType);
                break;
            case "K":
                //kakao
                String pwdK = "Kakao_nbw";
                signup(email_kakao,pwdK,signType);
                break;
            case "N":
                //naver
                String pwdN = "Naver_nbw";
                signup(email_naver,pwdN,signType);
                break;
        }


    }

    private void signup(final String id, final String pwd, String signType) {
        String pwd_sha1 = StringToSHA1(pwd);

        try {
            //서버에 회원가입 정보 전달
            JSONObject json = new JSONObject();
            json.put("email", id);
            json.put("pwd", pwd_sha1);
            json.put("signType", signType);//메일로 회원가입
            json.put("terms_agree", "Y");//이용약관, 개인정보처리방침 동의

            //회원가입 api 호출
            ServerApi.signupPost(json, new PostCallBack() {
                @Override
                public void onResponse(JSONObject ret, String errMsg) {
                    try {
                        //api호출 실패로 서버에서 에러가 나는지 확인
                        if (errMsg != null) {
                            Utils.toast(TermsAgreeActivity.this, errMsg);
                            return;
                        }
                        //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                        if (!ret.getString("responseCode").equals("SUCCESS")) {
                            Utils.toast(TermsAgreeActivity.this, "이미 존재하는 이메일이거나 회원가입에 문제가 생겼습니다. 다시 시도해주세요.ㅠ");
                            return;
                        }

                        uid = ret.getString("uid");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("uid", uid);
                        editor.commit();

                        // 로그인이 잘 끝났으니 SignupComplete로 화면을 바꿔주고 종료
                        Intent intent = new Intent(TermsAgreeActivity.this, SignupCompleteActivity.class);
                        intent.putExtra("email", id);
                        intent.putExtra("pwd", pwd);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Utils.toast(TermsAgreeActivity.this,e+"");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
