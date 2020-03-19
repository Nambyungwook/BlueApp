package com.nbw.blueapp.utils;

import java.util.ArrayList;

//스피너관련 어레이리스트 변수 설정
public class SpinnerVariables {

    public static ArrayList<String> arrayList_target;
    public static ArrayList<String> arrayList_local;
    public static ArrayList<String> arrayList_income;
    public static ArrayList<String> arrayList_age;
    public static ArrayList<String> arrayList_gender;

    public static void setArrayList_target() {
        arrayList_target = new ArrayList<>();
        SpinnerVariables.arrayList_target.add("지원금");
        SpinnerVariables.arrayList_target.add("일자리");
        SpinnerVariables.arrayList_target.add("공연");
        SpinnerVariables.arrayList_target.add("쇼핑");
        SpinnerVariables.arrayList_target.add("기타");
    }

    public static void setArrayList_local() {
        arrayList_local = new ArrayList<>();
        SpinnerVariables.arrayList_local.add("서울");
        SpinnerVariables.arrayList_local.add("경기도");
        SpinnerVariables.arrayList_local.add("인천");
        SpinnerVariables.arrayList_local.add("강원도");
        SpinnerVariables.arrayList_local.add("부산");
        SpinnerVariables.arrayList_local.add("대구");
        SpinnerVariables.arrayList_local.add("울산");
        SpinnerVariables.arrayList_local.add("경상도");
        SpinnerVariables.arrayList_local.add("대전");
        SpinnerVariables.arrayList_local.add("충청도");
        SpinnerVariables.arrayList_local.add("광주");
        SpinnerVariables.arrayList_local.add("전라도");
    }

    public static void setArrayList_income() {
        arrayList_income = new ArrayList<>();
        SpinnerVariables.arrayList_income.add("없음");
        SpinnerVariables.arrayList_income.add("0~1000만원");
        SpinnerVariables.arrayList_income.add("1000~2000만원");
        SpinnerVariables.arrayList_income.add("2000~3000만원");
        SpinnerVariables.arrayList_income.add("3000~4000만원");
        SpinnerVariables.arrayList_income.add("4000~5000만원");
        SpinnerVariables.arrayList_income.add("5000~6000만원");
        SpinnerVariables.arrayList_income.add("6000만원이상");
    }

    public static void setArrayList_age() {
        arrayList_age = new ArrayList<>();
        SpinnerVariables.arrayList_age.add("10대");
        SpinnerVariables.arrayList_age.add("20대");
        SpinnerVariables.arrayList_age.add("30대");
        SpinnerVariables.arrayList_age.add("40대");
        SpinnerVariables.arrayList_age.add("50대");
        SpinnerVariables.arrayList_age.add("60대이상");
    }

    public static void setArrayList_gender() {
        arrayList_gender = new ArrayList<>();
        SpinnerVariables.arrayList_gender.add("남");
        SpinnerVariables.arrayList_gender.add("여");
    }
}
