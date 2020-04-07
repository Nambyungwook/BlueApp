package com.nbw.blueapp;

public class GlobalApplication {

    public static String id;
    public static String pwd;

    public static String SERVER_IP = "http://13.209.106.116:9009";//aws서버ip
    //public static String SERVER_IP = "http://172.30.1.47:9009";

    //회원상태
    public static String USER_SIGNOUT = "signout";

    //디비에서 데이터를 불러올때 값이 null인 경우
    public static String NODATA_STRING = "입력안함";
    public static int NODATA_NUMBER = 0;
}
