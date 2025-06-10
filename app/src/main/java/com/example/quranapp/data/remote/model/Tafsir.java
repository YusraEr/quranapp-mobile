package com.example.quranapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class Tafsir {

    @SerializedName("ayat")
    private int ayat;

    @SerializedName("teks")
    private String teks;

    public Tafsir() {
    }

    public int getAyat() {
        return ayat;
    }

    public void setAyat(int ayat) {
        this.ayat = ayat;
    }

    public String getTeks() {
        return teks;
    }

    public void setTeks(String teks) {
        this.teks = teks;
    }
}
