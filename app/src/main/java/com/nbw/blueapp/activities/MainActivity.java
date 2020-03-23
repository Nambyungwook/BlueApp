package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.nbw.blueapp.BuildConfig;
import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.nbw.blueapp.GlobalApplication.NODATA_NUMBER;
import static com.nbw.blueapp.GlobalApplication.NODATA_STRING;
import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;
import static com.nbw.blueapp.utils.Utils.BirthdayToAge;
import static com.nbw.blueapp.utils.Utils.toast;

public class MainActivity extends AppCompatActivity {

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

    private int age;

    //스피너를 통해 선택한 정보
    private String selectedTarget;
    private String selectedLocal;
    private String selectedIncome;
    private String selectedAge;
    private String selectedGender;

    private ListView navDrawerListview = null;

    private Spinner spinnerTarget;
    private Spinner spinnerLocal;
    private Spinner spinnerIncome;
    private Spinner spinnerAge;
    private Spinner spinnerGender;

    private Button btn_search;

    private EditText et_item_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        spinnerTarget = (Spinner) findViewById(R.id.spinner_target);
        spinnerLocal = (Spinner) findViewById(R.id.spinner_local);
        spinnerIncome = (Spinner) findViewById(R.id.spinner_income);
        spinnerAge = (Spinner) findViewById(R.id.spinner_age);
        spinnerGender = (Spinner) findViewById(R.id.spinner_gender);

        btn_search = (Button) findViewById(R.id.btn_search);

        et_item_title = (EditText) findViewById(R.id.et_item_title);

        spinnerTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //목적 스피너에서 선택된 내용을 저장
                selectedTarget = adapterView.getItemAtPosition(i).toString();
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //네비게이션드로어 설정---------------------------------------------------------------------------------------

        navDrawerListview = (ListView) findViewById(R.id.drawer_menulist);
        navDrawerListview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                switch (position) {
                    case 0: // 회원정보
                        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 1: // 로그아웃
                        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

                        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

                        ServerApi.getSignout(uid, new PostCallBack() {
                            @Override
                            public void onResponse(JSONObject ret, String errMsg) {
                                try {
                                    //api호출 실패로 서버에서 에러가 나는지 확인
                                    if (errMsg != null) {
                                        Utils.toast(MainActivity.this, errMsg);
                                        return;
                                    }
                                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                                    if (!ret.getString("response_code").equals("SUCCESS")) {
                                        Utils.toast(MainActivity.this, ret.getString("message"));
                                        return;
                                    }
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("uid", USER_SIGNOUT);
                                    editor.commit();

                                    // 로그아웃이 잘 끝났으니 SplashActivity로 화면을 바꿔주고 종료
                                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                                    startActivity(intent);
                                    finish();

                                } catch (Exception e) {
                                    Utils.toast(MainActivity.this, e + "");
                                }
                            }
                        });
                        break;
                    case 2: // 회원탈퇴
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("회원탈퇴")
                                .setMessage("회원탈퇴를 하시면 앱을 이용하실수 없습니다. 정말 탈퇴하시겠습니까?")
                                .setCancelable(false)
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                    }
                                })
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

                                        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

                                        ServerApi.dropout(uid, new PostCallBack() {
                                            @Override
                                            public void onResponse(JSONObject ret, String errMsg) {
                                                try {
                                                    //api호출 실패로 서버에서 에러가 나는지 확인
                                                    if (errMsg != null) {
                                                        Utils.toast(MainActivity.this, errMsg);
                                                        return;
                                                    }
                                                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                                                    if (!ret.getString("response_code").equals("SUCCESS")) {
                                                        Utils.toast(MainActivity.this, ret.getString("message"));
                                                        return;
                                                    }
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("uid", USER_SIGNOUT);
                                                    editor.commit();

                                                    Utils.toast(MainActivity.this, ret.getString("message"));

                                                    // 로그아웃이 잘 끝났으니 SplashActivity로 화면을 바꿔주고 종료
                                                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                } catch (Exception e) {
                                                    Utils.toast(MainActivity.this, e + "");
                                                }
                                            }
                                        });
                                    }
                                });
                        AlertDialog dialog = builder.create();    // 알림창 객체 생성
                        dialog.show();    // 알림창 띄우기
                        break;
                }
                // close drawer.
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
        //네비게이션드로어 설정 끝--------------------------------------------------------------------------------------

        //구글 플레이스토어와 앱 버전 비교 및 업데이트 팝업 실행 - 테스트시에 주석처리
