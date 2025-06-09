package com.example.quranapp.adapter;

// import android.text.Html; // Tidak lagi digunakan di bind jika teksLatinSpanned ada
import android.text.Spanned; // Import Spanned
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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;


public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.AyatViewHolder> {

    private List<Ayat> ayatList;
    private Map<Integer, String> tafsirMap;
    private final OnAyatClickListener listener;
    private final OnPlayAudioClickListener audioClickListener;

    public interface OnAyatClickListener {
        void onAyatClick(Ayat ayat, AyatViewHolder holder);
    }

    public interface OnPlayAudioClickListener {
        void onPlayAudioClick(Ayat ayat, String audioUrl);
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
        holder.bind(currentAyat, tafsirTeks, listener, audioClickListener);
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
        notifyDataSetChanged();
    }

    public static class AyatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNomorAyat;
        TextView textViewTeksArab;
        TextView textViewTeksLatin;
        TextView textViewTeksIndonesia;
        ImageButton buttonPlayAudioAyat;
        public TextView textViewTafsirAyatItem;

        public AyatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNomorAyat = itemView.findViewById(R.id.textViewNomorAyatItem);
            textViewTeksArab = itemView.findViewById(R.id.textViewTeksArabItem);
            textViewTeksLatin = itemView.findViewById(R.id.textViewTeksLatinItem);
            textViewTeksIndonesia = itemView.findViewById(R.id.textViewTeksIndonesiaItem);
            buttonPlayAudioAyat = itemView.findViewById(R.id.buttonPlayAudioAyat);
            textViewTafsirAyatItem = itemView.findViewById(R.id.textViewTafsirAyatItem);
        }

        public void bind(final Ayat ayat, final String tafsirTeks, final OnAyatClickListener clickListener, final OnPlayAudioClickListener audioClickListener) {
            textViewNomorAyat.setText(String.format(Locale.getDefault(), "%d", ayat.getNomorAyat()));
            textViewTeksArab.setText(ayat.getTeksArab());

            // Gunakan teksLatinSpanned jika tersedia, jika tidak, fallback ke teksLatin asli (tanpa parsing HTML di sini)
            if (ayat.getTeksLatinSpanned() != null) {
                textViewTeksLatin.setText(ayat.getTeksLatinSpanned());
            } else if (ayat.getTeksLatin() != null) {
                // Fallback jika teksLatinSpanned belum diproses (seharusnya tidak terjadi jika repo benar)
                // Atau jika Anda memutuskan untuk tidak mem-parse beberapa teksLatin
                textViewTeksLatin.setText(ayat.getTeksLatin());
            } else {
                textViewTeksLatin.setText("");
            }

            textViewTeksIndonesia.setText(ayat.getTeksIndonesia());

            // Improved handling for tafsir text
            if (tafsirTeks != null && !tafsirTeks.isEmpty()) {
                textViewTafsirAyatItem.setText(tafsirTeks);
                // Initially hide tafsir, will be toggled on click
                textViewTafsirAyatItem.setVisibility(View.GONE);
            } else {
                textViewTafsirAyatItem.setText("Tafsir tidak tersedia");
                textViewTafsirAyatItem.setVisibility(View.GONE);
            }

            // Set click listener on the entire item view to toggle tafsir
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onAyatClick(ayat, this);
                }
            });

            final String preferredAudioUrl = ayat.getPreferredAudioUrl();
            if (preferredAudioUrl != null && !preferredAudioUrl.isEmpty()) {
                buttonPlayAudioAyat.setVisibility(View.VISIBLE);

                buttonPlayAudioAyat.setOnClickListener(v -> audioClickListener.onPlayAudioClick(ayat, preferredAudioUrl));
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
