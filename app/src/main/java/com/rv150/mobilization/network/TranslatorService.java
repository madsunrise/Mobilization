package com.rv150.mobilization.network;

import android.util.Log;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.rv150.mobilization.utils.UiThread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ivan on 30.03.17.
 */

public class TranslatorService {
    private static final TranslatorService instance = new TranslatorService();

    private TranslatorService() {}

    private final YandexApiService gitHubService = YandexApiService.retrofit.create(YandexApiService.class);


    private static final String API_KEY = "trnsl.1.1.20170330T065607Z.dc9520b57e28c5f3.d470142e2a9021eb88919a66af16cf82457f17f0";


    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    private ApiCallback callback;

    public static final int ERR_NETWORK = 0;
    public static final int UNKNOWN_ERROR = 1;

    public static TranslatorService getInstance() {
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


    private final LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    Call<TranslateResponse> call = gitHubService.getTranslate(API_KEY, key, "en-ru");
                    Response<TranslateResponse> response = call.execute();
                    if (!response.isSuccessful()) {
                        return null;
                    }
                    TranslateResponse result = response.body();
                    if (!result.getText().isEmpty()) {
                        return result.getText().get(0);
                    }
                    else {
                        return null;
                    }
                }
            });



    private void handleResult(String result) {
        if (callback != null) {
            callback.onDataLoaded(result);
        }
    }


    public void requestTranslate(final String query) {

        String result = cache.getIfPresent(query);
        if (result != null) {
            callback.onDataLoaded(result);  // Cache hit
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String result = cache.get(query);
                    if (result != null) {
                        UiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                handleResult(result);
                            }
                        });
                    }
                } catch (ExecutionException ex) {
                    Log.e(TAG, "Failed to load translate: " + ex.getMessage());
                }
            }
        });
    }


    private final static String TAG = TranslatorService.class.getSimpleName();
}

