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
    private String teksLatin;

    @SerializedName("teksIndonesia")
    private String teksIndonesia;

    @SerializedName("audio")
    private Map<String, String> audio;

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

    public Spanned getTeksLatinSpanned() {
        return teksLatinSpanned;
    }

    public void setTeksLatinSpanned(Spanned teksLatinSpanned) {
        this.teksLatinSpanned = teksLatinSpanned;
    }

    public String getAudioUrlForReciter(String reciterKey) {
        if (audio == null || audio.isEmpty()) {
            return null;
        }
        if (audio.containsKey(reciterKey)) {
            return audio.get(reciterKey);
        }
        if (audio.containsKey(SettingsUtils.DEFAULT_RECITER_KEY)) {
            return audio.get(SettingsUtils.DEFAULT_RECITER_KEY);
        }
        return audio.values().iterator().next();
    }
}
