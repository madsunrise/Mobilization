package com.rv150.mobilization.network;

import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.rv150.mobilization.model.TranslateRequest;
import com.rv150.mobilization.utils.UiThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ivan on 30.03.17.
 */

public class TranslatorService {
    private static final TranslatorService instance = new TranslatorService();

    private TranslatorService() {}

    private final YandexApiService api = YandexApiService.retrofit.create(YandexApiService.class);
    private static final String API_KEY = "trnsl.1.1.20170330T065607Z.dc9520b57e28c5f3.d470142e2a9021eb88919a66af16cf82457f17f0";


    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TranslateCallback callback;

    public static final int ERR_NETWORK = 0;
    public static final int UNKNOWN_ERROR = 1;

    public static TranslatorService getInstance() {
        return instance;
    }

    private boolean active = false;
    private boolean dirty = false;

    public interface TranslateCallback {
        void onDataLoaded(String result, boolean nextRequest);
        TranslateRequest getFreshData();
        void dataLoadingFailed(int errCode);
        void supLanguagesLoaded(Map<String, String> langs);
    }

    public void setCallback(TranslateCallback callback) {
        this.callback = callback;
    }


    private final LoadingCache<TranslateRequest, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<TranslateRequest, String>() {
                @Override
                public String load(TranslateRequest key) throws Exception {
                    String from = key.getFrom();
                    String to = key.getTo();
                    String text = key.getText();
                    Call<TranslateResponse> call = api.getTranslate(API_KEY, from + '-' + to, text);
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




    public void requestTranslate() {
        final TranslateRequest request = callback.getFreshData();
        String result = cache.getIfPresent(request);
        if (result != null) {
            Log.d(TAG, "Getting value from cache!");
            callback.onDataLoaded(result, false);
            return;
        }
        makeNetworkRequest(request);
    }

    private synchronized void makeNetworkRequest(TranslateRequest input) {
        if (active) {
            dirty = true;
            return;
        }
        active = true;
        runAsyncTask(input);
    }


    private void runAsyncTask(final TranslateRequest input) {
        Log.d(TAG, "Running async request...");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String result = cache.get(input);
                    onRequestFinished(result);
                } catch (Exception ex) {
                    //TODO Cache returns null
                    Log.e(TAG, "Failed to load translate: " + ex.getMessage());
                }
            }
        });
    }

    private synchronized void onRequestFinished(String result) {
        if (dirty) {
            dirty = false;
            notifyActivity(result, true);
            return;
        }
        active = false;
        notifyActivity(result, false);
    }



    private void notifyActivity(final String result, final boolean nextRequest) {
        UiThread.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onDataLoaded(result, false);
                    if (nextRequest) {
                        runAsyncTask(callback.getFreshData());
                    }
                }
            }
        });
    }

    public void getSupportedLanguages(final String ui) {
        Call<SupportedLanguages> call = api.getSupLangs(API_KEY, ui);
        call.enqueue(new Callback<SupportedLanguages>() {
            @Override
            public void onResponse(Call<SupportedLanguages> call, Response<SupportedLanguages> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Supported languages loaded");
                    Map<String, String> map = response.body().getLangs();
                    if (callback != null) {
                        callback.supLanguagesLoaded(map);
                    }
                }
                else {
                    Log.e(TAG, "Getting supported languages failed!");
                }
            }

            @Override
            public void onFailure(Call<SupportedLanguages> call, Throwable t) {
                Log.e(TAG, "Getting supported languages failed!");
            }
        });
    }


    private final static String TAG = TranslatorService.class.getSimpleName();
}

