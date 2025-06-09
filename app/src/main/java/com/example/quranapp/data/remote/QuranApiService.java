package com.example.quranapp.data.remote; // Sesuaikan dengan package name Anda

import com.example.quranapp.data.remote.model.AyatResponse;
import com.example.quranapp.data.remote.model.SurahResponse;
import com.example.quranapp.data.remote.model.TafsirResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuranApiService {

    @GET("surat")
    Call<SurahResponse> getAllSurah();

    @GET("surat/{nomor_surah}")
    Call<AyatResponse> getDetailSurah(@Path("nomor_surah") int nomorSurah);

    @GET("tafsir/{nomor_surah}")
    Call<TafsirResponse> getTafsirSurah(@Path("nomor_surah") int nomorSurah);

}
