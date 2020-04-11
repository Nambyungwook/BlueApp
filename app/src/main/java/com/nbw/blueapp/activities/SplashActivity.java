package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.nbw.blueapp.GlobalApplication;
import com.nbw.blueapp.R;
import com.nbw.blueapp.activities.dialogs.TermsAgreeActivity;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;
import static com.nbw.blueapp.utils.Utils.StringToSHA1;

public class SplashActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN = 10;

    private GoogleSignInClient mGoogleSignInClient;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private long mLastClickTime = 0;//버튼 중복 클릭 방지용 변수

    private EditText et_id;
    private EditText et_pwd;

    private String id;
    private String pwd;
    private String uid;

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

        //google 로그인 인증
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
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

    public void onClickGoogleSignup(View view) {

        //중복 클릭 방지를 위해 클릭리스너 안에 시간초를 재서 확인
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        //구글 계정 연동

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        //구글 인증
        if (requestCode==GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                gmail = account.getEmail();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
                Utils.toast(SplashActivity.this, "이메일정보가 올바르지 않습니다. 다시 시도해주세요.");
                return;
            }

            getUserInfo_email(gmail, "G");
        }
    }

    //서버 디비에 같은 이메일이 존재하는지 확인 - 구글(카카오,네이버) 로그인이 아닌데 구글(카카오,네이버) 메일이 있을수 있음
    private void getUserInfo_email(final String email, final String signType) {
        ServerApi.getUserInfo_email(email, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(SplashActivity.this, errMsg);
                        return;
                    }
                    //같은 이메일이 존재하지 않음
                    if (!ret.getString("responseCode").equals("SUCCESS")) {
                        Utils.toast(SplashActivity.this, "같은 이메일이 존재하지 않음");

                        if (signType.equals("G")) {
                            Intent intent = new Intent(SplashActivity.this, TermsAgreeActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("signType", "G");
                            startActivity(intent);
                        } else if (signType.equals("K")) {
                            Intent intent = new Intent(SplashActivity.this, TermsAgreeActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("signType", "K");
                            startActivity(intent);
                        } else if (signType.equals("N")) {
                            Intent intent = new Intent(SplashActivity.this, TermsAgreeActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("signType", "N");
                            startActivity(intent);
                        }

                        return;
                    } else {
                        //같은 이메일이 존재
                        //디비에서 불러온 값
                        String getUserSignType = ret.getString("signType");
                        //불러온 값과 입력한 값이 같으면 로그인 아니면 에러창 표시
                        if (signType.equals(getUserSignType)) {
                            if (getUserSignType.equals("G")) {
                                //Google
                                String pwd_SHA1 = Utils.StringToSHA1("Google_nbw");
                                signin(gmail,pwd_SHA1);
                            } else if (getUserSignType.equals("K")) {
                                //Kakao
                                String pwd_SHA1 = Utils.StringToSHA1("Kakao_nbw");
                                signin(email_kakao,pwd_SHA1);
                            } else if (getUserSignType.equals("N")) {
                                //Naver
                                String pwd_SHA1 = Utils.StringToSHA1("Naver_nbw");
                                signin(email_naver,pwd_SHA1);
                            }

                            return;
                        } else {
                            //에러창 표시
                            Utils.toast(SplashActivity.this, "같은 이메일로 이미 가입되어있습니다. 이메일로 로그인해주세요.");
                            return;
                        }
                    }

                } catch (Exception e) {
                    Utils.toast(SplashActivity.this, e + "");
                }
            }
        });
    }
}
