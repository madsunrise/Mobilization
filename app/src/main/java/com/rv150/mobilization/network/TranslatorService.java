package com.rv150.mobilization.network;

import android.util.Log;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.rv150.mobilization.model.TranslateRequest;
import com.rv150.mobilization.model.TranslateResponse;
import com.rv150.mobilization.model.Translation;
import com.rv150.mobilization.utils.UiThread;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rv150.mobilization.network.YandexApiService.API_KEY;

/**
 * Created by ivan on 30.03.17.
 */

public class TranslatorService {
    private static final TranslatorService instance = new TranslatorService();
    private final YandexApiService api = YandexApiService.retrofit.create(YandexApiService.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TranslateCallback callback;

    public static final int ERR_NETWORK = 0;

    private TranslatorService() {}
    public static TranslatorService getInstance() {
        return instance;
    }

    private boolean active = false;
    private boolean dirty = false;

    public interface TranslateCallback {
        void onDataLoaded(Translation result, boolean nextRequest);
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
                    String from = key.getFromCode();
                    String to = key.getToCode();
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
            callback.onDataLoaded(new Translation(request.getText(), result), false);
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
                    Translation translation = new Translation(input.getText(), result);
                    onRequestFinished(translation);
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to load translate: " + ex.getMessage());
                    onRequestFinished(null);
                }
            }
        });
    }

    private synchronized void onRequestFinished(Translation result) {
        if (dirty) {
            dirty = false;
            notifyActivity(result, true);
            return;
        }
        active = false;
        notifyActivity(result, false);
    }



    private void notifyActivity(final Translation result, final boolean nextRequest) {
        UiThread.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    if (result != null) {
                        callback.onDataLoaded(result, false);
                    }
                    else {
                        callback.dataLoadingFailed(ERR_NETWORK);
                    }
                    if (nextRequest) {
                        runAsyncTask(callback.getFreshData());
                    }
                }
            }
        });
    }



    public void requestSupportedLanguages(final String ui) {
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
                    if (callback != null) {
                        callback.dataLoadingFailed(ERR_NETWORK);
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportedLanguages> call, Throwable t) {
                Log.e(TAG, "Getting supported languages failed! " + t.getMessage());
                if (callback != null) {
                    callback.dataLoadingFailed(ERR_NETWORK);
                }
            }
        });
    }

    private final static String TAG = TranslatorService.class.getSimpleName();
}

