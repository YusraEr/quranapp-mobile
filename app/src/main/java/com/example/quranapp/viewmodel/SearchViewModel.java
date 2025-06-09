package com.example.quranapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quranapp.data.QuranRepository;
import com.example.quranapp.data.remote.model.AyatSearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final QuranRepository repository;
    private final MutableLiveData<List<AyatSearchResult>> _searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    // LiveData yang akan diobservasi oleh UI (tidak bisa diubah dari luar)
    public final LiveData<List<AyatSearchResult>> searchResults = _searchResults;
    public final LiveData<Boolean> isLoading = _isLoading;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = new QuranRepository(application);
    }

    public void searchAyats(String keyword) {
        if (keyword == null || keyword.trim().length() < 3) {
            _searchResults.setValue(new ArrayList<>());
            return;
        }
        _isLoading.setValue(true);
        // Repository akan mem-post hasil ke LiveData yang sama
        repository.searchAyats(keyword.trim(), _searchResults);
    }

    // --- METODE BARU UNTUK DIPANGGIL DARI ACTIVITY ---
    public void searchCompleted() {
        _isLoading.setValue(false);
    }   
}