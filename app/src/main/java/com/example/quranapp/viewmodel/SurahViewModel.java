package com.example.quranapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.quranapp.data.remote.model.Surah;
import com.example.quranapp.data.QuranRepository;

import java.util.List;

public class SurahViewModel extends AndroidViewModel {

    private QuranRepository quranRepository;

    private LiveData<List<Surah>> allSurahsLiveData;
    private LiveData<Boolean> isLoadingSurah;
    private LiveData<String> errorMessageSurah;

    // LiveData baru untuk audio surah penuh
    private LiveData<String> fullAudioUrlLiveData;
    private LiveData<String> fullAudioErrorLiveData;

    public SurahViewModel(@NonNull Application application) {
        super(application);
        quranRepository = new QuranRepository(application);

        allSurahsLiveData = quranRepository.getAllSurahsLiveData();
        isLoadingSurah = quranRepository.getIsLoadingSurah();
        errorMessageSurah = quranRepository.getErrorMessageSurah();

        // Hubungkan LiveData baru
        fullAudioUrlLiveData = quranRepository.getFullAudioUrlLiveData();
        fullAudioErrorLiveData = quranRepository.getFullAudioErrorLiveData();
    }

    // --- Getter untuk LiveData ---
    public LiveData<List<Surah>> getAllSurahs() { return allSurahsLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoadingSurah; }
    public LiveData<String> getErrorMessage() { return errorMessageSurah; }
    public LiveData<String> getFullAudioUrl() { return fullAudioUrlLiveData; }
    public LiveData<String> getFullAudioError() { return fullAudioErrorLiveData; }

    // --- Metode Aksi ---
    public void initialLoadSurahs() {
        if (allSurahsLiveData.getValue() == null || allSurahsLiveData.getValue().isEmpty()) {
            quranRepository.loadAllSurahs(false);
        }
    }

    public void refreshSurahs() {
        quranRepository.loadAllSurahs(true);
    }

    // Metode baru untuk mengambil URL audio
    public void fetchFullAudioUrl(int surahNumber) {
        quranRepository.fetchFullAudioUrlForSurah(surahNumber);
    }
}
