package com.example.quranapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quranapp.R;
import com.example.quranapp.data.remote.model.Surah;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.SurahViewHolder> {

    private List<Surah> surahList;
    private final OnSurahClickListener listener;
    private final OnPlayFullAudioClickListener audioClickListener;

    private int currentlyPlayingSurahNomor = -1;
    private boolean isAudioPlaying = false;
    private boolean isAudioLoading = false;

    public interface OnSurahClickListener {
        void onSurahClick(Surah surah);
    }

    public interface OnPlayFullAudioClickListener {
        void onPlayFullAudioClick(Surah surah);
    }

    public SurahAdapter(List<Surah> surahList, OnSurahClickListener listener, OnPlayFullAudioClickListener audioClickListener) {
        this.surahList = surahList != null ? surahList : new ArrayList<>();
        this.listener = listener;
        this.audioClickListener = audioClickListener;
    }

    @NonNull
    @Override
    public SurahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surah, parent, false);
        return new SurahViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurahViewHolder holder, int position) {
        Surah currentSurah = surahList.get(position);
        holder.bind(currentSurah, listener, audioClickListener, currentlyPlayingSurahNomor, isAudioPlaying, isAudioLoading);
    }

    @Override
    public int getItemCount() {
        return surahList.size();
    }

    public void updateSurahs(List<Surah> newSurahList) {
        this.surahList.clear();
        if (newSurahList != null) {
            this.surahList.addAll(newSurahList);
        }
        notifyDataSetChanged();
    }

    public void updatePlaybackState(int playingSurahNomor, boolean isPlaying, boolean isLoading) {
        int oldPlayingPosition = findPositionByNomor(this.currentlyPlayingSurahNomor);
        int newPlayingPosition = findPositionByNomor(playingSurahNomor);

        this.currentlyPlayingSurahNomor = playingSurahNomor;
        this.isAudioPlaying = isPlaying;
        this.isAudioLoading = isLoading;

        if (oldPlayingPosition != -1) notifyItemChanged(oldPlayingPosition);
        if (newPlayingPosition != -1) notifyItemChanged(newPlayingPosition);
    }

    private int findPositionByNomor(int nomor) {
        if (nomor == -1) return -1;
        for (int i = 0; i < surahList.size(); i++) {
            if (surahList.get(i).getNomor() == nomor) return i;
        }
        return -1;
    }

    static class SurahViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSurahNumber, textViewSurahName, textViewSurahNameArabic, textViewSurahTranslation, textViewSurahType;
        ImageButton buttonPlayFullAudioSurah;

        public SurahViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSurahNumber = itemView.findViewById(R.id.textViewSurahNumber);
            textViewSurahName = itemView.findViewById(R.id.textViewSurahName);
            textViewSurahNameArabic = itemView.findViewById(R.id.textViewSurahNameArabic);
            textViewSurahTranslation = itemView.findViewById(R.id.textViewSurahTranslation);
            textViewSurahType = itemView.findViewById(R.id.textViewSurahType);
            buttonPlayFullAudioSurah = itemView.findViewById(R.id.buttonPlayFullAudioSurah);
        }

        public void bind(final Surah surah, final OnSurahClickListener clickListener,
                         final OnPlayFullAudioClickListener audioClickListener,
                         int currentlyPlayingSurahNomor, boolean isAudioPlaying, boolean isAudioLoading) {

            if (surah == null) return;

            textViewSurahNumber.setText(String.valueOf(surah.getNomor()));

            textViewSurahName.setText(surah.getNamaLatin() != null ? surah.getNamaLatin() : "");

            textViewSurahNameArabic.setText(surah.getNama() != null ? surah.getNama() : "");

            String info = String.format(Locale.getDefault(), "%s • %d Ayat",
                surah.getArti() != null ? surah.getArti() : "",
                surah.getJumlahAyat());
            textViewSurahTranslation.setText(info);

            textViewSurahType.setText(surah.getTempatTurun() != null ? surah.getTempatTurun() : "");

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onSurahClick(surah);
                }
            });

            if (buttonPlayFullAudioSurah != null) {
                buttonPlayFullAudioSurah.setVisibility(View.VISIBLE);

                buttonPlayFullAudioSurah.setOnClickListener(v -> {
                    if (audioClickListener != null) {
                        audioClickListener.onPlayFullAudioClick(surah);
                    }
                });

                updateAudioButtonState(surah.getNomor(), currentlyPlayingSurahNomor, isAudioPlaying, isAudioLoading);
            }
        }

        private void updateAudioButtonState(int surahNomor, int playingSurahNomor, boolean isPlaying, boolean isLoading) {
            if (buttonPlayFullAudioSurah == null) return;

            if (surahNomor == playingSurahNomor) {
                if (isLoading) {
                    buttonPlayFullAudioSurah.setImageResource(R.drawable.ic_hourglass_empty);
                    buttonPlayFullAudioSurah.setContentDescription("Sedang memuat audio");
                } else if (isPlaying) {
                    buttonPlayFullAudioSurah.setImageResource(R.drawable.ic_pause_circle);
                    buttonPlayFullAudioSurah.setContentDescription("Jeda audio");
                } else {
                    buttonPlayFullAudioSurah.setImageResource(R.drawable.ic_play_arrow);
                    buttonPlayFullAudioSurah.setContentDescription("Putar audio");
                }
            } else {
                buttonPlayFullAudioSurah.setImageResource(R.drawable.ic_play_arrow);
                buttonPlayFullAudioSurah.setContentDescription("Putar audio");
            }
        }
    }
}
