package com.example.quranapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quranapp.data.remote.model.Surah;
import com.example.quranapp.data.QuranRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SurahViewModel extends AndroidViewModel {

    private QuranRepository quranRepository;

    private LiveData<List<Surah>> allSurahsLiveData;
    private LiveData<Boolean> isLoadingSurah;
    private LiveData<String> errorMessageSurah;

    // LiveData baru untuk audio surah penuh
    private LiveData<String> fullAudioUrlLiveData;
    private LiveData<String> fullAudioErrorLiveData;
    private List<Surah> masterSurahList = new ArrayList<>();
    private final MutableLiveData<List<Surah>> _filteredSurahs = new MutableLiveData<>();
    public final LiveData<List<Surah>> filteredSurahs = _filteredSurahs;

    public SurahViewModel(@NonNull Application application) {
        super(application);
        quranRepository = new QuranRepository(application);

        allSurahsLiveData = quranRepository.getAllSurahsLiveData();
        isLoadingSurah = quranRepository.getIsLoadingSurah();
        errorMessageSurah = quranRepository.getErrorMessageSurah();

        // Hubungkan LiveData baru
        fullAudioUrlLiveData = quranRepository.getFullAudioUrlLiveData();
        fullAudioErrorLiveData = quranRepository.getFullAudioErrorLiveData();

        allSurahsLiveData.observeForever(surahs -> {
            if (surahs != null) {
                masterSurahList = new ArrayList<>(surahs);
                _filteredSurahs.postValue(masterSurahList);
            }
        });
    }

    public void filterSurahs(String query) {
        if (query == null || query.trim().isEmpty()) {
            _filteredSurahs.postValue(masterSurahList);
            return;
        }

        List<Surah> filteredList = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase(Locale.getDefault()).trim();

        for (Surah surah : masterSurahList) {
            if (surah.getNamaLatin().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
                    surah.getArti().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
                    String.valueOf(surah.getNomor()).equals(lowerCaseQuery)) {
                filteredList.add(surah);
            }
        }
        _filteredSurahs.postValue(filteredList);
    }
    public LiveData<List<Surah>> getAllSurahs() {
        return allSurahsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingSurah;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageSurah;
    }

    public LiveData<String> getFullAudioUrl() {
        return fullAudioUrlLiveData;
    }

    public LiveData<String> getFullAudioError() {
        return fullAudioErrorLiveData;
    }

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
