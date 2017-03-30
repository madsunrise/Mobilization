package com.rv150.mobilization.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ivan on 30.03.17.
 */

public class ApiHelper {
    private static final ApiHelper instance = new ApiHelper();

    private static final String SERVER_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?" +
            "key=trnsl.1.1.20170330T065607Z.dc9520b57e28c5f3.d470142e2a9021eb88919a66af16cf82457f17f0&";


    private final OkHttpClient client = new OkHttpClient();
    private ApiCallback callback;

    public static ApiHelper getInstance() {
        return instance;
    }

    public interface ApiCallback {
        void onDataLoaded(String result);
    }

    public void setCallback(ApiCallback callback) {
        this.callback = callback;
    }

    public void requestTranslate(String query) {
        Request request = new Request.Builder()
                .url(SERVER_URL + "text=" + query + "&lang=en-ru")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try  {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    if (callback != null) {
                        callback.onDataLoaded(responseBody.string());
                    }
                    responseBody.close();
                }
                catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.getMessage());
                }
            }
        });
    }
}

