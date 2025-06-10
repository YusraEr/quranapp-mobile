package com.example.quranapp.data.remote;

import com.example.quranapp.data.remote.model.SuratNavInfo; // Pastikan import ini ada
import com.example.quranapp.data.remote.model.SuratNavInfoDeserializer; // Pastikan import ini ada
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static final String BASE_URL = "https://equran.id/api/v2/";
    private static Retrofit retrofit = null;
    private static Gson customGsonInstance = null;
    private static Gson getCustomGson() {
        if (customGsonInstance == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(SuratNavInfo.class, new SuratNavInfoDeserializer());
            customGsonInstance = gsonBuilder.create();
        }
        return customGsonInstance;
    }

    public static QuranApiService getApiService() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(getCustomGson()))
                    .build();
        }
        return retrofit.create(QuranApiService.class);
    }
}
