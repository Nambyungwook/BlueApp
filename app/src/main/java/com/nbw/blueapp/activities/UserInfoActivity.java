package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.nbw.blueapp.R;
import com.nbw.blueapp.activities.dialogs.TermsAgreeActivity;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.NODATA_NUMBER;
import static com.nbw.blueapp.GlobalApplication.NODATA_STRING;
import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;

public class UserInfoActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private String uid;

    private String email;
    private String name;
    private String birthday;
    private String gender;
    private String local;
    private String job;
    private String interest;
    private int income;
    private String phone;

    TextView tv_email;
    TextView tv_name;
    TextView tv_birthday;
    TextView tv_gender;
    TextView tv_local;
    TextView tv_job;
    TextView tv_interest;
    TextView tv_income;
    TextView tv_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_local = (TextView) findViewById(R.id.tv_local);
        tv_job = (TextView) findViewById(R.id.tv_job);
        tv_interest = (TextView) findViewById(R.id.tv_interest);
        tv_income = (TextView) findViewById(R.id.tv_income);
        tv_phone = (TextView) findViewById(R.id.tv_phone);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);
    }

    public void onClick_back(View view) {
        finish();
    }

    public void onClick_updateUserInfo(View view) {
        Intent intent = new Intent(UserInfoActivity.this, UserInfoEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserInfo(uid);
    }

    private void getUserInfo(String uid) {
        final String check_uid = uid;
        ServerApi.getUserInfo(uid, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(UserInfoActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("uid").equals(check_uid)) {
                        Utils.toast(UserInfoActivity.this, "사용자 정보를 불러오는데 실패했습니다.");
                        return;
                    } else {
                        email = ret.getString("email");
                        if (ret.getString("name")=="null") {
                            name = NODATA_STRING;
                        } else {
                            name = ret.getString("name");
                        }
                        if (ret.getString("birthday")=="null") {
                            birthday = NODATA_STRING;
                        } else {
                            birthday = ret.getString("birthday");
                        }
                        if (ret.getString("gender")=="null") {
                            gender = NODATA_STRING;
                        } else {
                            gender = ret.getString("gender");
                        }
                        if (ret.getString("local")=="null") {
                            local = NODATA_STRING;
                        } else {
                            local = ret.getString("local");
                        }
                        if (ret.getString("interest")=="null") {
                            interest = NODATA_STRING;
                        } else {
                            interest = ret.getString("interest");
                        }
                        if (ret.getString("job")=="null") {
                            job = NODATA_STRING;
                        } else {
                            job = ret.getString("job");
                        }
                        if (ret.getString("income")=="null") {
                            income = NODATA_NUMBER;
                        } else {
                            income = ret.getInt("income");
                        }
                        if (ret.getString("phone")=="null") {
                            phone = NODATA_STRING;
                        } else {
                            phone = ret.getString("phone");
                        }
                        //ui가 변경되는 경우(단순 글자가 아닌 화면상에 그려지는 것이 변경되는 경우) 쓰레드 사용
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_phone.setText(phone);
                                tv_income.setText(income+"");
                                tv_interest.setText(interest);
                                tv_job.setText(job);
                                tv_local.setText(local);
                                tv_gender.setText(gender);
                                tv_birthday.setText(birthday);
                                tv_name.setText(name);
                                tv_email.setText(email);
                            }
                        });
                    }

                } catch (Exception e) {
                    Utils.toast(UserInfoActivity.this,e+"");
                }
            }
        });
    }

    public void onClick_updatePhoneNumber(View view) {
        Intent intent = new Intent(UserInfoActivity.this, UpdatePhoneNumberActivity.class);
        startActivity(intent);
    }

    public void onClick_use(View view) {
        //이용약관 보여주기
        Intent termsOfUseIntent = new Intent(UserInfoActivity.this, TermsOfUseActivity.class);
        startActivity(termsOfUseIntent);
    }

    public void onClick_privacy(View view) {
        //개인정보 약관 보여주기
        Intent termsOfPrivacyIntent = new Intent(UserInfoActivity.this, TermsOfPrivacyActivity.class);
        startActivity(termsOfPrivacyIntent);
    }
}
