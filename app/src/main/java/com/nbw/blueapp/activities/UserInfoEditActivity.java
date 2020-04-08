package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
                if (gender.equals("선택안함")) {
                    gender = "X";
                }
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
    }

    public void onClick_back(View view) {
        finish();
    }

    //회원정보수정
    private void updateUserInfo(String uid) {
        //생년월일 형식
        String birthdayForm = "^([12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))$";

        name = et_name.getText().toString();
        birthday = et_birthday.getText().toString();
        job = et_job.getText().toString();
        String strIncome = et_income.getText().toString();
        interest = et_interest.getText().toString();

        if (name.equals("")|birthday.equals("")|job.equals("")|interest.equals("")|strIncome.equals("")) {
            Utils.toast(UserInfoEditActivity.this, "회원정보를 모두 입력해주세요.");
            return;
        } else if (!birthday.matches(birthdayForm)) {
            Utils.toast(this, "생년월일을 올바르게 입력해주세요.");
            return;
        }
        income = Integer.parseInt(strIncome);


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
                        if (!ret.getString("responseCode").equals("SUCCESS")) {
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSpinner(1);
                                }
                            });
                        }
                        if (ret.getString("local")=="null") {
                            local = NODATA_STRING;
                        } else {
                            local = ret.getString("local");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSpinner(0);
                                }
                            });
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

    //스피너 초기값 설정
    private void initSpinner(int index) {

        switch (index) {
            case 0:
                for (int i = 0; i < 13; i++) {
                    if (local.equals(spinnerLocal.getItemAtPosition(i))) {
                        spinnerLocal.setSelection(i);

                        break;
                    }
                }
            case 1:
                if (gender.equals("남")) {
                    spinnerGender.setSelection(1);
                } else if (gender.equals("여")) {
                    spinnerGender.setSelection(2);
                } else {
                    spinnerGender.setSelection(0);
                }
                break;
        }
    }

}
