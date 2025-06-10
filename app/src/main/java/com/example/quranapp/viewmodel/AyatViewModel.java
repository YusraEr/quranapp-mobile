package com.example.quranapp.viewmodel; // Ganti dengan package name aplikasi Anda

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quranapp.data.QuranRepository;
import com.example.quranapp.data.remote.model.Ayat;
import com.example.quranapp.data.remote.model.AyatResponse;
import com.example.quranapp.data.remote.model.TafsirResponse;

import java.util.List;

public class AyatViewModel extends AndroidViewModel {

    private QuranRepository quranRepository;
    private int currentSurahNumber = -1;

    // LiveData untuk daftar Ayat
    private LiveData<List<Ayat>> ayatsLiveData;
    private LiveData<AyatResponse.AyatData> surahDetailLiveData;
    private LiveData<Boolean> isLoadingAyat;
    private LiveData<String> errorMessageAyat;

    // LiveData untuk Tafsir
    private LiveData<TafsirResponse.TafsirData> tafsirLiveData;
    private LiveData<Boolean> isLoadingTafsir;
    private LiveData<String> errorMessageTafsir;


    public AyatViewModel(@NonNull Application application) {
        super(application);
        quranRepository = new QuranRepository(application);

        ayatsLiveData = quranRepository.getAyatsLiveData();
        surahDetailLiveData = quranRepository.getSurahDetailLiveData();
        isLoadingAyat = quranRepository.getIsLoadingAyat();
        errorMessageAyat = quranRepository.getErrorMessageAyat();

        tafsirLiveData = quranRepository.getTafsirLiveData();
        isLoadingTafsir = quranRepository.getIsLoadingTafsir();
        errorMessageTafsir = quranRepository.getErrorMessageTafsir();
    }

    public LiveData<List<Ayat>> getAyats() {
        return ayatsLiveData;
    }

    public LiveData<AyatResponse.AyatData> getSurahDetail() {
        return surahDetailLiveData;
    }

    public LiveData<Boolean> getIsLoadingAyat() {
        return isLoadingAyat;
    }

    public LiveData<String> getErrorMessageAyat() {
        return errorMessageAyat;
    }

    public LiveData<TafsirResponse.TafsirData> getTafsir() {
        return tafsirLiveData;
    }

    public void refreshAyats() {
        if (currentSurahNumber != -1) {
            quranRepository.loadAyatsBySurahNumber(currentSurahNumber, true);
            quranRepository.loadTafsirBySurahNumber(currentSurahNumber);
        }
    }

    public void initialLoadAyats(int nomorSurah) {
        this.currentSurahNumber = nomorSurah;
        quranRepository.loadAyatsBySurahNumber(nomorSurah, false);
    }
    public void loadTafsirForSurah(int nomorSurah) {
        quranRepository.loadTafsirBySurahNumber(nomorSurah);
    }
}

