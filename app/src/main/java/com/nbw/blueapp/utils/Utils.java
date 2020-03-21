package com.nbw.blueapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

//자주 사용하는 유용한 기능 정리 코드
public class Utils {

    //toast메세지 메소드화
    public static void toast(final Activity act, final String string) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //비밀번호 암호화 코드
    public static String StringToSHA1(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1"); // 이 부분을 SHA-256, MD5로만 바꿔주면 된다.
            md.update(str.getBytes()); // "세이프123"을 SHA-1으로 변환할 예정!

            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for(int i=0; i<byteData.length; i++) {
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }

            String sha1Value = sb.toString();
            return sha1Value;
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return "";
        }
    }

    //한국식 나이 계산
    public static int BirthdayToAge(String birthday) {
        int age = 0;
        int birthdayYear = Integer.parseInt(birthday.substring(0,4));

        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        int nowYear = calendar.get(Calendar.YEAR);

        age = nowYear - birthdayYear + 1;

        return age;
    }

}
