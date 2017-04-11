package com.rv150.mobilization.network;

import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private final YandexApiService gitHubService = YandexApiService.retrofit.create(YandexApiService.class);

    private Call<TranslateResponse> call = null;

    private static final String API_KEY = "trnsl.1.1.20170330T065607Z.dc9520b57e28c5f3.d470142e2a9021eb88919a66af16cf82457f17f0";



    private ApiCallback callback;

    public static final int ERR_NETWORK = 0;
    public static final int UNKNOWN_ERROR = 1;

    public static TranslatorService getInstance() {
        return instance;
    }

    public interface ApiCallback {
        void onDataLoaded(String result);
        void dataLoadingFailed(int errCode);
        void supLanguagesLoaded(List<String> langs);
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
                    return "";
                }
            });




    public void requestTranslate(final String query) {
        if (call != null && !call.isExecuted()) {
            call.cancel();                          // Отменяем предыдущий выполняющийся запрос
        }

        call = gitHubService.getTranslate(API_KEY, "en-ru", query);

        call.enqueue(new Callback<TranslateResponse>() {
            @Override
            public void onResponse(Call<TranslateResponse> call, Response<TranslateResponse> response) {
                synchronized (ApiCallback.class) {
                    if (callback == null) {
                        return;
                    }
                    if (response.isSuccessful()) {
                        TranslateResponse result = response.body();
                        if (!result.getText().isEmpty()) {
                            callback.onDataLoaded(result.getText().get(0));
                        }
                        else {
                            callback.dataLoadingFailed(UNKNOWN_ERROR);
                        }
                    } else {
                        callback.dataLoadingFailed(UNKNOWN_ERROR);
                    }
                }
            }
            @Override
            public void onFailure(Call<TranslateResponse> call, Throwable t) {
                synchronized (ApiCallback.class) {
                    if (callback != null) {
                        callback.dataLoadingFailed(ERR_NETWORK);
                    }
                }
            }
        });
    }

    public void getSupportedLanguages(final String ui) {
        Call<SupportedLanguages> call = gitHubService.getSupLangs(API_KEY, ui);
        call.enqueue(new Callback<SupportedLanguages>() {
            @Override
            public void onResponse(Call<SupportedLanguages> call, Response<SupportedLanguages> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Supported languages loaded");
                    Map<String, String> map = response.body().getLangs();

                    List<String> languages = new ArrayList<>(map.values());
                    Collections.sort(languages);
                    callback.supLanguagesLoaded(languages);
                }
                else {
                    Log.e(TAG, "Getting supported languages failed!");
                }
            }

            @Override
            public void onFailure(Call<SupportedLanguages> call, Throwable t) {

            }
        });
    }


    private final static String TAG = TranslatorService.class.getSimpleName();
}

