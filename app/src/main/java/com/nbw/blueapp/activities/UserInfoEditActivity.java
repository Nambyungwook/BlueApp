package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.NODATA_NUMBER;
import static com.nbw.blueapp.GlobalApplication.NODATA_STRING;
import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;

public class UserInfoEditActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private String uid;
    private String email;
    private String pwd;

    private String name;
    private String birthday;
    private String gender;
    private String local;
    private String job;
    private String interest;
    private int income;
    private String phone;

    EditText et_name;
    EditText et_birthday;
    EditText et_job;
    EditText et_interest;
    EditText et_income;
    EditText et_phone;

    private Spinner spinnerLocal;
    private Spinner spinnerGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);

        et_birthday = (EditText) findViewById(R.id.et_birthday);
        et_interest = (EditText) findViewById(R.id.et_interest);
        et_job = (EditText) findViewById(R.id.et_job);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_income = (EditText) findViewById(R.id.et_income);

        spinnerGender = (Spinner) findViewById(R.id.spinner_gender_user_info_edit);
        spinnerLocal = (Spinner) findViewById(R.id.spinner_local_user_info_edit);


        spinnerLocal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //지역 스피너에서 선택된 내용을 저장
                local = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //성별 스피너에서 선택된 내용을 저장
                gender = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserInfo(uid);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void onClick_update(View view) {
        updateUserInfo(uid);

        finish();
    }

    public void onClick_back(View view) {
        finish();
    }

    //회원정보수정
    private void updateUserInfo(String uid) {

        final String check_uid = uid;

        name = et_name.getText().toString();
        birthday = et_birthday.getText().toString();
        job = et_job.getText().toString();
        income = Integer.parseInt(et_income.getText().toString());
        String raw_phone = et_phone.getText().toString();
        interest = et_interest.getText().toString();
        phone = raw_phone.substring(0,3) + "-" + raw_phone.substring(3,7) + "-" + raw_phone.substring(7);

        try {
            //서버에 회원가입 정보 전달
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("pwd", pwd);
            json.put("name", name);
            json.put("birthday", birthday);
            json.put("gender", gender);
            json.put("local", local);
            json.put("job", job);
            json.put("interest", interest);
            json.put("income", income);
            json.put("phone", phone);

            //회원가입 api 호출
            ServerApi.updateUserInfoPost(json, uid, new PostCallBack() {
                @Override
                public void onResponse(JSONObject ret, String errMsg) {
                    try {
                        //api호출 실패로 서버에서 에러가 나는지 확인
                        if (errMsg != null) {
                            Utils.toast(UserInfoEditActivity.this, errMsg);
                            return;
                        }
                        //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                        if (!ret.getString("uid").equals(check_uid)) {
                            Utils.toast(UserInfoEditActivity.this, "회원정보 수정에 문제가 있습니다. 다시 시도해주세요.");
                            return;
                        }

                        finish();
                    } catch (Exception e) {
                        Utils.toast(UserInfoEditActivity.this,e+"");
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    //회원정보조회
    private void getUserInfo(String uid) {
        final String check_uid = uid;
        ServerApi.getUserInfo(uid, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(UserInfoEditActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("uid").equals(check_uid)) {
                        Utils.toast(UserInfoEditActivity.this, "사용자 정보를 불러오는데 실패했습니다.");
                        return;
                    } else {
                        email = ret.getString("email");
                        pwd = ret.getString("pwd");
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
                                if (!phone.equals(NODATA_STRING)) {
                                    String raw_phone = phone.substring(0,3)+phone.substring(4,8)+phone.substring(9);
                                    et_phone.setText(raw_phone);
                                }
                                if (income!=NODATA_NUMBER) {
                                    et_income.setText(income+"");
                                }
                                if (!interest.equals(NODATA_STRING)) {
                                    et_interest.setText(interest);
                                }
                                if (!job.equals(NODATA_STRING)) {
                                    et_job.setText(job);
                                }
                                if (!birthday.equals(NODATA_STRING)) {
                                    et_birthday.setText(birthday);
                                }
                                if (!name.equals(NODATA_STRING)) {
                                    et_name.setText(name);
                                }
                            }
                        });
                    }

                } catch (Exception e) {
                    Utils.toast(UserInfoEditActivity.this,e+"");
                }
            }
        });
    }
}
