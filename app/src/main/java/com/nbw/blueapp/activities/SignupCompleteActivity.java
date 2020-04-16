package com.nbw.blueapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nbw.blueapp.GlobalApplication;
import com.nbw.blueapp.R;
import com.nbw.blueapp.server.PostCallBack;
import com.nbw.blueapp.server.ServerApi;
import com.nbw.blueapp.utils.Utils;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static com.nbw.blueapp.GlobalApplication.NODATA_NUMBER;
import static com.nbw.blueapp.GlobalApplication.NODATA_STRING;
import static com.nbw.blueapp.GlobalApplication.USER_SIGNOUT;
import static com.nbw.blueapp.utils.Utils.StringToSHA1;

public class SignupCompleteActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    private String id;
    private String pwd;
    private String uid;
    private String name;
    private String birthday;
    private String gender;
    private String local;
    private String job;
    private String interest;
    private int income;

    //파이어베이스 전화번호 인증시 필요한 값
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    //firebase 인증관련 변수 선언및 초기화
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    //전화번호, 인증번호 입력용
    private EditText et_phone;
    private EditText et_mVerificationField;

    //인증번호 전송, 인증받기, 재전송, 인증완료표시, 국가코드스피너 선언
    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private Button mCompleteButton;
    private Button btn_next;
    private Spinner sprinner_country_code;

    //국가코드, 전화번호, firebaseuid
    public String country_code;
    public String phoneNumber;
    public String firebaseUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_complete);

        sharedPreferences = getSharedPreferences("blue", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        id = intent.getStringExtra("email");
        pwd = intent.getStringExtra("pwd");
        uid = sharedPreferences.getString("uid", USER_SIGNOUT);

        sprinner_country_code = (Spinner) findViewById(R.id.sprinner_country_code);
        sprinner_country_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                country_code = adapterView.getItemAtPosition(i).toString();//선택된 국가코드 받아옴
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        et_phone = (EditText) findViewById(R.id.et_phone_number);
        et_mVerificationField = findViewById(R.id.etVerificationCode);

        mStartButton = findViewById(R.id.btn_StartVerification);
        mVerifyButton = findViewById(R.id.btn_VerifyPhone);
        mResendButton = findViewById(R.id.btn_Resend);
        mCompleteButton = findViewById(R.id.btn_Complete);
        btn_next = (Button) findViewById(R.id.btn_next);

        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    et_phone.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        Log.d("credential", credential.toString());
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();

                            //db에 저장해야되는 값 폰번호
                            phoneNumber = user.getPhoneNumber();
                            firebaseUID = user.getUid();

                            mVerifyButton.setVisibility(View.GONE);
                            mResendButton.setVisibility(View.GONE);
                            mCompleteButton.setVisibility(View.VISIBLE);
                            mCompleteButton.setPressed(true);
                            mCompleteButton.setClickable(false);
                            et_mVerificationField.setVisibility(View.GONE);
                            btn_next.setVisibility(View.VISIBLE);
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                et_mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                        }
                    }
                });
    }

    //휴대폰번호 확인
    private boolean validatePhoneNumber() {
        String phoneNumber = et_phone.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            et_phone.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    //버튼 클릭 스위치문으로 분류
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //인증번호 발송
            case R.id.btn_StartVerification:
                if (!validatePhoneNumber()) {
                    return;
                }

                //실제 인증번호 발송시에는 국가 코드가 포함되어야 파이어베이스에서 발송해준다.
                String phoneNum = country_code + et_phone.getText().toString();

                //인증번호 발송 메소드 실행
                startPhoneNumberVerification(phoneNum);

                //인증번호 입력 필드, 인증하기 버튼, 재전송 버튼이 보이게 설정
                et_mVerificationField.setVisibility(View.VISIBLE);
                mVerifyButton.setVisibility(View.VISIBLE);
                mResendButton.setVisibility(View.VISIBLE);
                //인증번호 발송 버튼은 안보이게 설정
                mStartButton.setVisibility(View.GONE);


                break;
            //인증하기
            case R.id.btn_VerifyPhone:
                //인증번호 입력 값을 받아온다.
                String code = et_mVerificationField.getText().toString();
                //비어있는지 확인
                if (TextUtils.isEmpty(code)) {
                    et_mVerificationField.setError("Cannot be empty.");
                    return;
                }
                //인증번호가 맞는지 판단하는 메소드 호출
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            //재전송
            case R.id.btn_Resend:
                //전화번호와 재전송 토큰을 입력으로 인증번호 재전송 메소드 호출
                resendVerificationCode(country_code + et_phone.getText().toString(), mResendToken);
                break;
        }
    }
    //스마트폰 뒤로가기 버튼 막음
    @Override
    public void onBackPressed() {
        return;
    }

    public void onClick_ok(View view) {

        updateUserInfo(uid);

    }

    //회원정보수정
    private void updateUserInfo(String uid) {

        String phone = "0" + phoneNumber.substring(3,5) + "-" + phoneNumber.substring(5,9) + "-" + phoneNumber.substring(9);


        try {
            //서버에 회원가입 정보 전달
            JSONObject json = new JSONObject();
            json.put("phone", phone);

            //회원가입 api 호출
            ServerApi.updateUserInfoPost(json, uid, new PostCallBack() {
                @Override
                public void onResponse(JSONObject ret, String errMsg) {
                    try {
                        //api호출 실패로 서버에서 에러가 나는지 확인
                        if (errMsg != null) {
                            Utils.toast(SignupCompleteActivity.this, errMsg);
                            return;
                        }
                        //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                        if (!ret.getString("responseCode").equals("SUCCESS")) {
                            Utils.toast(SignupCompleteActivity.this, "이미 인증된 전화번호입니다. 다시 입력하려면 회원정보창을 이용해주세요.");

                            String pwd_sha1 = StringToSHA1(pwd);

                            signin(id, pwd_sha1);

                            return;
                        }

                        Utils.toast(SignupCompleteActivity.this, "전화번호 인증을 완료했습니다.");

                        String pwd_sha1 = StringToSHA1(pwd);

                        signin(id, pwd_sha1);

                    } catch (Exception e) {
                        Utils.toast(SignupCompleteActivity.this,e+"");
                    }
                }
            });
        } catch (Exception e) {

        }
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
                            Utils.toast(SignupCompleteActivity.this, errMsg);
                            return;
                        }
                        //api호출은 작동했지만 code가 성공이 아닌 다른 경우에 무슨 에러인지 보여주는 부분
                        if (!ret.getString("responseCode").equals("SUCCESS")) {
                            Utils.toast(SignupCompleteActivity.this, ret.getString("message"));
                            return;
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("uid", ret.getString("uid"));
                        editor.commit();

                        // 로그인이 잘 끝났으니 MainActivity로 화면을 바꿔주고 종료
                        Intent intent = new Intent(SignupCompleteActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Utils.toast(SignupCompleteActivity.this,e+"");
                    }
                }
            });
        } catch (Exception e) {

        }
    }
}
