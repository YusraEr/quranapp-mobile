package com.example.quranapp.adapter;

import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quranapp.R;
import com.example.quranapp.data.remote.model.Ayat;
import com.example.quranapp.data.remote.model.Tafsir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.AyatViewHolder> {

    private List<Ayat> ayatList;
    private Map<Integer, String> tafsirMap;
    private final OnAyatClickListener listener;
    private final OnPlayAudioClickListener audioClickListener;

    private int currentlyPlayingAyatNomor = -1;
    private boolean isAudioPlaying = false;
    private boolean isAudioLoading = false;

    // Interface untuk klik pada item ayat (untuk toggle tafsir)
    public interface OnAyatClickListener {
        void onAyatClick(Ayat ayat, AyatViewHolder holder);
    }

    // Interface untuk klik pada tombol putar audio
    public interface OnPlayAudioClickListener {
        void onPlayAudioClick(Ayat ayat);
    }

    public AyatAdapter(List<Ayat> ayatList, OnAyatClickListener listener, OnPlayAudioClickListener audioClickListener) {
        this.ayatList = ayatList;
        this.listener = listener;
        this.audioClickListener = audioClickListener;
        this.tafsirMap = new HashMap<>();
    }

    @NonNull
    @Override
    public AyatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ayat, parent, false);
        return new AyatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AyatViewHolder holder, int position) {
        Ayat currentAyat = ayatList.get(position);
        String tafsirTeks = tafsirMap.get(currentAyat.getNomorAyat());
        holder.bind(currentAyat, tafsirTeks, listener, audioClickListener, currentlyPlayingAyatNomor, isAudioPlaying, isAudioLoading);
    }

    @Override
    public int getItemCount() {
        return ayatList == null ? 0 : ayatList.size();
    }

    public void updateAyats(List<Ayat> newAyatList) {
        this.ayatList.clear();
        if (newAyatList != null) {
            this.ayatList.addAll(newAyatList);
        }
        notifyDataSetChanged();
    }

    public void updateTafsir(List<Tafsir> newTafsirList) {
        tafsirMap.clear();
        if (newTafsirList != null) {
            for (Tafsir tafsirItem : newTafsirList) {
                tafsirMap.put(tafsirItem.getAyat(), tafsirItem.getTeks());
            }
        }
    }

    /**
     * Metode untuk mengupdate status playback dari Fragment.
     * Adapter akan me-refresh item yang relevan untuk menampilkan ikon yang benar.
     * @param playingAyatNomor Nomor ayat yang sedang diputar/dimuat, atau -1 jika tidak ada.
     * @param isPlaying True jika audio sedang berjalan.
     * @param isLoading True jika audio sedang dalam proses persiapan.
     */
    public void updatePlaybackState(int playingAyatNomor, boolean isPlaying, boolean isLoading) {
        int oldPlayingPosition = findPositionByNomor(this.currentlyPlayingAyatNomor);
        int newPlayingPosition = findPositionByNomor(playingAyatNomor);

        this.currentlyPlayingAyatNomor = playingAyatNomor;
        this.isAudioPlaying = isPlaying;
        this.isAudioLoading = isLoading;

        // Refresh item lama (untuk menghilangkan ikon pause/loading)
        if (oldPlayingPosition != -1) {
            notifyItemChanged(oldPlayingPosition);
        }
        // Refresh item baru (untuk menampilkan ikon pause/loading)
        if (newPlayingPosition != -1 && newPlayingPosition != oldPlayingPosition) {
            notifyItemChanged(newPlayingPosition);
        } else if (newPlayingPosition != -1) {
            // Jika item yang sama di-klik (misalnya, untuk pause), refresh juga
            notifyItemChanged(newPlayingPosition);
        }
    }

    private int findPositionByNomor(int nomor) {
        if (nomor == -1) return -1;
        for (int i = 0; i < ayatList.size(); i++) {
            if (ayatList.get(i).getNomorAyat() == nomor) {
                return i;
            }
        }
        return -1;
    }


    public static class AyatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNomorAyat;
        TextView textViewTeksArab;
        TextView textViewTeksLatin;
        TextView textViewTeksIndonesia;
        public TextView textViewTafsirAyatItem;
        ImageButton buttonPlayAudioAyat;

        public AyatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNomorAyat = itemView.findViewById(R.id.textViewNomorAyatItem);
            textViewTeksArab = itemView.findViewById(R.id.textViewTeksArabItem);
            textViewTeksLatin = itemView.findViewById(R.id.textViewTeksLatinItem);
            textViewTeksIndonesia = itemView.findViewById(R.id.textViewTeksIndonesiaItem);
            buttonPlayAudioAyat = itemView.findViewById(R.id.buttonPlayAudioAyat);
            textViewTafsirAyatItem = itemView.findViewById(R.id.textViewTafsirAyatItem);
        }

        public void bind(final Ayat ayat, final String tafsirTeks, final OnAyatClickListener clickListener,
                         final OnPlayAudioClickListener audioClickListener,
                         int currentlyPlayingAyatNomor, boolean isAudioPlaying, boolean isAudioLoading) {

            textViewNomorAyat.setText(String.format(Locale.getDefault(), "%d", ayat.getNomorAyat()));
            textViewTeksArab.setText(ayat.getTeksArab());

            if (ayat.getTeksLatinSpanned() != null) {
                textViewTeksLatin.setText(ayat.getTeksLatinSpanned());
            } else if (ayat.getTeksLatin() != null) {
                textViewTeksLatin.setText(ayat.getTeksLatin());
            } else {
                textViewTeksLatin.setText("");
            }

            textViewTeksIndonesia.setText(ayat.getTeksIndonesia());

            if (tafsirTeks != null && !tafsirTeks.isEmpty()) {
                textViewTafsirAyatItem.setText(tafsirTeks);
            } else {
                textViewTafsirAyatItem.setText("");
                textViewTafsirAyatItem.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> clickListener.onAyatClick(ayat, this));

            if (ayat.getAudio() != null && !ayat.getAudio().isEmpty()) {
                buttonPlayAudioAyat.setVisibility(View.VISIBLE);
                buttonPlayAudioAyat.setOnClickListener(v -> audioClickListener.onPlayAudioClick(ayat));

                // Logika untuk menampilkan 1 dari 3 ikon: loading, playing, atau paused/stopped
                if (ayat.getNomorAyat() == currentlyPlayingAyatNomor) {
                    if (isAudioLoading) {
                        buttonPlayAudioAyat.setImageResource(R.drawable.ic_hourglass_empty);
                    } else if (isAudioPlaying) {
                        buttonPlayAudioAyat.setImageResource(R.drawable.ic_pause_circle);
                    } else { // Paused
                        buttonPlayAudioAyat.setImageResource(R.drawable.ic_play_arrow);
                    }
                } else { // Bukan ayat yang sedang diputar
                    buttonPlayAudioAyat.setImageResource(R.drawable.ic_play_arrow);
                }
            } else {
                buttonPlayAudioAyat.setVisibility(View.GONE);
            }
        }

        public void toggleTafsirVisibility() {
            if (textViewTafsirAyatItem.getText() != null && !textViewTafsirAyatItem.getText().toString().isEmpty()) {
                textViewTafsirAyatItem.setVisibility(
                        textViewTafsirAyatItem.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
                );
            }
        }
    }
}
