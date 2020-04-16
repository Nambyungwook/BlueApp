package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.GMailSender;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import static com.nbw.blueapp.GlobalApplication.NODATA_NUMBER;
import static com.nbw.blueapp.GlobalApplication.NODATA_STRING;
import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;
import static com.nbw.blueapp.utils.Utils.BirthdayToAge;

public class ContactActivity extends AppCompatActivity {

    private long mLastClickTime = 0;//버튼 중복 클릭 방지용 변수

    private String uid;
    private String email;
    private String phone;
    private String contactContents;

    private SharedPreferences sharedPreferences;

    EditText et_contact_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        et_contact_contents = (EditText) findViewById(R.id.et_contact_contents);

        getUserInfo(uid);

        //메일 자동 발송시 필요한 설정
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        //contactDDIPS@gmail.com
        //ddips3691!@
    }

    public void onClick_send_mail(View view) {

        //중복 클릭 방지를 위해 클릭리스너 안에 시간초를 재서 확인
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        contactContents = et_contact_contents.getText().toString();
        final String userEmail = email;
        final String userPhone = phone;

        //메일 자동 보내기
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //보내는 메일 주소, 비밀번호
                    GMailSender gMailSender = new GMailSender("contactDDIPS@gmail.com", "ddips3691!@");
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("문의자 UID : " + uid, "문의내용 : " + contactContents + "\n전화번호 : " + userPhone + "\n이메일 : " + userEmail, "contactDDIPS@gmail.com");
                } catch (SendFailedException e) {
                    //Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);

        builder.setTitle("문의하기")
                .setMessage("정상적으로 문의가 들어갔습니다. 최대한 빠른 시일내로 문의사항에 대한 답변을 드리겠습니다.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    public void onClick_back(View view) {
        finish();
    }

    private void getUserInfo(String uid) {
        final String check_uid = uid;
        ServerApi.getUserInfo(uid, new PostCallBack() {
            @Override
            public void onResponse(JSONObject ret, String errMsg) {
                try {
                    //api호출 실패로 서버에서 에러가 나는지 확인
                    if (errMsg != null) {
                        Utils.toast(ContactActivity.this, errMsg);
                        return;
                    }
                    //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                    if (!ret.getString("uid").equals(check_uid)) {
                        Utils.toast(ContactActivity.this, "사용자 정보를 불러오는데 실패했습니다.");
                        Intent intent = new Intent(ContactActivity.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        email = ret.getString("email");
                        if (ret.getString("phone") == "null") {
                            phone = NODATA_STRING;
                        } else {
                            phone = ret.getString("phone");
                        }
                    }

                } catch (Exception e) {
                    Utils.toast(ContactActivity.this, e + "");
                }
            }
        });
    }
}
