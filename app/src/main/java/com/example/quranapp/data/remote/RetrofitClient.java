package com.example.quranapp.data.remote; // Sesuaikan dengan package name aplikasi Anda

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
    private static Gson customGsonInstance = null; // Variabel untuk menyimpan instance Gson kustom

    // Metode untuk membuat atau mendapatkan instance Gson yang sudah dikonfigurasi
    private static Gson getCustomGson() {
        if (customGsonInstance == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            // Daftarkan TypeAdapter (Deserializer) untuk kelas SuratNavInfo.
            // Ini akan memberitahu Gson untuk menggunakan deserializer kustom kita
            // setiap kali ia perlu mengubah JSON menjadi objek SuratNavInfo.
            gsonBuilder.registerTypeAdapter(SuratNavInfo.class, new SuratNavInfoDeserializer());
            customGsonInstance = gsonBuilder.create();
        }
        return customGsonInstance;
    }

    public static QuranApiService getApiService() {
        if (retrofit == null) {
            // Konfigurasi HttpLoggingInterceptor untuk logging request dan response.
            // Ini sangat berguna untuk debugging.
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            // Set level logging. Gunakan Level.BODY untuk melihat detail lengkap request/response.
            // PENTING: Ganti ke HttpLoggingInterceptor.Level.NONE untuk rilis produksi
            // agar tidak membocorkan data sensitif di log.
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Konfigurasi OkHttpClient dengan interceptor logging dan timeout.
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor) // Tambahkan logging interceptor
                    .connectTimeout(30, TimeUnit.SECONDS) // Atur timeout untuk koneksi
                    .readTimeout(30, TimeUnit.SECONDS)    // Atur timeout untuk membaca data
                    .writeTimeout(30, TimeUnit.SECONDS)   // Atur timeout untuk menulis data
                    .build();

            // Membuat instance Retrofit.
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Tetapkan base URL untuk semua request API
                    .client(okHttpClient) // Gunakan OkHttpClient yang sudah dikonfigurasi
                    // Gunakan GsonConverterFactory dengan instance Gson kustom kita.
                    // Ini memastikan deserializer kustom kita akan digunakan.
                    .addConverterFactory(GsonConverterFactory.create(getCustomGson()))
                    .build();
        }
        // Buat dan kembalikan implementasi dari interface QuranApiService.
        return retrofit.create(QuranApiService.class);
    }
}
