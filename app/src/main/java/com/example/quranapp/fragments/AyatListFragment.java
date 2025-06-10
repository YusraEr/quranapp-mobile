package com.example.quranapp.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.quranapp.utils.SettingsUtils;
import com.example.quranapp.viewmodel.AyatViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AyatListFragment extends Fragment {

    private static final String ARG_SURAH_NUMBER = "arg_surah_number";
    private static final String ARG_SURAH_NAME_LATIN = "arg_surah_name_latin";

    private int surahNumber;
    private AyatViewModel ayatViewModel;
    private RecyclerView recyclerViewAyats;
    private AyatAdapter ayatAdapter;

    private MediaPlayer mediaPlayer;
    private int currentlyPlayingAyatNomor = -1;
    private boolean isAudioPlaying = false;
    private boolean isManuallyStopped = true;
    private List<Ayat> currentAyatList = new ArrayList<>();

    // Variabel UI
    private ProgressBar progressBarAyat;
    private TextView textViewErrorAyat, textViewSurahNameHeader, textViewSurahArabicHeader;
    private Button buttonRefreshAyat;
    private TextView textViewSurahInfo;
    private ImageView imageViewError;

    public AyatListFragment() {

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
        }
        ayatViewModel = new ViewModelProvider(requireActivity()).get(AyatViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ayat_list, container, false);
        recyclerViewAyats = view.findViewById(R.id.rv_ayats_fragment);
        progressBarAyat = view.findViewById(R.id.progressBarAyatFragment);
        textViewErrorAyat = view.findViewById(R.id.textViewErrorAyatFragment);
        buttonRefreshAyat = view.findViewById(R.id.buttonRefreshAyatFragment);
        textViewSurahInfo = view.findViewById(R.id.textViewSurahInfoDetail);
        textViewSurahNameHeader = view.findViewById(R.id.textViewSurahNameHeader);
        textViewSurahArabicHeader = view.findViewById(R.id.textViewSurahArabicHeader);
        imageViewError = view.findViewById(R.id.imageViewError);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers();
        setupEventListeners();
        if (surahNumber > 0) {
            ayatViewModel.initialLoadAyats(surahNumber);
            ayatViewModel.loadTafsirForSurah(surahNumber);
        }
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

    private void initializeMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(mp -> {
                if (!isManuallyStopped) {
                    playNextAyat();
                } else {
                    stopAndResetAudioState();
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (getContext() != null) Toast.makeText(getContext(), "Gagal memutar audio.", Toast.LENGTH_SHORT).show();
                stopAndResetAudioState();
                return true;
            });
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopAndResetAudioState();
    }

    private void stopAndResetAudioState() {
        isAudioPlaying = false;
        isManuallyStopped = true;
        currentlyPlayingAyatNomor = -1;
        if (ayatAdapter != null) {
            ayatAdapter.updatePlaybackState(-1, false, false);
        }
    }

    private void setupRecyclerView() {

        AyatAdapter.OnAyatClickListener ayatClickListener = (ayat, holder) -> {
            Log.d("AyatListFragment", "Ayat clicked: " + ayat.getNomorAyat());
            holder.toggleTafsirVisibility();
        };

        AyatAdapter.OnPlayAudioClickListener playAudioClickListener = this::handlePlayAudioClick;

        ayatAdapter = new AyatAdapter(new ArrayList<>(), ayatClickListener, playAudioClickListener);

        recyclerViewAyats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAyats.setAdapter(ayatAdapter);
    }

    private void setupObservers() {
        ayatViewModel.getIsLoadingAyat().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading && ayatAdapter.getItemCount() == 0) {
                progressBarAyat.setVisibility(View.VISIBLE);
                recyclerViewAyats.setVisibility(View.GONE);
                textViewErrorAyat.setVisibility(View.GONE);
                buttonRefreshAyat.setVisibility(View.GONE);
                imageViewError.setVisibility(View.GONE);
            } else {
                progressBarAyat.setVisibility(View.GONE);
            }
        });

        ayatViewModel.getErrorMessageAyat().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                textViewErrorAyat.setText(errorMessage);
                textViewErrorAyat.setVisibility(View.VISIBLE);
                buttonRefreshAyat.setVisibility(View.VISIBLE);
                imageViewError.setVisibility(View.VISIBLE);
                recyclerViewAyats.setVisibility(View.GONE);
            } else {
                textViewErrorAyat.setVisibility(View.GONE);
                buttonRefreshAyat.setVisibility(View.GONE);
                imageViewError.setVisibility(View.GONE);
            }
        });

        ayatViewModel.getAyats().observe(getViewLifecycleOwner(), ayats -> {
            if (ayats != null && !ayats.isEmpty()) {
                currentAyatList.clear();
                currentAyatList.addAll(ayats);
                ayatAdapter.updateAyats(ayats);
                recyclerViewAyats.setVisibility(View.VISIBLE);
            } else {
                recyclerViewAyats.setVisibility(View.GONE);
            }
        });

        ayatViewModel.getSurahDetail().observe(getViewLifecycleOwner(), surahDetail -> {
            if (surahDetail != null) {
                String info = "";
                if (surahDetail.getArti() != null && !surahDetail.getArti().isEmpty()) info += "Arti: " + surahDetail.getArti() + "\n\n";
                if (surahDetail.getDeskripsi() != null && !surahDetail.getDeskripsi().isEmpty()) info += "Deskripsi:\n" + android.text.Html.fromHtml(surahDetail.getDeskripsi()).toString();
                textViewSurahInfo.setText(info);
                textViewSurahArabicHeader.setText(surahDetail.getNama());
                textViewSurahNameHeader.setText(surahDetail.getNamaLatin());
                textViewSurahInfo.setVisibility(View.VISIBLE);
            } else {
                textViewSurahInfo.setVisibility(View.GONE);
            }
        });

        ayatViewModel.getTafsir().observe(getViewLifecycleOwner(), tafsirData -> {
            if (tafsirData != null && tafsirData.getTafsir() != null) {
                ayatAdapter.updateTafsir(tafsirData.getTafsir());
            } else {
                ayatAdapter.updateTafsir(new ArrayList<>());
            }
        });
    }

    private void setupEventListeners() {
        buttonRefreshAyat.setOnClickListener(v -> {
            if (surahNumber > 0) {
                ayatViewModel.refreshAyats();
            }
        });
    }

    private void handlePlayAudioClick(final Ayat ayat) {
        initializeMediaPlayer();

        if (ayat.getNomorAyat() == currentlyPlayingAyatNomor) {
            if (isAudioPlaying) {
                mediaPlayer.pause();
                isAudioPlaying = false;
                isManuallyStopped = true;
            } else {
                mediaPlayer.start();
                isAudioPlaying = true;
                isManuallyStopped = false;
            }
            ayatAdapter.updatePlaybackState(currentlyPlayingAyatNomor, isAudioPlaying, false);
        } else {
            isManuallyStopped = false;
            playNewAyat(ayat);
        }
    }

    private void playNewAyat(final Ayat ayat) {
        String reciterKey = SettingsUtils.getReciterPreference(requireContext());
        String audioUrl = ayat.getAudioUrlForReciter(reciterKey);

        if (audioUrl == null || audioUrl.isEmpty()) {
            Toast.makeText(getContext(), "Audio tidak tersedia.", Toast.LENGTH_SHORT).show();
            if (!isManuallyStopped) playNextAyat();
            return;
        }

        try {
            currentlyPlayingAyatNomor = ayat.getNomorAyat();
            ayatAdapter.updatePlaybackState(currentlyPlayingAyatNomor, false, true);

            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isAudioPlaying = true;
                ayatAdapter.updatePlaybackState(currentlyPlayingAyatNomor, true, false); // Tampilkan pause
            });
            mediaPlayer.prepareAsync();
        } catch (IOException | IllegalStateException e) {
            Log.e("AyatListFragment", "Error playing new audio", e);
            stopAndResetAudioState();
        }
    }

    private void playNextAyat() {
        int currentIdx = -1;
        for (int i = 0; i < currentAyatList.size(); i++) {
            if (currentAyatList.get(i).getNomorAyat() == currentlyPlayingAyatNomor) {
                currentIdx = i;
                break;
            }
        }

        if (currentIdx != -1 && currentIdx < currentAyatList.size() - 1) {
            Ayat nextAyat = currentAyatList.get(currentIdx + 1);
            playNewAyat(nextAyat);
        } else {
            Toast.makeText(getContext(), "Selesai.", Toast.LENGTH_SHORT).show();
            stopAndResetAudioState();
        }
    }
}
