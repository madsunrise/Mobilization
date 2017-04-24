package com.rv150.mobilization.network;

import com.rv150.mobilization.model.TranslateResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ivan on 10.04.17.
 */

interface YandexApiService {

    String API_KEY = "trnsl.1.1.20170330T065607Z.dc9520b57e28c5f3.d470142e2a9021eb88919a66af16cf82457f17f0";
    String BASE_URL = "https://translate.yandex.net";

    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/translate")
    Call<TranslateResponse> getTranslate(
            @Query("key") String key,
            @Query("lang") String lang,
            @Field("text") String text);


    @GET("/api/v1.5/tr.json/getLangs")
    Call<SupportedLanguages> getSupLangs(
            @Query("key") String key,
            @Query("ui") String ui);



    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build();
}