//        MainActivity.versionCheck versionCheck_ = new MainActivity.versionCheck();
//        versionCheck_.execute();

        //제도 검색 버튼 기능 구현
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //검색할 제목
                String item_title = et_item_title.getText().toString();

                toast(MainActivity.this, "검색할이름 : " + item_title + "\n목적 : " + selectedTarget + "\n지역 : " + selectedLocal + "\n나이 : " + selectedAge + "\n연봉 : "+selectedIncome+"\n성별 : "+selectedGender);
            }
        });
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
                        Utils.toast(MainActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("uid").equals(check_uid)) {
                        Utils.toast(MainActivity.this, "사용자 정보를 불러오는데 실패했습니다.");
                        return;
                    } else {
                        email = ret.getString("email");
                        if (ret.getString("name") == "null") {
                            name = NODATA_STRING;
                        } else {
                            name = ret.getString("name");
                        }
                        if (ret.getString("birthday") == "null") {
                            birthday = NODATA_STRING;
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
                    Utils.toast(MainActivity.this, e + "");
                }
            }
        });
    }

    //스피너 초기값 설정
    private void initSpinner(int index) {

        switch (index) {
            case 0://지역
                for (int i = 0; i < 13; i++) {
                    if (local.equals(spinnerLocal.getItemAtPosition(i))) {
                        spinnerLocal.setSelection(i);

                        break;
                    }
                }
            case 1://연봉
                if (income == 0) {
                    spinnerIncome.setSelection(0);
                } else if (income < 1000) {
                    spinnerIncome.setSelection(1);
                } else if (income < 2000) {
                    spinnerIncome.setSelection(2);
                } else if (income < 3000) {
                    spinnerIncome.setSelection(3);
                } else if (income < 4000) {
                    spinnerIncome.setSelection(4);
                } else if (income < 5000) {
                    spinnerIncome.setSelection(5);
                } else if (income < 6000) {
                    spinnerIncome.setSelection(6);
                } else {
                    spinnerIncome.setSelection(7);
                }
                break;
            case 2://나이
                if (age<=10) {
                    spinnerAge.setSelection(0);
                } else if (age<20) {
                    spinnerAge.setSelection(1);
                } else if (age<30) {
                    spinnerAge.setSelection(2);
                } else if (age<40) {
                    spinnerAge.setSelection(3);
                } else if (age<50) {
                    spinnerAge.setSelection(4);
                } else if (age<60) {
                    spinnerAge.setSelection(5);
                } else if (age<70) {
                    spinnerAge.setSelection(6);
                } else if (age<80) {
                    spinnerAge.setSelection(7);
                } else {
                    spinnerAge.setSelection(8);
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

    //구글플레이스토어의 걷다 어플 버전가져와서 현재 어플과 비교 - 강제 업데이트를 위함
    private class versionCheck extends AsyncTask<Void, Void, String> {

        private final String APP_VERSION_NAME = BuildConfig.VERSION_NAME;

        private final String STORE_URL = "추후에 구글 플레이 스토어에 올린 우리 앱의 주소";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(STORE_URL).get();

                Elements Version = doc.select(".htlgb");

                for (int i = 0; i < Version.size(); i++) {

                    String VersionMarket = Version.get(i).text();

                    if (Pattern.matches("^[0-9]{1}.[0-9]{1}.[0-9]{1}[a-z]{1}$", VersionMarket) || Pattern.matches("^[0-9]{1}.[0-9]{1}.[0-9]{1}$", VersionMarket)) {

                        return VersionMarket;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) { //s는 마켓의 버전
            if (s != null) {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(MainActivity.this);
                if (!s.equals(APP_VERSION_NAME)) { //APP_VERSION_NAME는 현재 앱의 버전
                    mDialog.setMessage("최신 버전이 출시되었습니다. 업데이트 후 사용 가능합니다.")
                            .setCancelable(false)
                            .setPositiveButton("업데이트 바로가기",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            Intent marketLaunch = new Intent(
                                                    Intent.ACTION_VIEW);
                                            marketLaunch.setData(Uri
                                                    .parse(STORE_URL));
                                            startActivity(marketLaunch);
                                            finish();
                                        }
                                    });
                    AlertDialog alert = mDialog.create();
                    alert.setTitle("업데이트 알림");
                    alert.show();
                }
            }
            super.onPostExecute(s);
        }
    }
}
