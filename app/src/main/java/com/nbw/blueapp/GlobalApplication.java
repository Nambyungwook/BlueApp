package com.nbw.blueapp;

public class GlobalApplication {

    public static String id;
    public static String pwd;

    public static final String SERVER_IP = "http://13.209.106.116:9009";//aws서버ip
    //public static String SERVER_IP = "http://172.30.1.50:9009";

    //회원상태
    public static final String USER_SIGNOUT = "signout";

    //디비에서 데이터를 불러올때 값이 null인 경우
    public static final String NODATA_STRING = "입력안함";
    public static final int NODATA_NUMBER = 0;

    //targetMain 설정
    public static final String TARGET_MAIN_1 = "전체";
    public static final String TARGET_MAIN_2 = "취업지원";
    public static final String TARGET_MAIN_3 = "창업지원";
    public static final String TARGET_MAIN_4 = "혜택";
    public static final String TARGET_MAIN_5 = "공연";
    public static final String TARGET_MAIN_6 = "쇼핑";
    public static final String TARGET_MAIN_7 = "기타";
}
