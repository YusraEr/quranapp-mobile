package com.example.quranapp.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quranapp.R;
import com.example.quranapp.data.remote.model.Ayat;
import com.example.quranapp.data.remote.model.AyatResponse;
import com.example.quranapp.data.remote.model.Surah;
import com.example.quranapp.data.remote.model.Tafsir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AyatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_AYAT = 1;

    private List<Object> items;
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

    public AyatAdapter(List<Object> items, OnAyatClickListener listener, OnPlayAudioClickListener audioClickListener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
        this.audioClickListener = audioClickListener;
        this.tafsirMap = new HashMap<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < items.size() && items.get(position) instanceof com.example.quranapp.data.remote.model.AyatResponse.AyatData) {
            return TYPE_HEADER;
        }
        return TYPE_AYAT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_surah, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ayat, parent, false);
            return new AyatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            AyatResponse.AyatData ayatData =
                (AyatResponse.AyatData) items.get(position);
            headerHolder.bind(ayatData);
        } else {
            AyatViewHolder ayatHolder = (AyatViewHolder) holder;
            Ayat ayat = (Ayat) items.get(position);
            String tafsirTeks = tafsirMap.get(ayat.getNomorAyat());
            ayatHolder.bind(ayat, tafsirTeks, listener, audioClickListener, currentlyPlayingAyatNomor, isAudioPlaying, isAudioLoading);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<Object> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateAyats(List<Ayat> newAyatList) {
        List<Object> newItems = new ArrayList<>();
        // Keep header if exists
        if (!items.isEmpty() && items.get(0) instanceof Surah) {
            newItems.add(items.get(0));
        }
        if (newAyatList != null) {
            newItems.addAll(newAyatList);
        }
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void updateTafsir(List<Tafsir> newTafsirList) {
        tafsirMap.clear();
        if (newTafsirList != null) {
            for (Tafsir tafsirItem : newTafsirList) {
                tafsirMap.put(tafsirItem.getAyat(), tafsirItem.getTeks());
            }
        }
        notifyDataSetChanged();
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
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof Ayat && ((Ayat) items.get(i)).getNomorAyat() == nomor) {
                return i;
            }
        }
        return -1;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSurahNameHeader;
        TextView textViewSurahArabicHeader;
        TextView textViewSurahInfoDetail;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSurahNameHeader = itemView.findViewById(R.id.textViewSurahNameHeader);
            textViewSurahArabicHeader = itemView.findViewById(R.id.textViewSurahArabicHeader);
            textViewSurahInfoDetail = itemView.findViewById(R.id.textViewSurahInfoDetail);
        }

        public void bind(final com.example.quranapp.data.remote.model.AyatResponse.AyatData surahData) {
            if (surahData == null) return;

            textViewSurahNameHeader.setText(surahData.getNamaLatin());
            textViewSurahArabicHeader.setText(surahData.getNama());

            String info = "";
            if (surahData.getArti() != null && !surahData.getArti().isEmpty()) {
                info += "Arti: " + surahData.getArti() + "\n\n";
            }
            if (surahData.getDeskripsi() != null && !surahData.getDeskripsi().isEmpty()) {
                info += "Deskripsi:\n" + Html.fromHtml(surahData.getDeskripsi()).toString();
            }
            textViewSurahInfoDetail.setText(info);
        }
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

            if (ayat == null) return;

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
            }
        }

        public void toggleTafsirVisibility() {
            if (textViewTafsirAyatItem == null) return;

            if (textViewTafsirAyatItem.getVisibility() == View.VISIBLE) {
                textViewTafsirAyatItem.setVisibility(View.GONE);
            } else if (textViewTafsirAyatItem.getText() != null && !textViewTafsirAyatItem.getText().toString().isEmpty()) {
                textViewTafsirAyatItem.setVisibility(View.VISIBLE);
            }
        }
    }
}

