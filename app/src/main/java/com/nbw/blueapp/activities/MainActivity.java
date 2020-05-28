package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nbw.blueapp.BuildConfig;
import com.nbw.blueapp.R;
import com.nbw.blueapp.activities.dialogs.NoticePopupActivity;
import com.nbw.blueapp.items.Sites;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.SitesListAdapter;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;
import static com.nbw.blueapp.activities.SearchSettingActivity.allAgeCB;
import static com.nbw.blueapp.activities.SearchSettingActivity.allGenderCB;
import static com.nbw.blueapp.activities.SearchSettingActivity.allIncomeCB;
import static com.nbw.blueapp.activities.SearchSettingActivity.allLocalCB;
import static com.nbw.blueapp.activities.SearchSettingActivity.selectedAge;
import static com.nbw.blueapp.activities.SearchSettingActivity.selectedGender;
import static com.nbw.blueapp.activities.SearchSettingActivity.selectedIncome;
import static com.nbw.blueapp.activities.SearchSettingActivity.selectedLocal;
import static com.nbw.blueapp.activities.SearchSettingActivity.selectedSubLocal;
import static com.nbw.blueapp.activities.SearchSettingActivity.selectedTargetDetail;
import static com.nbw.blueapp.activities.SearchSettingActivity.selectedTargetMain;

