package com.example.quranapp.data.remote.model; // Sesuaikan dengan package name Anda

import com.google.gson.annotations.SerializedName;

public class Tafsir {

    @SerializedName("ayat")
    private int ayat; // Nomor ayat yang ditafsirkan

    @SerializedName("teks")
    private String teks; // Isi tafsir

    // Konstruktor default
    public Tafsir() {
    }

    // Getter dan Setter
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
