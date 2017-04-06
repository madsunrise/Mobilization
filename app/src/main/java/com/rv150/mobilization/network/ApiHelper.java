package com.rv150.mobilization.network;

import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.rv150.mobilization.R;
import com.rv150.mobilization.activity.MainActivity;
import com.rv150.mobilization.utils.UiThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    private ExecutorService executor = Executors.newFixedThreadPool(4);

    private final OkHttpClient client = new OkHttpClient();
    private ApiCallback callback;

    public static final int ERR_NETWORK = 0;
    public static final int ERR_PARSING = 1;

    public static ApiHelper getInstance() {
        return instance;
    }

    public interface ApiCallback {
        void onDataLoaded(String result);
        void dataLoadingFailed(int errCode);
    }

    public void setCallback(ApiCallback callback) {
        synchronized (ApiCallback.class) {
            this.callback = callback;
        }
    }


    private LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    Log.d(TAG, "Requesting \'" + key + "\' from network...");
                    final Request request = new Request.Builder()
                            .url(SERVER_URL + "text=" + key + "&lang=en-ru")
                            .build();
                     return client.newCall(request).execute().body().string();
                }
            });



    public void requestTranslate(final String query) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = cache.get(query);
                    synchronized (ApiCallback.class) {
                        if (callback != null) {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray array = jsonObject.getJSONArray("text");
                            final String text = (String)array.get(0);
                            callback.onDataLoaded(text);
                        }
                    }
                }
                catch (ExecutionException ex) {
                    Log.e(TAG, "Failed http request: " + ex.getMessage());
                    synchronized (ApiCallback.class) {
                        if (callback != null) {
                            callback.dataLoadingFailed(ERR_NETWORK);
                        }
                    }
                }
                catch (JSONException ex) {
                    Log.e(TAG, "JSON parsing exception: " + ex.getMessage());
                    synchronized (ApiCallback.class) {
                        if (callback != null) {
                            callback.dataLoadingFailed(ERR_PARSING);
                        }
                    }
                }
            }
        });
    }



    private final static String TAG = ApiHelper.class.getSimpleName();
}

