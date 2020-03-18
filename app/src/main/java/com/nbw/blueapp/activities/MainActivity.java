package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

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

import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //구글 플레이스토어와 앱 버전 비교 및 업데이트 팝업 실행 - 테스트시에 주석처리
//        MainActivity.versionCheck versionCheck_ = new MainActivity.versionCheck();
//        versionCheck_.execute();
    }

    //로그아웃
    public void onClick_signout(View view) {

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
                    Utils.toast(MainActivity.this,e+"");
                }
            }
        });
    }

    //회원탈퇴
    public void onCLick_dropout(View view) {
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
                                    Utils.toast(MainActivity.this,e+"");
                                }
                            }
                        });
                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
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
