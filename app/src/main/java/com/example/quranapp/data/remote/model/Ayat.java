package com.example.quranapp.data.remote.model; // Sesuaikan dengan package name Anda

import android.text.Spanned; // Import Spanned

import com.example.quranapp.utils.SettingsUtils;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class Ayat {

    @SerializedName("nomorAyat")
    private int nomorAyat;

    @SerializedName("teksArab")
    private String teksArab;

    @SerializedName("teksLatin")
    private String teksLatin; // Tetap simpan string HTML asli

    @SerializedName("teksIndonesia")
    private String teksIndonesia;

    @SerializedName("audio")
    private Map<String, String> audio;

    // Field baru untuk menyimpan hasil parsing Html.fromHtml()
    // transient agar tidak ikut di-serialize/deserialize oleh Gson jika tidak diinginkan
    // atau biarkan saja jika tidak masalah (Gson akan mengabaikannya jika tidak ada di JSON)
    private transient Spanned teksLatinSpanned;

    // Konstruktor default
    public Ayat() {
    }

    // Getter dan Setter
    public int getNomorAyat() {
        return nomorAyat;
    }

    public void setNomorAyat(int nomorAyat) {
        this.nomorAyat = nomorAyat;
    }

    public String getTeksArab() {
        return teksArab;
    }

    public void setTeksArab(String teksArab) {
        this.teksArab = teksArab;
    }

    public String getTeksLatin() {
        return teksLatin;
    }

    public void setTeksLatin(String teksLatin) {
        this.teksLatin = teksLatin;
    }

    public String getTeksIndonesia() {
        return teksIndonesia;
    }

    public void setTeksIndonesia(String teksIndonesia) {
        this.teksIndonesia = teksIndonesia;
    }

    public Map<String, String> getAudio() {
        return audio;
    }

    public void setAudio(Map<String, String> audio) {
        this.audio = audio;
    }

    // Getter dan Setter untuk teksLatinSpanned
    public Spanned getTeksLatinSpanned() {
        return teksLatinSpanned;
    }

    public void setTeksLatinSpanned(Spanned teksLatinSpanned) {
        this.teksLatinSpanned = teksLatinSpanned;
    }


    public String getPreferredAudioUrl() {
        if (audio != null) {
            if (audio.containsKey("01") && audio.get("01") != null && !audio.get("01").isEmpty()) {
                return audio.get("01");
            }
            if (audio.containsKey("05") && audio.get("05") != null && !audio.get("05").isEmpty()) {
                return audio.get("05");
            }
            for (String url : audio.values()) {
                if (url != null && !url.isEmpty()) {
                    return url;
                }
            }
        }
        return null;
    }
    public String getAudioUrlForReciter(String reciterKey) {
        if (audio == null || audio.isEmpty()) {
            return null;
        }

        // 1. Coba dapatkan URL untuk qari pilihan
        if (audio.containsKey(reciterKey)) {
            return audio.get(reciterKey);
        }

        // 2. Fallback jika qari pilihan tidak tersedia: Coba qari default (Misyari)
        if (audio.containsKey(SettingsUtils.DEFAULT_RECITER_KEY)) {
            return audio.get(SettingsUtils.DEFAULT_RECITER_KEY);
        }

        // 3. Fallback terakhir: Kembalikan URL pertama yang tersedia
        return audio.values().iterator().next();
    }
}
