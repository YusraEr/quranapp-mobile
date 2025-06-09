package com.example.quranapp.fragments;

import android.content.Intent;
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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quranapp.DetailActivity;
import com.example.quranapp.R;
import com.example.quranapp.adapter.SurahAdapter;
import com.example.quranapp.data.remote.model.Surah;
import com.example.quranapp.utils.NetworkUtils;
import com.example.quranapp.utils.SettingsUtils; // Pastikan import ini ada
import com.example.quranapp.viewmodel.SurahViewModel;

import java.io.IOException;
import java.util.ArrayList;

public class SurahListFragment extends Fragment {

    private SurahViewModel surahViewModel;
    private RecyclerView recyclerViewSurahs;
    private SurahAdapter surahAdapter;
    private ProgressBar progressBarFragment;
    private TextView textViewErrorFragment;
    private Button buttonRefreshFragment;
    private SearchView searchViewSurah;

    private MediaPlayer surahMediaPlayer;
    private int currentlyPlayingSurahNomor = -1;
    private boolean isSurahAudioPlaying = false;

    public SurahListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surahViewModel = new ViewModelProvider(requireActivity()).get(SurahViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surah_list, container, false);
        recyclerViewSurahs = view.findViewById(R.id.rv_surahs_fragment);
        progressBarFragment = view.findViewById(R.id.progressBarFragment);
        textViewErrorFragment = view.findViewById(R.id.textViewErrorFragment);
        buttonRefreshFragment = view.findViewById(R.id.buttonRefreshFragment);
        searchViewSurah = view.findViewById(R.id.searchViewSurah);
        setupRecyclerView();
        setupSearchView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers();
        setupEventListeners();
        surahViewModel.initialLoadSurahs();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeMediaPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void setupSearchView() {
        searchViewSurah.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                surahViewModel.filterSurahs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                surahViewModel.filterSurahs(newText);
                return true;
            }
        });

        // Clear button listener
        searchViewSurah.setOnCloseListener(() -> {
            surahViewModel.filterSurahs("");
            return false;
        });
    }

    private void initializeMediaPlayer() {
        if (surahMediaPlayer == null) {
            surahMediaPlayer = new MediaPlayer();
            surahMediaPlayer.setOnCompletionListener(mp -> stopAndResetAudioState());
            surahMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (getContext() != null) Toast.makeText(getContext(), "Gagal memutar audio.", Toast.LENGTH_SHORT).show();
                Log.e("SurahListFragment", "MediaPlayer Error: what=" + what + ", extra=" + extra);
                stopAndResetAudioState();
                return true;
            });
        }
    }

    private void releaseMediaPlayer() {
        if (surahMediaPlayer != null) {
            if (surahMediaPlayer.isPlaying()) surahMediaPlayer.stop();
            surahMediaPlayer.release();
            surahMediaPlayer = null;
            stopAndResetAudioState();
        }
    }

    private void stopAndResetAudioState() {
        isSurahAudioPlaying = false;
        currentlyPlayingSurahNomor = -1;
        if (surahAdapter != null) {
            surahAdapter.updatePlaybackState(-1, false, false);
        }
    }

    private void setupRecyclerView() {
        surahAdapter = new SurahAdapter(new ArrayList<>(),
                surah -> { // Listener klik item surah
                    stopAndResetAudioState();
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_SURAH_NUMBER, surah.getNomor());
                    intent.putExtra(DetailActivity.EXTRA_SURAH_NAME_LATIN, surah.getNamaLatin());
                    startActivity(intent);
                },
                surah -> { // Listener klik tombol audio
                    handlePlayFullAudio(surah);
                }
        );
        recyclerViewSurahs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSurahs.setAdapter(surahAdapter);
    }

    private void setupObservers() {
        surahViewModel.filteredSurahs.observe(getViewLifecycleOwner(), surahs -> {
            if (!isAdded()) return;

            // Jika daftar tidak kosong, update adapter
            if (surahs != null && !surahs.isEmpty()) {
                surahAdapter.updateSurahs(surahs);
                recyclerViewSurahs.setVisibility(View.VISIBLE);
                textViewErrorFragment.setVisibility(View.GONE);
            }
            // Jika daftar kosong tapi tidak sedang loading, tampilkan pesan
            else if (surahViewModel.getIsLoading().getValue() != null && !surahViewModel.getIsLoading().getValue()) {
                surahAdapter.updateSurahs(new ArrayList<>()); // Kosongkan adapter
                textViewErrorFragment.setText("Surah tidak ditemukan.");
                textViewErrorFragment.setVisibility(View.VISIBLE);
                recyclerViewSurahs.setVisibility(View.GONE);
            }
        });

        surahViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading && surahAdapter.getItemCount() == 0) {
                progressBarFragment.setVisibility(View.VISIBLE);
                recyclerViewSurahs.setVisibility(View.GONE);
                textViewErrorFragment.setVisibility(View.GONE);
                buttonRefreshFragment.setVisibility(View.GONE);
            } else {
                progressBarFragment.setVisibility(View.GONE);
            }
        });

        surahViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                textViewErrorFragment.setText(errorMessage);
                textViewErrorFragment.setVisibility(View.VISIBLE);
                buttonRefreshFragment.setVisibility(View.VISIBLE);
                recyclerViewSurahs.setVisibility(View.GONE);
            } else {
                textViewErrorFragment.setVisibility(View.GONE);
                buttonRefreshFragment.setVisibility(View.GONE);
            }
        });

        surahViewModel.getFullAudioUrl().observe(getViewLifecycleOwner(), audioUrl -> {
            if (audioUrl != null && !audioUrl.isEmpty()) {
                // URL berhasil didapat, putar audio
                playNewSurahAudio(audioUrl);
            }
        });

        surahViewModel.getFullAudioError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                // Gagal mendapatkan URL, tampilkan pesan dan reset UI
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                stopAndResetAudioState();
            }
        });
    }

    private void setupEventListeners() {
        buttonRefreshFragment.setOnClickListener(v -> {
            // PERBAIKAN DI SINI:
            // Cek koneksi internet sebelum mencoba refresh.
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                // Jika ada koneksi, panggil metode refresh di ViewModel.
                surahViewModel.refreshSurahs();
            } else {
                // Jika tidak ada koneksi, beri tahu pengguna dengan Toast.
                Toast.makeText(getContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePlayFullAudio(final Surah surah) {
        initializeMediaPlayer();

        if (surah.getNomor() == currentlyPlayingSurahNomor) { // Jika surah yang sama diklik
            if (isSurahAudioPlaying) { // dan sedang bermain -> pause
                surahMediaPlayer.pause();
                isSurahAudioPlaying = false;
            } else { // dan sedang pause -> lanjutkan
                surahMediaPlayer.start();
                isSurahAudioPlaying = true;
            }
            surahAdapter.updatePlaybackState(currentlyPlayingSurahNomor, isSurahAudioPlaying, false);
        } else { // Jika surah baru diklik
            stopAndResetAudioState(); // Hentikan pemutaran sebelumnya

            // Perbarui UI untuk menunjukkan status loading
            currentlyPlayingSurahNomor = surah.getNomor();
            surahAdapter.updatePlaybackState(currentlyPlayingSurahNomor, false, true);

            // Minta ViewModel untuk mengambil URL audio
            surahViewModel.fetchFullAudioUrl(surah.getNomor());
        }
    }

    // Metode playNewSurahAudio sekarang hanya menerima URL
    private void playNewSurahAudio(String audioUrl) {
        try {
            surahMediaPlayer.reset();
            surahMediaPlayer.setDataSource(audioUrl);
            surahMediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isSurahAudioPlaying = true;
                // Update UI untuk menunjukkan status 'playing'
                surahAdapter.updatePlaybackState(currentlyPlayingSurahNomor, true, false);
            });
            surahMediaPlayer.prepareAsync();
        } catch (IOException | IllegalStateException e) {
            Log.e("SurahListFragment", "Error playing new audio", e);
            stopAndResetAudioState();
        }
    }
}

