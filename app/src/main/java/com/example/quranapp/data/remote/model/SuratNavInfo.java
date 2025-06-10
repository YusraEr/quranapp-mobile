package com.example.quranapp.data.remote.model;

import com.google.gson.annotations.SerializedName;
public class SuratNavInfo {

    @SerializedName("nomor")
    private int nomor;

    @SerializedName("nama")
    private String nama;

    @SerializedName("namaLatin")
    private String namaLatin;

    @SerializedName("jumlahAyat")
    private int jumlahAyat;

    public SuratNavInfo() {
    }

    // Getter dan Setter
    public int getNomor() {
        return nomor;
    }

    public void setNomor(int nomor) {
        this.nomor = nomor;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNamaLatin() {
        return namaLatin;
    }

    public void setNamaLatin(String namaLatin) {
        this.namaLatin = namaLatin;
    }

    public int getJumlahAyat() {
        return jumlahAyat;
    }

    public void setJumlahAyat(int jumlahAyat) {
        this.jumlahAyat = jumlahAyat;
    }
}
