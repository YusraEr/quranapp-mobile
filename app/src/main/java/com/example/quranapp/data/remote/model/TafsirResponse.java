package com.example.quranapp.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TafsirResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private TafsirData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TafsirData getData() {
        return data;
    }

    public void setData(TafsirData data) {
        this.data = data;
    }

    public static class TafsirData {
        @SerializedName("nomor")
        private int nomor;

        @SerializedName("nama")
        private String nama;

        @SerializedName("namaLatin")
        private String namaLatin;

        @SerializedName("jumlahAyat")
        private int jumlahAyat;

        @SerializedName("tempatTurun")
        private String tempatTurun;

        @SerializedName("arti")
        private String arti;

        @SerializedName("deskripsi")
        private String deskripsi;

        @SerializedName("audioFull")
        private Surah.AudioUrl audioFull;

        @SerializedName("tafsir")
        private List<Tafsir> tafsir;

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

        public String getTempatTurun() {
            return tempatTurun;
        }

        public void setTempatTurun(String tempatTurun) {
            this.tempatTurun = tempatTurun;
        }

        public String getArti() {
            return arti;
        }

        public void setArti(String arti) {
            this.arti = arti;
        }

        public String getDeskripsi() {
            return deskripsi;
        }

        public void setDeskripsi(String deskripsi) {
            this.deskripsi = deskripsi;
        }

        public Surah.AudioUrl getAudioFull() {
            return audioFull;
        }

        public void setAudioFull(Surah.AudioUrl audioFull) {
            this.audioFull = audioFull;
        }

        public List<Tafsir> getTafsir() {
            return tafsir;
        }

        public void setTafsir(List<Tafsir> tafsir) {
            this.tafsir = tafsir;
        }
    }
}
