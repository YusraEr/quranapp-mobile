package com.example.quranapp.data;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.Html; // Import Html
import android.text.Spanned; // Import Spanned
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quranapp.data.local.QuranDbHelper;
import com.example.quranapp.data.remote.QuranApiService;
import com.example.quranapp.data.remote.RetrofitClient;
import com.example.quranapp.data.remote.model.Ayat;
import com.example.quranapp.data.remote.model.AyatResponse;
import com.example.quranapp.data.remote.model.Surah;
import com.example.quranapp.data.remote.model.SurahResponse;
import com.example.quranapp.data.remote.model.TafsirResponse;
import com.example.quranapp.utils.NetworkUtils;
import com.example.quranapp.utils.SettingsUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuranRepository {
    private static final String TAG = "QuranRepository";

    private QuranDbHelper dbHelper;
    private QuranApiService quranApiService;
    private ExecutorService executorService;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private Application application;
    private MutableLiveData<String> fullAudioUrlLiveData = new MutableLiveData<>();
    private MutableLiveData<String> fullAudioErrorLiveData = new MutableLiveData<>();

    private MutableLiveData<List<Surah>> allSurahsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingSurah = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessageSurah = new MutableLiveData<>();

    private MutableLiveData<List<Ayat>> ayatsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingAyat = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessageAyat = new MutableLiveData<>();
    private MutableLiveData<AyatResponse.AyatData> surahDetailLiveData = new MutableLiveData<>();

    private MutableLiveData<TafsirResponse.TafsirData> tafsirLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingTafsir = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessageTafsir = new MutableLiveData<>();
    public LiveData<String> getFullAudioUrlLiveData() { return fullAudioUrlLiveData; }
    public LiveData<String> getFullAudioErrorLiveData() { return fullAudioErrorLiveData; }


    public QuranRepository(Application application) {
        this.application = application;
        this.dbHelper = new QuranDbHelper(application);
        this.quranApiService = RetrofitClient.getApiService();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Helper method untuk memproses teksLatin menjadi Spanned
    private void processAyatListSpannedText(List<Ayat> ayatList) {
        if (ayatList != null) {
            for (Ayat ayat : ayatList) {
                if (ayat.getTeksLatin() != null && !ayat.getTeksLatin().isEmpty()) {
                    // Lakukan parsing Html.fromHtml di sini (background thread)
                    Spanned spanned = Html.fromHtml(ayat.getTeksLatin(), Html.FROM_HTML_MODE_LEGACY);
                    ayat.setTeksLatinSpanned(spanned);
                }
            }
        }
    }
    public void fetchFullAudioUrlForSurah(int nomorSurah) {
        if (!NetworkUtils.isNetworkAvailable(application)) {
            fullAudioErrorLiveData.postValue("Tidak ada koneksi internet.");
            return;
        }

        // Reset error sebelumnya
        fullAudioErrorLiveData.postValue(null);

        quranApiService.getDetailSurah(nomorSurah).enqueue(new Callback<AyatResponse>() {
            @Override
            public void onResponse(Call<AyatResponse> call, Response<AyatResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    AyatResponse.AyatData data = response.body().getData();
                    if (data.getAudioFull() != null) {
                        // Gunakan reciter default atau yang tersimpan untuk mendapatkan URL
                        String reciterKey = SettingsUtils.getReciterPreference(application);
                        String audioUrl = data.getAudioFull().getAudioUrlForReciter(reciterKey);
                        if (audioUrl != null && !audioUrl.isEmpty()) {
                            fullAudioUrlLiveData.postValue(audioUrl);
                        } else {
                            fullAudioErrorLiveData.postValue("Audio tidak tersedia untuk qari ini.");
                        }
                    } else {
                        fullAudioErrorLiveData.postValue("Data audio tidak ditemukan.");
                    }
                } else {
                    fullAudioErrorLiveData.postValue("Gagal memuat data audio. Kode: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AyatResponse> call, Throwable t) {
                fullAudioErrorLiveData.postValue("Kesalahan jaringan: " + t.getMessage());
            }
        });
    }
    // --- LiveData Getters (tidak berubah) ---
    public LiveData<List<Surah>> getAllSurahsLiveData() { return allSurahsLiveData; }
    public LiveData<Boolean> getIsLoadingSurah() { return isLoadingSurah; }
    public LiveData<String> getErrorMessageSurah() { return errorMessageSurah; }
    public LiveData<List<Ayat>> getAyatsLiveData() { return ayatsLiveData; }
    public LiveData<AyatResponse.AyatData> getSurahDetailLiveData() { return surahDetailLiveData; }
    public LiveData<Boolean> getIsLoadingAyat() { return isLoadingAyat; }
    public LiveData<String> getErrorMessageAyat() { return errorMessageAyat; }
    public LiveData<TafsirResponse.TafsirData> getTafsirLiveData() { return tafsirLiveData; }
    public LiveData<Boolean> getIsLoadingTafsir() { return isLoadingTafsir; }
    public LiveData<String> getErrorMessageTafsir() { return errorMessageTafsir; }


    // --- Operasi Surah (tidak berubah signifikan, hanya memastikan konsistensi) ---
    public void loadAllSurahs(boolean forceRefresh) {
        isLoadingSurah.postValue(true);
        errorMessageSurah.postValue(null);

        // Mencegah loading tanpa batas dengan timeout
        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        Runnable timeoutRunnable = () -> {
            // Jika setelah 30 detik masih loading, anggap error
            if (Boolean.TRUE.equals(isLoadingSurah.getValue())) {
                Log.e(TAG, "Loading surah timeout after 30 seconds");
                errorMessageSurah.postValue("Permintaan data timeout. Coba lagi nanti.");
                isLoadingSurah.postValue(false);
            }
        };
        // Set timeout 30 detik
        timeoutHandler.postDelayed(timeoutRunnable, 30000);

        executorService.execute(() -> {
            try {
                List<Surah> surahsFromDb = null;
                if (!forceRefresh) surahsFromDb = dbHelper.getAllSurahs();
                if (surahsFromDb != null && !surahsFromDb.isEmpty() && !forceRefresh) {
                    allSurahsLiveData.postValue(surahsFromDb);
                    isLoadingSurah.postValue(false);
                    timeoutHandler.removeCallbacks(timeoutRunnable); // Batalkan timeout
                } else {
                    fetchAllSurahsFromApi(timeoutHandler, timeoutRunnable);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadAllSurahs", e);
                errorMessageSurah.postValue("Terjadi kesalahan saat memuat data: " + e.getMessage());
                isLoadingSurah.postValue(false);
                timeoutHandler.removeCallbacks(timeoutRunnable); // Batalkan timeout
                loadFallbackSurahsFromDb("Terjadi kesalahan: ");
            }
        });
    }

    private void fetchAllSurahsFromApi(Handler timeoutHandler, Runnable timeoutRunnable) {
        if (!NetworkUtils.isNetworkAvailable(application)) {
            mainThreadHandler.post(() -> {
                loadFallbackSurahsFromDb("Tidak ada koneksi internet. Menampilkan data offline.");
                isLoadingSurah.setValue(false);
                timeoutHandler.removeCallbacks(timeoutRunnable); // Batalkan timeout
            });
            return;
        }

        quranApiService.getAllSurah().enqueue(new Callback<SurahResponse>() {
            @Override
            public void onResponse(Call<SurahResponse> call, Response<SurahResponse> response) {
                timeoutHandler.removeCallbacks(timeoutRunnable); // Batalkan timeout karena respons sudah diterima

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Surah> surahsFromApi = response.body().getData();
                    executorService.execute(() -> {
                        try {
                            dbHelper.addOrReplaceSurahs(surahsFromApi);
                            mainThreadHandler.post(() -> {
                                allSurahsLiveData.setValue(surahsFromApi);
                                isLoadingSurah.setValue(false);
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "Error saving surahs to database", e);
                            mainThreadHandler.post(() -> {
                                allSurahsLiveData.setValue(surahsFromApi); // Tetap set data dari API
                                errorMessageSurah.setValue("Data berhasil dimuat, tapi gagal disimpan offline: " + e.getMessage());
                                isLoadingSurah.setValue(false);
                            });
                        }
                    });
                } else {
                    mainThreadHandler.post(() -> {
                        errorMessageSurah.setValue("Gagal mengambil data surah. Kode: " + response.code());
                        isLoadingSurah.setValue(false);
                        loadFallbackSurahsFromDb("Gagal mengambil data surah. Menampilkan data offline jika ada.");
                    });
                }
            }
            @Override
            public void onFailure(Call<SurahResponse> call, Throwable t) {
                timeoutHandler.removeCallbacks(timeoutRunnable); // Batalkan timeout karena sudah gagal

                mainThreadHandler.post(() -> {
                    errorMessageSurah.setValue("Kesalahan jaringan: " + t.getMessage());
                    isLoadingSurah.setValue(false);
                    loadFallbackSurahsFromDb("Kesalahan jaringan. Menampilkan data offline jika ada.");
                });
            }
        });
    }

    private void loadFallbackSurahsFromDb(String messagePrefix) {
        executorService.execute(() -> {
            List<Surah> surahsFromDb = dbHelper.getAllSurahs();
            if (surahsFromDb != null && !surahsFromDb.isEmpty()) {
                mainThreadHandler.post(() -> {
                    allSurahsLiveData.setValue(surahsFromDb);
                    if (errorMessageSurah.getValue() == null || errorMessageSurah.getValue().isEmpty()) {
                        errorMessageSurah.setValue(messagePrefix);
                    }
                });
            } else {
                mainThreadHandler.post(() -> {
                    if (errorMessageSurah.getValue() == null || errorMessageSurah.getValue().isEmpty()) {
                        errorMessageSurah.setValue(messagePrefix + " Tidak ada data surah offline.");
                    }
                });
            }
        });
    }

    // --- Operasi Ayat (MODIFIKASI DI SINI) ---
    public void loadAyatsBySurahNumber(int nomorSurah, boolean forceRefresh) {
        isLoadingAyat.postValue(true);
        errorMessageAyat.postValue(null);

        executorService.execute(() -> {
            if (!forceRefresh) {
                List<Ayat> ayatsFromDb = dbHelper.getAyatsBySurahNumber(nomorSurah);
                Surah surahDetailFromDb = dbHelper.getSurahByNomor(nomorSurah);

                if (ayatsFromDb != null && !ayatsFromDb.isEmpty() && surahDetailFromDb != null) {
                    Log.d(TAG, "Ayats and Surah Detail for " + nomorSurah + " loaded from DB");
                    processAyatListSpannedText(ayatsFromDb);
                    ayatsLiveData.postValue(ayatsFromDb);

                    // Buat objek AyatData secara manual dari Surah yang sudah ada di DB
                    AyatResponse.AyatData offlineAyatData = new AyatResponse.AyatData();
                    offlineAyatData.setNomor(surahDetailFromDb.getNomor());
                    offlineAyatData.setNama(surahDetailFromDb.getNama());
                    offlineAyatData.setNamaLatin(surahDetailFromDb.getNamaLatin());
                    offlineAyatData.setJumlahAyat(surahDetailFromDb.getJumlahAyat());
                    offlineAyatData.setTempatTurun(surahDetailFromDb.getTempatTurun());
                    offlineAyatData.setArti(surahDetailFromDb.getArti());
                    offlineAyatData.setDeskripsi(surahDetailFromDb.getDeskripsi());

                    // Set info navigasi dari objek Surah yang diambil dari DB
                    offlineAyatData.setSuratSelanjutnya(surahDetailFromDb.getSuratSelanjutnya());
                    offlineAyatData.setSuratSebelumnya(surahDetailFromDb.getSuratSebelumnya());

                    surahDetailLiveData.postValue(offlineAyatData);
                    isLoadingAyat.postValue(false);

                    // Jika online, coba perbarui info navigasi di latar belakang
                    if (NetworkUtils.isNetworkAvailable(application)) {
                        fetchDetailSurahFromApiForInfo(nomorSurah);
                    }
                    return;
                }
            }
            fetchAyatsAndDetailFromApi(nomorSurah);
        });
    }

    private void fetchAyatsAndDetailFromApi(int nomorSurah) {
        if (!NetworkUtils.isNetworkAvailable(application)) {
            mainThreadHandler.post(() -> {
                List<Ayat> ayatsFromDb = dbHelper.getAyatsBySurahNumber(nomorSurah);
                if (ayatsFromDb != null && !ayatsFromDb.isEmpty()) {
                    processAyatListSpannedText(ayatsFromDb); // Proses juga jika fallback ke DB
                    ayatsLiveData.setValue(ayatsFromDb);
                    errorMessageAyat.setValue("Tidak ada koneksi internet. Menampilkan data ayat offline.");
                } else {
                    errorMessageAyat.setValue("Tidak ada koneksi internet dan tidak ada data ayat offline.");
                }
                isLoadingAyat.setValue(false);
            });
            return;
        }
        isLoadingAyat.postValue(true);
        errorMessageAyat.postValue(null);

        quranApiService.getDetailSurah(nomorSurah).enqueue(new Callback<AyatResponse>() {
            @Override
            public void onResponse(Call<AyatResponse> call, Response<AyatResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    AyatResponse.AyatData ayatDataFromApi = response.body().getData();
                    List<Ayat> ayatsFromApi = ayatDataFromApi.getAyat();

                    if (ayatsFromApi != null) {
                        processAyatListSpannedText(ayatsFromApi); // Proses Spanned text dari API
                        executorService.execute(() -> {
                            dbHelper.addOrReplaceAyatsForSurah(nomorSurah, ayatsFromApi);
                            // Ambil lagi dari DB untuk konsistensi (meskipun ayatsFromApi sudah diproses)
                            // atau langsung gunakan ayatsFromApi yang sudah diproses.
                            // Untuk menghindari double processing, kita bisa gunakan ayatsFromApi langsung
                            // jika kita yakin addOrReplaceAyatsForSurah tidak mengubahnya.
                            // Atau, panggil getAyatsBySurahNumber dan processAyatListSpannedText lagi.
                            // Lebih sederhana: gunakan ayatsFromApi yang sudah diproses.
                            Surah surahToUpdate = dbHelper.getSurahByNomor(nomorSurah);
                            if (surahToUpdate != null) {
                                surahToUpdate.setSuratSelanjutnya(ayatDataFromApi.getSuratSelanjutnya());
                                surahToUpdate.setSuratSebelumnya(ayatDataFromApi.getSuratSebelumnya());
                                // Gunakan addOrReplaceSurahs dengan list berisi satu item untuk update
                                dbHelper.addOrReplaceSurahs(Collections.singletonList(surahToUpdate));
                            }

                            mainThreadHandler.post(() -> {
                                ayatsLiveData.setValue(ayatsFromApi);
                                surahDetailLiveData.setValue(ayatDataFromApi);
                                isLoadingAyat.setValue(false);
                            });
                        });
                    } else {
                        mainThreadHandler.post(() -> {
                            errorMessageAyat.setValue("Data ayat tidak ditemukan di server untuk surah ini.");
                            isLoadingAyat.setValue(false);
                            loadFallbackAyatsFromDb(nomorSurah, "Data ayat tidak ditemukan. Menampilkan data offline jika ada.");
                        });
                    }
                } else {
                    mainThreadHandler.post(() -> {
                        errorMessageAyat.setValue("Gagal mengambil data ayat. Kode: " + response.code());
                        isLoadingAyat.setValue(false);
                        loadFallbackAyatsFromDb(nomorSurah, "Gagal mengambil data ayat. Menampilkan data offline jika ada.");
                    });
                }
            }
            @Override
            public void onFailure(Call<AyatResponse> call, Throwable t) {
                mainThreadHandler.post(() -> {
                    errorMessageAyat.setValue("Kesalahan jaringan saat mengambil ayat: " + t.getMessage());
                    isLoadingAyat.setValue(false);
                    loadFallbackAyatsFromDb(nomorSurah, "Kesalahan jaringan. Menampilkan data offline jika ada.");
                });
            }
        });
    }

    private void fetchDetailSurahFromApiForInfo(int nomorSurah) {
        quranApiService.getDetailSurah(nomorSurah).enqueue(new Callback<AyatResponse>() {
            @Override
            public void onResponse(Call<AyatResponse> call, Response<AyatResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    AyatResponse.AyatData dataFromApi = response.body().getData();
                    surahDetailLiveData.postValue(dataFromApi);

                    executorService.execute(() -> {
                        Surah surahToUpdate = dbHelper.getSurahByNomor(nomorSurah);
                        if (surahToUpdate != null) {
                            surahToUpdate.setSuratSelanjutnya(dataFromApi.getSuratSelanjutnya());
                            surahToUpdate.setSuratSebelumnya(dataFromApi.getSuratSebelumnya());
                            dbHelper.addOrReplaceSurahs(Collections.singletonList(surahToUpdate));
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<AyatResponse> call, Throwable t) {
                // Failure handling is already implemented in fetchAyatsAndDetailFromApi
            }
        });
    }

    private void loadFallbackAyatsFromDb(int nomorSurah, String messagePrefix) {
        executorService.execute(() -> {
            List<Ayat> ayatsFromDb = dbHelper.getAyatsBySurahNumber(nomorSurah);
            if (ayatsFromDb != null && !ayatsFromDb.isEmpty()) {
                processAyatListSpannedText(ayatsFromDb); // Proses Spanned text
                mainThreadHandler.post(() -> {
                    ayatsLiveData.setValue(ayatsFromDb);
                    if (errorMessageAyat.getValue() == null || errorMessageAyat.getValue().isEmpty()) {
                        errorMessageAyat.setValue(messagePrefix);
                    }
                });
            } else {
                mainThreadHandler.post(() -> {
                    if (errorMessageAyat.getValue() == null || errorMessageAyat.getValue().isEmpty()) {
                        errorMessageAyat.setValue(messagePrefix + " Tidak ada data offline.");
                    }
                });
            }
        });
    }

    // --- Operasi Tafsir (Sama seperti sebelumnya) ---
    public void loadTafsirBySurahNumber(int nomorSurah) {
        isLoadingTafsir.postValue(true);
        errorMessageTafsir.postValue(null);

        if (!NetworkUtils.isNetworkAvailable(application)) {
            errorMessageTafsir.postValue("Tidak ada koneksi internet untuk mengambil data tafsir.");
            isLoadingTafsir.postValue(false);
            // Di sini Anda bisa menambahkan logika untuk memuat tafsir dari DB jika disimpan
            return;
        }

        quranApiService.getTafsirSurah(nomorSurah).enqueue(new Callback<TafsirResponse>() {
            @Override
            public void onResponse(Call<TafsirResponse> call, Response<TafsirResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    tafsirLiveData.postValue(response.body().getData());
                    // Di sini Anda bisa menyimpan tafsir ke DB jika diperlukan
                } else {
                    errorMessageTafsir.postValue("Gagal mengambil data tafsir. Kode: " + response.code());
                }
                isLoadingTafsir.postValue(false);
            }

            @Override
            public void onFailure(Call<TafsirResponse> call, Throwable t) {
                errorMessageTafsir.postValue("Kesalahan jaringan saat mengambil tafsir: " + t.getMessage());
                isLoadingTafsir.postValue(false);
            }
        });
    }
}
