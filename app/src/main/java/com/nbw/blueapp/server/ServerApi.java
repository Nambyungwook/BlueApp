package com.nbw.blueapp.server;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.nbw.blueapp.GlobalApplication.SERVER_IP;


public class ServerApi {

    //주로 post 바디를 구성할대 json형식으로 구성
    static private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //okhttp를 사용하여 api 호출을 한다.
    static private OkHttpClient client = new OkHttpClient();


    //post호출
    static private Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //get호출
    static private Call get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //get호출 + 요청 파라미터가 존재시 - email
    static private Call getParam_email(String url,
                                 String email, Callback callback) {

        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        builder.addQueryParameter("email", email);

        String requestUrl = builder.build().toString();

        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //get호출 + 요청 파라미터가 존재시
    static private Call getParam(String url,
                                 String page,
                                 String size,
                                 String targetMain,
                                 String targetDetail,
                                 String local,
                                 String all_local,
                                 String income,
                                 String all_income,
                                 String age,
                                 String all_age,
                                 String gender,
                                 String all_gender,
                                 String siteName, Callback callback) {

        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        builder.addQueryParameter("page", page)
                .addQueryParameter("size",size)
                .addQueryParameter("targetMain", targetMain)
                .addQueryParameter("targetDetail", targetDetail)
                .addQueryParameter("local", local)
                .addQueryParameter("allLocal", all_local)
                .addQueryParameter("income",income)
                .addQueryParameter("allIncome", all_income)
                .addQueryParameter("age",age)
                .addQueryParameter("allAge", all_age)
                .addQueryParameter("gender",gender)
                .addQueryParameter("allGender", all_gender)
                .addQueryParameter("siteName",siteName);

        String requestUrl = builder.build().toString();

        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //put호출
    static private Call put(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //서버연결확인
    static public void checkServer(final PostCallBack cb) {
        get(SERVER_IP+"/blue/v1/check/", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //회원가입
    static public void signupPost(JSONObject params, final PostCallBack cb) {
        post(SERVER_IP+"/blue/v1/users/signup/", params.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //로그인
    static public void signinPost(JSONObject params, final PostCallBack cb) {
        post(SERVER_IP+"/blue/v1/users/signin/", params.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //회원정보조회 - uid
    static public void getUserInfo(String uid, final PostCallBack cb) {
        get(SERVER_IP+"/blue/v1/users/userinfo/"+ uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //회원정보조회 - email
    static public void getUserInfo_email(String email, final PostCallBack cb) {
        getParam_email(SERVER_IP+"/blue/v1/users/userinfo", email, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //회원정보수정
    static public void updateUserInfoPost(JSONObject params, String uid, final PostCallBack cb) {
        put(SERVER_IP+"/blue/v1/users/update/" + uid, params.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //로그인상태
    static public void getSignStatus(String uid, final PostCallBack cb) {
        get(SERVER_IP+"/blue/v1/users/status/"+ uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //로그아웃
    static public void getSignout(String uid, final PostCallBack cb) {
        get(SERVER_IP+"/blue/v1/users/signout/"+ uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //회원탈퇴
    static public void dropout(String uid, final PostCallBack cb) {
        get(SERVER_IP+"/blue/v1/users/dropout/"+ uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //사이트 조건에 맞워서 검색
    static public void getSites(String page,
                                String size,
                                String targetMain,
                                String targetDetail,
                                String local,
                                String all_local,
                                String income,
                                String all_income,
                                String age,
                                String all_age,
                                String gender,
                                String all_gender,
                                String siteName,
                                final PostCallBack cb) {
        getParam(SERVER_IP+"/blue/v1/sites/", page, size, targetMain, targetDetail, local, all_local, income, all_income, age, all_age, gender, all_gender, siteName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //사용자가 원하는 사이트 저장(즐겨찾기 추가)
    static public void saveSites(JSONObject params, final PostCallBack cb) {
        post(SERVER_IP+"/blue/v1/users/save/sites/", params.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //저장한 사이트 조회
    static public void getUserSavedSites(String uid, final PostCallBack cb) {
        get(SERVER_IP+"/blue/v1/users/sites/"+uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

    //저장한 사이트 삭제
    static public void deleteUserSavedSites(String uid, Long siteId, final PostCallBack cb) {
        get(SERVER_IP+"/blue/v1/users/delete/site/"+uid+"/"+siteId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null)
                    cb.onResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cb == null)
                    return;
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        cb.onResponse(new JSONObject(responseStr), null);
                    } else {
                        String responseStr = response.body().string();
                        JSONObject ret = new JSONObject(responseStr);
                        cb.onResponse(null, ret.getString("message"));
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }
}
