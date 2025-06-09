package com.example.quranapp.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quranapp.R;
import com.example.quranapp.adapter.AyatAdapter;
import com.example.quranapp.data.remote.model.Ayat;
import com.example.quranapp.data.remote.model.Tafsir;
import com.example.quranapp.utils.SettingsUtils;
import com.example.quranapp.viewmodel.AyatViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AyatListFragment extends Fragment {

    private static final String ARG_SURAH_NUMBER = "arg_surah_number";
    private static final String ARG_SURAH_NAME_LATIN = "arg_surah_name_latin";

    private int surahNumber;
    private String surahNameLatin;

    private AyatViewModel ayatViewModel;
    private RecyclerView recyclerViewAyats;
    private AyatAdapter ayatAdapter;
    private ProgressBar progressBarAyat;
    private TextView textViewErrorAyat;
    private Button buttonRefreshAyat;
    private TextView textViewSurahInfo;

    private MediaPlayer mediaPlayer;

    public AyatListFragment() {
        // Required empty public constructor
    }

    public static AyatListFragment newInstance(int surahNumber, String surahNameLatin) {
        AyatListFragment fragment = new AyatListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SURAH_NUMBER, surahNumber);
        args.putString(ARG_SURAH_NAME_LATIN, surahNameLatin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            surahNumber = getArguments().getInt(ARG_SURAH_NUMBER);
            surahNameLatin = getArguments().getString(ARG_SURAH_NAME_LATIN);
        }
        ayatViewModel = new ViewModelProvider(requireActivity()).get(AyatViewModel.class);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ayat_list, container, false);
        recyclerViewAyats = view.findViewById(R.id.rv_ayats_fragment);
        progressBarAyat = view.findViewById(R.id.progressBarAyatFragment);
        textViewErrorAyat = view.findViewById(R.id.textViewErrorAyatFragment);
        buttonRefreshAyat = view.findViewById(R.id.buttonRefreshAyatFragment);
        textViewSurahInfo = view.findViewById(R.id.textViewSurahInfoDetail);

        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers();
        setupEventListeners();

        if (surahNumber > 0) {
            if (ayatViewModel.getCurrentSurahNumber() != surahNumber || ayatViewModel.getAyats().getValue() == null) {
                ayatViewModel.initialLoadAyats(surahNumber);
            }
            ayatViewModel.loadTafsirForSurah(surahNumber);
        } else {
            textViewErrorAyat.setText(getString(R.string.error_surah_not_valid));
            textViewErrorAyat.setVisibility(View.VISIBLE);
            progressBarAyat.setVisibility(View.GONE);
            buttonRefreshAyat.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        // Listener untuk klik item ayat (untuk toggle tafsir)
        AyatAdapter.OnAyatClickListener ayatClickListener = (ayat, holder) -> {
            Log.d("AyatListFragment", "Ayat clicked: " + ayat.getNomorAyat());
            holder.toggleTafsirVisibility();
        };

        // Listener untuk klik tombol putar audio per ayat
        AyatAdapter.OnPlayAudioClickListener playAudioClickListener = (ayat, audioUrl) -> {
            // PERUBAHAN UTAMA DI SINI:
            // 1. Ambil preferensi qari dari SharedPreferences melalui SettingsUtils
            String preferredReciterKey = SettingsUtils.getReciterPreference(requireContext());

            // 2. Dapatkan URL audio yang benar dari model Ayat berdasarkan preferensi
            String correctAudioUrl = ayat.getAudioUrlForReciter(preferredReciterKey);

            Log.d("AyatListFragment", "Play audio for ayat: " + ayat.getNomorAyat() +
                    ". Reciter Key: " + preferredReciterKey +
                    ". Final URL: " + correctAudioUrl);

            // 3. Putar audio jika URL valid
            if (correctAudioUrl != null && !correctAudioUrl.isEmpty()) {
                playAudio(correctAudioUrl);
            } else {
                Toast.makeText(getContext(), "Audio tidak tersedia untuk qari ini.", Toast.LENGTH_SHORT).show();
            }
        };

        // Inisialisasi adapter dengan kedua listener
        ayatAdapter = new AyatAdapter(new ArrayList<>(), ayatClickListener, playAudioClickListener);

        recyclerViewAyats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAyats.setAdapter(ayatAdapter);
    }

    // Metode playAudio dan metode siklus hidup lainnya (onCreate, onDestroy, dll) tetap sama
    // ...
    private void playAudio(String url) {
        if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if (getContext() != null) Toast.makeText(getContext(), "Memutar audio...", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                if (getContext() != null) Toast.makeText(getContext(), "Audio selesai.", Toast.LENGTH_SHORT).show();
                mp.reset();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (getContext() != null) Toast.makeText(getContext(), "Gagal memutar audio.", Toast.LENGTH_SHORT).show();
                Log.e("AyatListFragment", "MediaPlayer Error: what=" + what + ", extra=" + extra + " for URL: " + url);
                mp.reset();
                return true;
            });
            mediaPlayer.prepareAsync();
        } catch (IOException | IllegalStateException | IllegalArgumentException e) {
            Log.e("AyatListFragment", "Error playing audio for URL: " + url, e);
            if (getContext() != null) Toast.makeText(getContext(), "Gagal memuat audio.", Toast.LENGTH_LONG).show();
            if (mediaPlayer != null) mediaPlayer.reset();
        }
    }

    private void setupObservers() {
        ayatViewModel.getIsLoadingAyat().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) progressBarAyat.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading != null && isLoading) {
                textViewErrorAyat.setVisibility(View.GONE);
                buttonRefreshAyat.setVisibility(View.GONE);
                recyclerViewAyats.setVisibility(View.GONE);
            }
        });

        ayatViewModel.getErrorMessageAyat().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                textViewErrorAyat.setText(errorMessage);
                textViewErrorAyat.setVisibility(View.VISIBLE);
                buttonRefreshAyat.setVisibility(View.VISIBLE);
                recyclerViewAyats.setVisibility(View.GONE);
                progressBarAyat.setVisibility(View.GONE);
            } else {
                textViewErrorAyat.setVisibility(View.GONE);
            }
        });

        ayatViewModel.getAyats().observe(getViewLifecycleOwner(), ayats -> {
            if (!isAdded() || getContext() == null) return;
            Boolean isLoading = ayatViewModel.getIsLoadingAyat().getValue();
            String errorMessage = ayatViewModel.getErrorMessageAyat().getValue();

            if (ayats != null && !ayats.isEmpty()) {
                ayatAdapter.updateAyats(ayats);
                recyclerViewAyats.setVisibility(View.VISIBLE);
                textViewErrorAyat.setVisibility(View.GONE);
                buttonRefreshAyat.setVisibility(View.GONE);
                progressBarAyat.setVisibility(View.GONE);
            } else if (isLoading != null && !isLoading) {
                if (errorMessage == null || errorMessage.isEmpty()) {
                    textViewErrorAyat.setText("Tidak ada data ayat."); // Pesan default jika kosong
                    textViewErrorAyat.setVisibility(View.VISIBLE);
                }
                if (textViewErrorAyat.getVisibility() == View.VISIBLE) buttonRefreshAyat.setVisibility(View.VISIBLE);
                recyclerViewAyats.setVisibility(View.GONE);
                progressBarAyat.setVisibility(View.GONE);
            }
        });

        ayatViewModel.getSurahDetail().observe(getViewLifecycleOwner(), surahDetail -> {
            if (surahDetail != null) {
                String info = "";
                if (surahDetail.getArti() != null && !surahDetail.getArti().isEmpty()) info += "Arti: " + surahDetail.getArti() + "\n\n";
                if (surahDetail.getDeskripsi() != null && !surahDetail.getDeskripsi().isEmpty()) info += "Deskripsi:\n" + android.text.Html.fromHtml(surahDetail.getDeskripsi()).toString();
                textViewSurahInfo.setText(info);
                textViewSurahInfo.setVisibility(View.VISIBLE);
            } else {
                textViewSurahInfo.setVisibility(View.GONE);
            }
        });

        ayatViewModel.getIsLoadingTafsir().observe(getViewLifecycleOwner(), isLoading -> {
            if(isLoading != null && isLoading) {
                // Bisa tampilkan loading global jika diinginkan
                // Toast.makeText(getContext(), "Memuat data tafsir...", Toast.LENGTH_SHORT).show();
            }
        });

        ayatViewModel.getErrorMessageTafsir().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.error_failed_load_tafsir) + ": " + error, Toast.LENGTH_LONG).show();
            }
        });

        ayatViewModel.getTafsir().observe(getViewLifecycleOwner(), tafsirData -> {
            if (tafsirData != null && tafsirData.getTafsir() != null) {
                ayatAdapter.updateTafsir(tafsirData.getTafsir());
            } else {
                ayatAdapter.updateTafsir(new ArrayList<>()); // Kirim list kosong jika tidak ada data tafsir
            }
        });
    }

    private void setupEventListeners() {
        buttonRefreshAyat.setOnClickListener(v -> {
            if (surahNumber > 0) {
                ayatViewModel.refreshAyats();
                ayatViewModel.loadTafsirForSurah(surahNumber);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
