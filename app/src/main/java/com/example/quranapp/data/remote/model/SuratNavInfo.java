package com.example.quranapp.data.remote.model; // Sesuaikan dengan package name Anda

import com.google.gson.annotations.SerializedName;

// Kelas ini digunakan untuk parsing objek suratSelanjutnya dan suratSebelumnya
// yang bisa berupa objek atau boolean 'false'.
// Gson akan menangani ini dengan baik jika field di AyatResponse.AyatData
// dideklarasikan sebagai SuratNavInfo. Jika API mengembalikan 'false', objek ini akan null.
public class SuratNavInfo {

    @SerializedName("nomor")
    private int nomor;

    @SerializedName("nama")
    private String nama; // Nama Arab

    @SerializedName("namaLatin")
    private String namaLatin;

    @SerializedName("jumlahAyat")
    private int jumlahAyat;

    // Konstruktor default
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
