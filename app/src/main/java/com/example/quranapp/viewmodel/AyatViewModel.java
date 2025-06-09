package com.example.quranapp.viewmodel; // Ganti dengan package name aplikasi Anda

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quranapp.data.QuranRepository;
import com.example.quranapp.data.remote.model.Ayat;
import com.example.quranapp.data.remote.model.AyatResponse; // Untuk info detail surah
import com.example.quranapp.data.remote.model.TafsirResponse; // Untuk data tafsir

import java.util.List;

public class AyatViewModel extends AndroidViewModel {

    private QuranRepository quranRepository;
    private int currentSurahNumber = -1;

    // LiveData untuk daftar Ayat
    private LiveData<List<Ayat>> ayatsLiveData;
    private LiveData<AyatResponse.AyatData> surahDetailLiveData; // Info surah (nama, arti, dll)
    private LiveData<Boolean> isLoadingAyat;
    private LiveData<String> errorMessageAyat;

    // LiveData untuk Tafsir
    private LiveData<TafsirResponse.TafsirData> tafsirLiveData;
    private LiveData<Boolean> isLoadingTafsir;
    private LiveData<String> errorMessageTafsir;


    public AyatViewModel(@NonNull Application application) {
        super(application);
        quranRepository = new QuranRepository(application);

        // Mendapatkan LiveData dari Repository
        ayatsLiveData = quranRepository.getAyatsLiveData();
        surahDetailLiveData = quranRepository.getSurahDetailLiveData();
        isLoadingAyat = quranRepository.getIsLoadingAyat();
        errorMessageAyat = quranRepository.getErrorMessageAyat();

        tafsirLiveData = quranRepository.getTafsirLiveData();
        isLoadingTafsir = quranRepository.getIsLoadingTafsir();
        errorMessageTafsir = quranRepository.getErrorMessageTafsir();
    }

    // --- Getter untuk LiveData Ayat dan Detail Surah ---
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

    // --- Getter untuk LiveData Tafsir ---
    public LiveData<TafsirResponse.TafsirData> getTafsir() {
        return tafsirLiveData;
    }

    public LiveData<Boolean> getIsLoadingTafsir() {
        return isLoadingTafsir;
    }

    public LiveData<String> getErrorMessageTafsir() {
        return errorMessageTafsir;
    }

    /**
     * Meminta repository untuk memuat atau me-refresh data ayat untuk surah tertentu.
     * @param nomorSurah Nomor surah yang ayatnya ingin dimuat.
     * @param forceRefresh true jika ingin paksa ambil dari API.
     */
    public void loadAyatsForSurah(int nomorSurah, boolean forceRefresh) {
        this.currentSurahNumber = nomorSurah;
        quranRepository.loadAyatsBySurahNumber(nomorSurah, forceRefresh);
    }

    public void refreshAyats() {
        if (currentSurahNumber != -1) {
            quranRepository.loadAyatsBySurahNumber(currentSurahNumber, true); // Selalu paksa refresh
        }
    }

    public void initialLoadAyats(int nomorSurah) {
        this.currentSurahNumber = nomorSurah;
        // Cek apakah data sudah ada di LiveData untuk surah ini, jika tidak, muat.
        // Parameter 'false' berarti tidak memaksa refresh jika data sudah ada di DB.
        // Logika ini bisa lebih kompleks tergantung bagaimana Anda ingin menangani cache per surah.
        // Untuk sederhana, kita panggil loadAyatsForSurah dengan forceRefresh false.
        quranRepository.loadAyatsBySurahNumber(nomorSurah, false);
    }


    /**
     * Meminta repository untuk memuat data tafsir untuk surah tertentu.
     * @param nomorSurah Nomor surah yang tafsirnya ingin dimuat.
     */
    public void loadTafsirForSurah(int nomorSurah) {
        // Jika tafsir sudah dimuat untuk surah ini, mungkin tidak perlu load ulang kecuali diminta
        // if (tafsirLiveData.getValue() != null && tafsirLiveData.getValue().getNomor() == nomorSurah && !forceRefresh) {
        //     return;
        // }
        quranRepository.loadTafsirBySurahNumber(nomorSurah);
    }

    public int getCurrentSurahNumber() {
        return currentSurahNumber;
    }
}
