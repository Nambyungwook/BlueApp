package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import static com.nbw.blueapp.GlobalApplication.NODATA_NUMBER;
import static com.nbw.blueapp.GlobalApplication.NODATA_STRING;
import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;
import static com.nbw.blueapp.utils.Utils.BirthdayToAge;

public class SearchSettingActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    //서버 디비에서 가져온 유저의 정보
    private String email;
    private String uid;
    private String local;
    private int income;
    private String gender;
    private String name;
    private String birthday;
    private String job;
    private String interest;
    private String phone;
    private String signType;

    private int age;

    //무관 지역을 포함하는지 확인하는 값
    public static String allLocalCB = "1";
    public static String allIncomeCB = "1";
    public static String allAgeCB = "1";
    public static String allGenderCB = "1";

    public static String selectedTargetMain;
    public static String selectedTargetDetail;
    public static String selectedLocal;
    public static String selectedSubLocal;
    public static String selectedIncome;
    public static String selectedAge;
    public static String selectedGender;

    private Spinner spinnerTargetMain;
    private Spinner spinnerTargetDetail;
    private Spinner spinnerLocal;
    private Spinner spinnerSubLocal;
    private Spinner spinnerIncome;
    private Spinner spinnerAge;
    private Spinner spinnerGender;

    //지역, 연령, 수입, 성별에 전체를 포함 할지 안할지 정하기 위함
    private LinearLayout layout_all_local;
    private CheckBox cb_all_local;
    private LinearLayout layout_all_income;
    private CheckBox cb_all_income;
    private LinearLayout layout_all_age;
    private CheckBox cb_all_age;
    private LinearLayout layout_all_gender;
    private CheckBox cb_all_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_setting);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        spinnerTargetMain = (Spinner) findViewById(R.id.spinner_target_main);
        spinnerTargetDetail = (Spinner) findViewById(R.id.spinner_target_detail);
        spinnerLocal = (Spinner) findViewById(R.id.spinner_local);
        spinnerSubLocal = (Spinner) findViewById(R.id.spinner_sub_local);
        spinnerIncome = (Spinner) findViewById(R.id.spinner_income);
        spinnerAge = (Spinner) findViewById(R.id.spinner_age);
        spinnerGender = (Spinner) findViewById(R.id.spinner_gender);

        layout_all_local = (LinearLayout) findViewById(R.id.layout_all_local);
        cb_all_local = (CheckBox) findViewById(R.id.cb_all_local);
        layout_all_income = (LinearLayout) findViewById(R.id.layout_all_income);
        cb_all_income = (CheckBox) findViewById(R.id.cb_all_income);
        layout_all_age = (LinearLayout) findViewById(R.id.layout_all_age);
        cb_all_age = (CheckBox) findViewById(R.id.cb_all_age);
        layout_all_gender = (LinearLayout) findViewById(R.id.layout_all_gender);
        cb_all_gender = (CheckBox) findViewById(R.id.cb_all_gender);


        //스피너 리스너 구현-------------------------------------------------------------------------

        spinnerTargetMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //목적 스피너에서 선택된 내용을 저장
                selectedTargetMain = adapterView.getItemAtPosition(i).toString();
                setTargetDetailSpinner(selectedTargetMain);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        spinnerTargetDetail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //목적 스피너에서 선택된 내용을 저장
                selectedTargetDetail = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerLocal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //지역 스피너에서 선택된 내용을 저장
                selectedLocal = adapterView.getItemAtPosition(i).toString();
                setAllLocalCheckBox(selectedLocal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerSubLocal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubLocal = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerIncome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //수입 스피너에서 선택된 내용을 저장
                selectedIncome = adapterView.getItemAtPosition(i).toString();
                setAllIncomeCheckBox(selectedIncome);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //나이 스피너에서 선택된 내용을 저장
                selectedAge = adapterView.getItemAtPosition(i).toString();
                setAllAgeCheckBox(selectedAge);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //성별 스피너에서 선택된 내용을 저장
                selectedGender = adapterView.getItemAtPosition(i).toString();
                setAllGenderCheckBox(selectedGender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //스피너 리스너 구현-------------------------------------------------------------------------

        //체크박스 설정-------------------------------------------------------------------------
        cb_all_local.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //무관 포함
                    allLocalCB = "1";
                } else {
                    //무관 제외
                    allLocalCB = "0";
                }
            }
        });

        cb_all_income.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //무관 포함
                    allIncomeCB = "1";
                } else {
                    //무관 제외
                    allIncomeCB = "0";
                }
            }
        });

        cb_all_age.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //무관 포함
                    allAgeCB = "1";
                } else {
                    //무관 제외
                    allAgeCB = "0";
                }
            }
        });

        cb_all_gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //무관 포함
                    allGenderCB = "1";
                } else {
                    //무관 제외
                    allGenderCB = "0";
                }
            }
        });
        //체크박스 설정-------------------------------------------------------------------------
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserInfo(uid);

        //전체, 무관 등 포함 여부 체크박스 표시 설정
        setAllLocalCheckBox(selectedLocal);
        setAllIncomeCheckBox(selectedIncome);
        setAllAgeCheckBox(selectedAge);
        setAllGenderCheckBox(selectedGender);
    }

    private void setAllLocalCheckBox(String selectedLocal) {
        if (selectedLocal==null||selectedLocal.equals("전체")||selectedLocal.equals("무관")) {
            layout_all_local.setVisibility(View.GONE);
        } else {
            layout_all_local.setVisibility(View.VISIBLE);
        }
    }
    private void setAllIncomeCheckBox(String selectedIncome) {
        if (selectedIncome==null||selectedIncome.equals("무관")) {
            layout_all_income.setVisibility(View.GONE);
        } else  {
            layout_all_income.setVisibility(View.VISIBLE);
        }
    }
    private void setAllAgeCheckBox(String selectedAge) {
        if (selectedAge==null||selectedAge.equals("무관")) {
            layout_all_age.setVisibility(View.GONE);
        } else {
            layout_all_age.setVisibility(View.VISIBLE);
        }
    }
    private void setAllGenderCheckBox(String selectedGender) {
        if (selectedGender==null||selectedGender.equals("무관")) {
            layout_all_gender.setVisibility(View.GONE);
        } else {
            layout_all_gender.setVisibility(View.VISIBLE);
        }
    }

    private void setTargetDetailSpinner(String selectedTargetMain) {
        if (selectedTargetMain==null) {
            selectedTargetMain = "전체";
        }
        switch (selectedTargetMain) {
            case "전체":
                ArrayAdapter<String> arrayAdapter_total = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.target_detail_total));
                spinnerTargetDetail.setAdapter(arrayAdapter_total);
                break;
            case "취업지원":
                ArrayAdapter<String> arrayAdapter_employment_support = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.target_detail_employment_support));
                spinnerTargetDetail.setAdapter(arrayAdapter_employment_support);
                break;
            case "창업지원":
                ArrayAdapter<String> arrayAdapter_startup_support = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.target_detail_startup_support));
                spinnerTargetDetail.setAdapter(arrayAdapter_startup_support);
                break;
            case "혜택":
                ArrayAdapter<String> arrayAdapter_benefits = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.target_detail_benefits));
                spinnerTargetDetail.setAdapter(arrayAdapter_benefits);
                break;
            case "공연":
                ArrayAdapter<String> arrayAdapter_show = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.target_detail_show));
                spinnerTargetDetail.setAdapter(arrayAdapter_show);
                break;
            case "쇼핑":
                ArrayAdapter<String> arrayAdapter_shop = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.target_detail_shop));
                spinnerTargetDetail.setAdapter(arrayAdapter_shop);
                break;
            case "기타":
                ArrayAdapter<String> arrayAdapter_etc = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.target_detail_etc));
                spinnerTargetDetail.setAdapter(arrayAdapter_etc);
                break;
        }
    }

    private void getUserInfo(String uid) {
        final String check_uid = uid;
        ServerApi.getUserInfo(uid, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(SearchSettingActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("uid").equals(check_uid)) {
                        Utils.toast(SearchSettingActivity.this, "사용자 정보를 불러오는데 실패했습니다.");
                        Intent intent = new Intent(SearchSettingActivity.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        email = ret.getString("email");
                        signType = ret.getString("signType");
                        if (ret.getString("name") == "null") {
                            name = NODATA_STRING;
                        } else {
                            name = ret.getString("name");
                        }
                        if (ret.getString("birthday") == "null"||ret.getString("birthday").equals("입력안함")) {
                            birthday = NODATA_STRING;
                            age = 0;
                        } else {
                            birthday = ret.getString("birthday");
                            age = BirthdayToAge(birthday);
                            //ui가 변경되는 경우(단순 글자가 아닌 화면상에 그려지는 것이 변경되는 경우) 쓰레드 사용
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSpinner(2);
                                }
                            });

                        }
                        if (ret.getString("gender") == "null") {
                            gender = NODATA_STRING;
                        } else {
                            gender = ret.getString("gender");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSpinner(3);
                                }
                            });
                        }
                        if (ret.getString("local") == "null") {
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
                        if (ret.getString("interest") == "null") {
                            interest = NODATA_STRING;
                        } else {
                            interest = ret.getString("interest");
                        }
                        if (ret.getString("job") == "null") {
                            job = NODATA_STRING;
                        } else {
                            job = ret.getString("job");
                        }
                        if (ret.getString("income") == "null") {
                            income = NODATA_NUMBER;
                        } else {
                            income = ret.getInt("income");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSpinner(1);
                                }
                            });
                        }
                        if (ret.getString("phone") == "null") {
                            phone = NODATA_STRING;
                        } else {
                            phone = ret.getString("phone");
                        }
                    }

                } catch (Exception e) {
                    Utils.toast(SearchSettingActivity.this, e + "");
                }
            }
        });
    }

    //스피너 초기값 설정
    private void initSpinner(int index) {

        switch (index) {
            case 0://지역
                for (int i = 0; i < 13; i++) {
                    if (local.contains("_")) {
                        String mainLocal = local.split("_")[0];
                        String subLocal = local.split("_")[1];

                        if (mainLocal.equals(spinnerLocal.getItemAtPosition(i))) {
                            spinnerLocal.setSelection(i);

                            for (int j = 0; j < 10; j++) {
                                if (subLocal.equals(spinnerSubLocal.getItemAtPosition(j))) {
                                    spinnerSubLocal.setSelection(j);

                                    break;
                                }
                            }

                            break;
                        }
                    } else if (local.equals(spinnerLocal.getItemAtPosition(i))) {
                        spinnerLocal.setSelection(i);

                        break;
                    }
                }
            case 1://연봉
                if (income == 0) {
                    spinnerIncome.setSelection(0);
                } else if (income <= 1000) {
                    spinnerIncome.setSelection(1);
                } else if (income <= 2000) {
                    spinnerIncome.setSelection(2);
                } else if (income <= 3000) {
                    spinnerIncome.setSelection(3);
                } else if (income <= 4000) {
                    spinnerIncome.setSelection(4);
                } else if (income <= 5000) {
                    spinnerIncome.setSelection(5);
                } else if (income <= 6000) {
                    spinnerIncome.setSelection(6);
                } else {
                    spinnerIncome.setSelection(7);
                }
                break;
            case 2://나이
                if (age<=10) {
                    spinnerAge.setSelection(1);
                } else if (age<20) {
                    spinnerAge.setSelection(2);
                } else if (age<30) {
                    spinnerAge.setSelection(3);
                } else if (age<40) {
                    spinnerAge.setSelection(4);
                } else if (age<50) {
                    spinnerAge.setSelection(5);
                } else if (age<60) {
                    spinnerAge.setSelection(6);
                } else {
                    spinnerAge.setSelection(7);
                }
                break;
            case 3://성별
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

    public void onClick_ok(View view) {
        finish();
    }
}