//구글 애드몹
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    //구글 로그아웃시 필요
    private GoogleSignInClient mGoogleSignInClient;

    //서버 디비에서 가져온 유저의 정보
    private String uid;
    private String birthday;
    private String signType;

    private ListView navDrawerListview = null;
    private ListView sitesListview;

    private DrawerLayout drawer;

    private Button btn_search;

    private EditText et_item_title;

    private InputMethodManager imm;

    private TextView tv_error;

    //구글 애드몹 광고
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //구글 애드몹 광고 초기화 및 로드-----------------시작------------------------------------------------
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //구글 애드몹 광고 초기화 및 로드-----------------------------------------------------------------끝---

        //구글 인증 관련-----------시작--------------------------------------------------------------------------
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //구글인증관련 끝----------------------------------------------------------------------------------------

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        //최신 공지 보여주기 메소드
        getLastNotice();

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        btn_search = (Button) findViewById(R.id.btn_search);

        et_item_title = (EditText) findViewById(R.id.et_item_title);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        sitesListview = (ListView) findViewById(R.id.lv_sites);

        tv_error = (TextView) findViewById(R.id.tv_error);

        //=========================================================================================================

        //사이트명 검색시 키보드 엔터키로 바로 검색할수있도록 연결---------------------------------------------------------------------
        et_item_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_ENTER:
                        //검색하기색
                        getSitesToListview();
                        imm.hideSoftInputFromWindow(et_item_title.getWindowToken(), 0);

                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        //------------------------------------------------------------------------------------------------------------------

        //네비게이션드로어 설정---------------------------------------------------------------------------------------

        navDrawerListview = (ListView) findViewById(R.id.drawer_menulist);
        navDrawerListview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                switch (position) {
                    case 0: // 공지사항
                        Intent intentNotice = new Intent(MainActivity.this, NoticeActivity.class);
                        startActivity(intentNotice);
                        break;
                    case 1: // 회원정보
                        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 2: // 로그아웃
                        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

                        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

                        getUserInfo(uid);

                        if (signType.equals("G")) {
                            // Google sign out
                            mGoogleSignInClient.signOut();
                        } else if (signType.equals("K")) {

                        } else if (signType.equals("N")) {

                        }

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
                                    if (!ret.getString("responseCode").equals("SUCCESS")) {
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
                    case 3: // 회원탈퇴
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

                                        getUserInfo(uid);

                                        if (signType.equals("G")) {
                                            // Google sign out
                                            mGoogleSignInClient.signOut();
                                        } else if (signType.equals("K")) {

                                        } else if (signType.equals("N")) {

                                        }

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
                                                    if (!ret.getString("responseCode").equals("SUCCESS")) {
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
                    case 4: // 내사이트
                        Intent intentMySites = new Intent(MainActivity.this, UserSavedSitesActivity.class);
                        startActivity(intentMySites);

                        break;
                    case 5: // 문의하기
                        Intent contactIntent = new Intent(MainActivity.this, ContactActivity.class);
                        startActivity(contactIntent);

                        break;

                }
                // close drawer.
                drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        //네비게이션드로어 설정 끝--------------------------------------------------------------------------------------

        //=========================================================================================================

        //제도 검색 버튼 기능 구현
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSitesToListview();
                //키보드 내리기
                imm.hideSoftInputFromWindow(et_item_title.getWindowToken(), 0);

                //toast(MainActivity.this, "검색할이름 : " + item_title + "\n목적 : " + selectedTargetMain + "\n지역 : " + selectedLocal + "\n나이 : " + selectedAge + "\n연봉 : "+selectedIncome+"\n성별 : "+selectedGender);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //버전 체크
        //구글 플레이스토어와 앱 버전 비교 및 업데이트 팝업 실행 - 테스트시에 주석처리
        MainActivity.versionCheck versionCheck_ = new MainActivity.versionCheck();
        versionCheck_.execute();

        if (uid.equals(USER_SIGNOUT)) {
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }

        getSitesToListview();


        //사이트 리스트뷰 리스너 구현----------------------------------------------------------------------------------------

        sitesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Sites site = (Sites) parent.getItemAtPosition(position);

                Long siteId = site.getId();
                String targetMain = site.getTargetMain();
                String targetDetail = site.getTargetDetail();
                String local = site.getLocal();
                String income = site.getIncome();
                String age = site.getAge();
                String gender = site.getGender();
                String siteName = site.getSiteName();
                String siteUrl = site.getSiteUrl();
                String siteDetail = site.getSiteDetail();

                Intent intent = new Intent(MainActivity.this, SiteDetailActivity.class);
                intent.putExtra("siteId", siteId);
                intent.putExtra("targetMain", targetMain);
                intent.putExtra("targetDetail", targetDetail);
                intent.putExtra("local", local);
                intent.putExtra("income", income);
                intent.putExtra("age", age);
                intent.putExtra("gender", gender);
                intent.putExtra("siteName", siteName);
                intent.putExtra("siteUrl", siteUrl);
                intent.putExtra("siteDetail", siteDetail);
                startActivity(intent);
            }
        });

        //사이트 리스트뷰 리스너 구현 End----------------------------------------------------------------------------------------
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
                        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        signType = ret.getString("signType");
                    }

                } catch (Exception e) {
                    Utils.toast(MainActivity.this, e + "");
                }
            }
        });
    }

    private void getSitesToListview() {
        final SitesListAdapter sitesListAdapter = new SitesListAdapter();

        String targetMain = "";
        if (selectedTargetMain!=null&&!selectedTargetMain.equals("전체")) {
            targetMain = selectedTargetMain;
        }
        String targetDetail = "";
        if (selectedTargetDetail!=null&&!selectedTargetDetail.equals("전체")) {
            targetDetail = selectedTargetDetail;
        }
        String mainLocal = "";
        if (selectedLocal!=null&&!selectedLocal.equals("전체")) {
            mainLocal = selectedLocal;
        }
        String subLocal = "";
        if (selectedSubLocal!=null&&!selectedSubLocal.equals("전체")) {
            subLocal = selectedSubLocal;
        }
        String income = "";
        if (selectedIncome!=null&&!selectedIncome.equals("무관")) {
            income = selectedIncome;
        }
        String age = "";
        if (selectedAge!=null&&!selectedAge.equals("무관")) {
            age = selectedAge;
        }
        String gender = "";
        if (selectedGender!=null&&!selectedGender.equals("무관")) {
            gender = selectedGender;
        }
        String siteName = et_item_title.getText().toString();
        if (siteName.equals("")) {
            siteName = "";
        }

        ServerApi.getSites("0",
                "10",
                targetMain,
                targetDetail,
                mainLocal,
                subLocal,
                allLocalCB,
                income,
                allIncomeCB,
                age,
                allAgeCB,
                gender,
                allGenderCB,
                siteName, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(MainActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("responseCode").equals("SUCCESS")) {
                        Utils.toast(MainActivity.this, "사용자 정보를 불러오는데 실패했습니다.");
                        return;
                    } else if (ret.get("sites").toString().equals("[]")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_error.setVisibility(View.VISIBLE);
                                sitesListview.setVisibility(View.GONE);
                            }
                        });
                        Utils.toast(MainActivity.this, "해당 사이트가 없습니다.");
                        return;
                    } else {
                        Gson gson = new GsonBuilder().create();
                        JsonParser parser = new JsonParser();
                        JsonElement rootObject = parser.parse(ret.get("sites").toString());

                        Sites[] sites = gson.fromJson(rootObject, Sites[].class);

                        if(sites.length > 0){
                            for(Sites p : sites){
                                sitesListAdapter.addItem(p.getId(),
                                        p.getTargetMain(),
                                        p.getTargetDetail(),
                                        p.getLocal(),
                                        p.getIncome(),
                                        p.getAge(),
                                        p.getGender(),
                                        p.getSiteName(),
                                        p.getSiteUrl(),
                                        p.getSiteDetail());
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_error.setVisibility(View.GONE);
                                sitesListview.setVisibility(View.VISIBLE);
                                sitesListview.setAdapter(sitesListAdapter);
                            }
                        });

                    }

                } catch (Exception e) {
                    Utils.toast(MainActivity.this, e + "");
                }
            }
        });

    }


    public void onClick_help(View view) {
        //사용방법 설명 창으로 이동
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    public void onClick_menu(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer);
        drawer.openDrawer(GravityCompat.START);
    }

    private void getLastNotice() {

        String today = Utils.getCurrentDate();

        int todayChecker = sharedPreferences.getInt(today, 0);

        if (todayChecker==0) {
            Intent intent = new Intent(MainActivity.this,NoticePopupActivity.class);
            startActivity(intent);
        }

    }

    public void onClick_search_setting(View view) {
        Intent intent = new Intent(MainActivity.this, SearchSettingActivity.class);
        startActivity(intent);
    }

    //구글플레이스토어의 걷다 어플 버전가져와서 현재 어플과 비교 - 강제 업데이트를 위함
    private class versionCheck extends AsyncTask<Void, Void, String> {
        private AppCompatActivity appCompatActivity = new AppCompatActivity();
        private final String APP_VERSION_NAME = BuildConfig.VERSION_NAME;
        private final String APP_PACKAGE_NAME = BuildConfig.APPLICATION_ID;

        private final String STORE_URL = "https://play.google.com/store/apps/details?id=com.nbw.blueapp";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(STORE_URL).ignoreHttpErrors(true).get();

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
        protected void onPostExecute(String s) { //s는 마켓의 버전입니다.
            if (s != null) {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(MainActivity.this);
                if (!s.equals(APP_VERSION_NAME)) { //APP_VERSION_NAME는 현재 앱의
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
