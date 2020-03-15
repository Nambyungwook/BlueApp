package com.nbw.blueapp.server;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
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

    static private Call get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

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
                        cb.onResponse(null, response.message());
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

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
                        cb.onResponse(null, response.message());
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

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
                        cb.onResponse(null, response.message());
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

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
                        cb.onResponse(null, response.message());
                    }
                } catch (Exception e) {
                    cb.onResponse(null, e.getMessage());
                }
            }
        });

    }

}
