package com.example.quranapp.data.remote.model;

import android.text.Spanned;

public class AyatSearchResult {
    private int surahNumber;
    private String surahNameLatin;
    private int ayatNumber;
    private String ayatTextArab;
    private String ayatTranslation;
    private Spanned spannedTranslation;

    public int getSurahNumber() {
        return surahNumber;
    }

    public void setSurahNumber(int surahNumber) {
        this.surahNumber = surahNumber;
    }

    public String getSurahNameLatin() {
        return surahNameLatin;
    }

    public void setSurahNameLatin(String surahNameLatin) {
        this.surahNameLatin = surahNameLatin;
    }

    public int getAyatNumber() {
        return ayatNumber;
    }

    public void setAyatNumber(int ayatNumber) {
        this.ayatNumber = ayatNumber;
    }

    public String getAyatTextArab() {
        return ayatTextArab;
    }

    public void setAyatTextArab(String ayatTextArab) {
        this.ayatTextArab = ayatTextArab;
    }

    public String getAyatTranslation() {
        return ayatTranslation;
    }

    public void setAyatTranslation(String ayatTranslation) {
        this.ayatTranslation = ayatTranslation;
    }

    public Spanned getSpannedTranslation() {
        return spannedTranslation;
    }

    public void setSpannedTranslation(Spanned spannedTranslation) {
        this.spannedTranslation = spannedTranslation;
    }

    public AyatSearchResult(int surahNumber, String surahNameLatin, int ayatNumber, String ayatTextArab, String ayatTranslation) {
        this.surahNumber = surahNumber;
        this.surahNameLatin = surahNameLatin;
        this.ayatNumber = ayatNumber;
        this.ayatTextArab = ayatTextArab;
        this.ayatTranslation = ayatTranslation;
    }
}
